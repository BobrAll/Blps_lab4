package bobr.mainMicroservice.security.jwt;

import bobr.mainMicroservice.exceptions.jwt.JwtNotFoundException;
import bobr.mainMicroservice.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${JWT_SECRET_KEY}")
    private String SECRET_KEY;
    private final JwtRepository jwtRepository;

    public String extractId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(Map<String, Object> extraClaims, User user) {
        extraClaims.put("role", user.getRole());

        String token = Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        save(token);
        return token;
    }

    public boolean isTokenValid(String token) {
        return findJwtByToken(token) != null && !isTokenExpired(token);
    }

    public Jwt findJwtByToken(String token) {
        return jwtRepository.findJwtByToken(token).orElseThrow(() -> new JwtNotFoundException());
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void save(String token) {
        jwtRepository.save(new Jwt.JwtBuilder().token(token).build());
    }

    public void delete(String token) {
        Jwt jwt = findJwtByToken(token);
        jwtRepository.delete(jwt);
    }

    public String extractJwtFromHeader(String header) {
        int prefix = "Bearer ".length();

        return header.substring(prefix);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
