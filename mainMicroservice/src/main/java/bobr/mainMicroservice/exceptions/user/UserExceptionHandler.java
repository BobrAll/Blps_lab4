package bobr.mainMicroservice.exceptions.user;

import bobr.mainMicroservice.exceptions.ExceptionBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(value = {
            AlreadyHaveSuperuserPermissionsException.class,
            UserNotSubscribedException.class,
            NotEnoughMoneyException.class
    })
    public ResponseEntity<Object> handleSuperuserExceptions(
            RuntimeException e) {
        HttpStatus status = HttpStatus.CONFLICT;

        return new ResponseEntity<>(new ExceptionBody(e.getMessage(), status), status);
    }
}