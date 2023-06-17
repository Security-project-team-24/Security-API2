package SecurityAPI2.Exceptions;
import SecurityAPI2.Service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class RuntimeErrorHandlingConfiguration {
    private final Logger logger = LoggerFactory.getLogger(RuntimeErrorHandlingConfiguration.class);
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> notFoundExceptionHandler(BaseException exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(exception.getMessage(),
                        exception.getStatus()), exception.getStatus());
    }
}