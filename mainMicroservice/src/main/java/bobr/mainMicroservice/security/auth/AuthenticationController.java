package bobr.mainMicroservice.security.auth;

import bobr.mainMicroservice.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/signUp")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/signIn")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @Transactional
    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader String authorization) {
        String jwt = jwtService.extractJwtFromHeader(authorization);
        jwtService.delete(jwt);

        return ResponseEntity.ok("Token has been removed successfully");
    }
}
