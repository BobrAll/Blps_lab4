package bobr.mainMicroservice.exceptions.flat;

import bobr.mainMicroservice.exceptions.ExceptionBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FlatExceptionHandler {

    @ExceptionHandler(value = {NoSuchFlatException.class})
    public ResponseEntity<Object> handleNoSuchFlatException(NoSuchFlatException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(new ExceptionBody(e.getMessage(), status), status);
    }

}
