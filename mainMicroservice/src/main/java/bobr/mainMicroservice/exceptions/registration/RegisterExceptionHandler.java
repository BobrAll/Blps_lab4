package bobr.mainMicroservice.exceptions.registration;

import bobr.mainMicroservice.exceptions.ExceptionBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RegisterExceptionHandler {

    @ExceptionHandler(value = {UserAlreadyRegisteredException.class})
    public ResponseEntity<Object> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException e) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        return new ResponseEntity<>(new ExceptionBody(e.getMessage(), status), status);
    }
}
