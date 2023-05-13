package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class UserDoesntExistException extends BaseException{
    public UserDoesntExistException() {
        super("Cant find specified user!", HttpStatus.NOT_FOUND);
    }
}
