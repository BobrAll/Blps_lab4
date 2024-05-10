package bobr.mainMicroservice;

import bobr.mainMicroservice.user.Role;
import bobr.mainMicroservice.user.User;
import bobr.mainMicroservice.user.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class MainMicroserviceApplication {
    public static void main(String[] args) {
        var context = SpringApplication.run(MainMicroserviceApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        PasswordEncoder passwordEncoder = context.getBean(BCryptPasswordEncoder.class);

        userService.save(User.builder()
                .login("admin")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ADMIN)
                .enabled(true)
                .build()
        );
    }
}
