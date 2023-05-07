package SecurityAPI2.Exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RuntimeErrorHandlingConfiguration {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> notFoundExceptionHandler(BaseException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage(), exception.getStatus()), exception.getStatus());
    }
}