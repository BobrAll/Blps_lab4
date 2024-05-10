package bobr.mainMicroservice.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    @PutMapping("/{userId}/block")
    public void block(@PathVariable Integer userId) {
        userService.block(userId);
    }

    @PutMapping("/{userId}/unblock")
    public void unblock(@PathVariable Integer userId) {
        userService.unblock(userId);
    }

    @PutMapping("/{userId}/addBalance/{usdVal}")
    @Transactional
    public void addBalance(@PathVariable Integer userId, @PathVariable Double usdVal) {
        User user = userService.findById(userId).orElseThrow();
        user.setUsdBalance(user.getUsdBalance() + usdVal);
        userService.save(user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Integer userId) {
        userService.delete(userId);
    }

}
