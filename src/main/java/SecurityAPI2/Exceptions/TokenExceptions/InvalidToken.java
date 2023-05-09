package SecurityAPI2.Exceptions.TokenExceptions;

import SecurityAPI2.Exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidToken extends BaseException {
    public InvalidToken(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
