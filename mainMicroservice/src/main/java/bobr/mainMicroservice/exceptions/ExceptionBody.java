package bobr.mainMicroservice.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class ExceptionBody {

        private String message;
        private HttpStatus httpStatus;
        private ZonedDateTime timestamp;

        public ExceptionBody(String message, HttpStatus httpStatus) {
                this.message = message;
                this.httpStatus = httpStatus;

                timestamp = ZonedDateTime.now(ZoneId.of("Z"));
        }

}
