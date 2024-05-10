package bobr.mainMicroservice.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findUserByLogin(String login);

    List<User> findUsersBySubscriptionExpirationBefore(Calendar calendar);
}
