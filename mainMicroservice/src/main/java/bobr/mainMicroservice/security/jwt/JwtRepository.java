package bobr.mainMicroservice.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtRepository extends JpaRepository<Jwt, Integer> {
    Optional<Jwt> findJwtByToken(String token);
}
