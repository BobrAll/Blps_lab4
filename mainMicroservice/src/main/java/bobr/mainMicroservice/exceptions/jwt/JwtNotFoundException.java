package bobr.mainMicroservice.exceptions.jwt;

public class JwtNotFoundException extends RuntimeException{
    public JwtNotFoundException() {
        super("Token not found");
    }
}
