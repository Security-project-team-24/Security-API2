package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class UserBlockedException extends BaseException{
    public UserBlockedException() {
        super("You are blocked! Contact administrator!", HttpStatus.UNAUTHORIZED);
    }
}
