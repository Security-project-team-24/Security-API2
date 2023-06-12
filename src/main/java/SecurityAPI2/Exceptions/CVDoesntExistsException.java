package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class CVDoesntExistsException extends BaseException{
    public CVDoesntExistsException() {
        super("You haven't uploaded CV yet!", HttpStatus.BAD_REQUEST);
    }
}
