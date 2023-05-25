package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class RolesEmptyException extends BaseException{
    public RolesEmptyException() {
        super("Role should be specified!", HttpStatus.BAD_REQUEST);
    }
}
