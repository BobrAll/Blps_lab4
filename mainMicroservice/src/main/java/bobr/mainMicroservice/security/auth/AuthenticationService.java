package bobr.mainMicroservice.security.auth;

import bobr.mainMicroservice.camunda.ExternalTaskClientService;
import bobr.mainMicroservice.exceptions.registration.UserAlreadyRegisteredException;
import bobr.mainMicroservice.security.jwt.JwtService;
import bobr.mainMicroservice.user.Role;
import bobr.mainMicroservice.user.User;
import bobr.mainMicroservice.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ExternalTaskClientService externalTaskClientService;

    private HashMap<String, String> jwts = new HashMap<>();

    @PostConstruct
    public void addCamundaTaskHandlers() {
        externalTaskClientService.addHandlerToTopic("register-user", this::registerHandler);
        externalTaskClientService.addHandlerToTopic("login-user", this::loginHandler);
        externalTaskClientService.addHandlerToTopic("find-user", this::findUser);
    }

    private void findUser(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String camundaLogin = externalTask.getVariable("currentUser").toString();

        String jwt = jwts.get(camundaLogin);
        jwt = (jwt == null || !jwtService.isTokenValid(jwt)) ? "" : jwt;

        externalTaskService.complete(externalTask, Collections.singletonMap("jwt", jwt));
    }

    private void registerHandler(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<String, Object> vars = externalTask.getAllVariables();

        RegisterRequest request = RegisterRequest.builder()
                .firstname(vars.get("firstname").toString())
                .lastname(vars.get("lastname").toString())
                .email(vars.get("email").toString())
                .login(vars.get("login").toString())
                .password(vars.get("password").toString())
                .build();

        try {
            String jwt = register(request).getToken();
            jwts.put(externalTask.getVariable("currentUser").toString(), jwt);

            externalTaskService.complete(externalTask, Collections.singletonMap("jwt", jwt));
        } catch (Exception e) {
            externalTaskService.handleBpmnError(externalTask, "registerError");
        }

    }

    private void loginHandler(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<String, Object> vars = externalTask.getAllVariables();

        AuthenticationRequest request = AuthenticationRequest.builder()
                .login(vars.get("login").toString())
                .password(vars.get("password").toString())
                .build();

        try {
            String jwt = authenticate(request).getToken();
            externalTaskService.complete(externalTask, Collections.singletonMap("jwt", jwt));
        } catch (Exception e) {
            externalTaskService.handleBpmnError(externalTask, "loginError");
        }
    }

    public AuthenticationResponse register(RegisterRequest request) {
        if (userService.findByEmail(request.getEmail()).isPresent())
            throw new UserAlreadyRegisteredException("A user with this email already exists");
        if (userService.findByLogin(request.getLogin()).isPresent())
            throw new UserAlreadyRegisteredException("A user with this login already exists");

        var user = User.builder()
                .login(request.getLogin())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .build();

        userService.save(user);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        var user = userService.findByLogin(request.getLogin()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

}
