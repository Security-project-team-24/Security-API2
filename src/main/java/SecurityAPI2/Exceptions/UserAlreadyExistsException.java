package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends  BaseException{
    public UserAlreadyExistsException() {
        super("You are already registered!", HttpStatus.CONFLICT);
    }
}
