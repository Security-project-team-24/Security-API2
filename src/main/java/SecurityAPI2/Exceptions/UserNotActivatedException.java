package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class UserNotActivatedException extends BaseException {
    public UserNotActivatedException() {
        super("Your account is not activated!", HttpStatus.NOT_ACCEPTABLE);
    }
}
