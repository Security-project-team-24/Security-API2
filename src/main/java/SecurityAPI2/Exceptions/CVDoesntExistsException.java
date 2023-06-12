package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class CVDoesntExistsException extends BaseException{
    public CVDoesntExistsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
