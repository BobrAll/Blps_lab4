package bobr.mainMicroservice.camunda;

import bobr.mainMicroservice.security.auth.AuthenticationRequest;
import bobr.mainMicroservice.security.auth.AuthenticationService;
import bobr.mainMicroservice.security.auth.RegisterRequest;
import bobr.mainMicroservice.security.jwt.JwtService;
import bobr.mainMicroservice.user.User;
import bobr.mainMicroservice.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationExternalService {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationService authService;
    private final ExternalTaskClientService externalTaskClientService;

    @Getter
    private HashMap<String, String> jwts = new HashMap<>();

    @PostConstruct
    public void addCamundaTaskHandlers() {
        externalTaskClientService.addHandlerToTopic("user-register", this::register);
        externalTaskClientService.addHandlerToTopic("user-login", this::login);
        externalTaskClientService.addHandlerToTopic("user-auth", this::authUserAutomatically);
    }

    private void authUserAutomatically(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String camundaLogin = externalTask.getVariable("currentUser").toString();

        String jwt = jwts.get(camundaLogin);
        jwt = (jwt == null || !jwtService.isTokenValid(jwt)) ? "" : jwt;

        externalTaskService.complete(externalTask, Collections.singletonMap("jwt", jwt));
    }

    private void register(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<String, Object> vars = externalTask.getAllVariables();

        RegisterRequest request = RegisterRequest.builder()
                .firstname(vars.get("firstname").toString())
                .lastname(vars.get("lastname").toString())
                .email(vars.get("email").toString())
                .login(vars.get("login").toString())
                .password(vars.get("password").toString())
                .build();

        try {
            String jwt = authService.register(request).getToken();
            jwts.put(externalTask.getVariable("currentUser").toString(), jwt);

            externalTaskService.complete(externalTask, Collections.singletonMap("jwt", jwt));
        } catch (Exception e) {
            externalTaskService.handleBpmnError(externalTask, "registerError");
        }

    }

    private void login(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<String, Object> vars = externalTask.getAllVariables();

        AuthenticationRequest request = AuthenticationRequest.builder()
                .login(vars.get("login").toString())
                .password(vars.get("password").toString())
                .build();

        try {
            String jwt = authService.authenticate(request).getToken();
            externalTaskService.complete(externalTask, Collections.singletonMap("jwt", jwt));
        } catch (Exception e) {
            externalTaskService.handleBpmnError(externalTask, "loginError");
        }
    }

    public User getTaskUser(ExternalTask externalTask) {
        String jwt = jwts.get(externalTask.getVariable("currentUser").toString());
        Integer userId = Integer.valueOf(jwtService.extractId(jwt));

        return userService.findById(userId).get();
    }

}
