package bobr.mainMicroservice.security.auth;

import bobr.mainMicroservice.exceptions.registration.UserAlreadyRegisteredException;
import bobr.mainMicroservice.security.jwt.JwtService;
import bobr.mainMicroservice.user.Role;
import bobr.mainMicroservice.user.User;
import bobr.mainMicroservice.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

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
