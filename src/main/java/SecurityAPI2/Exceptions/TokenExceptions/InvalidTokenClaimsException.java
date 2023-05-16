package SecurityAPI2.Exceptions.TokenExceptions;

import SecurityAPI2.Exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidTokenClaimsException extends BaseException {
    public InvalidTokenClaimsException() {
        super("Invalid token claims, can't find user!", HttpStatus.UNAUTHORIZED);
    }
}
