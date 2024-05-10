package bobr.mainMicroservice.config;

import bobr.mainMicroservice.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/api/v1/auth/*",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/v2/api-docs/**",
                                "/swagger-resources/**"
                        ).permitAll()


                        .requestMatchers(PUT, "/api/v1/users/{userId}/block")
                        .hasAuthority("user.block")
                        .requestMatchers(PUT, "/api/v1/users/{userId}/unblock")
                        .hasAuthority("user.unblock")
                        .requestMatchers(DELETE, "/api/v1/users/{userId}")
                        .hasAuthority("user.delete")
                        .requestMatchers(PUT, "/api/v1/users/{userId}/addBalance/{usdVal}")
                        .hasAuthority("user.addBalance")

                        .requestMatchers(POST, "/api/v1/flats")
                        .hasAuthority("flat.add")
                        .requestMatchers(PUT, "/api/v1/flats/{flatId}/boost")
                        .hasAuthority("flat.boost")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
