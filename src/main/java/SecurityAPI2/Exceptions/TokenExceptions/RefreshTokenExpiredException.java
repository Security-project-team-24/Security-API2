package SecurityAPI2.Exceptions.TokenExceptions;

import SecurityAPI2.Exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class RefreshTokenExpiredException extends BaseException {
    public RefreshTokenExpiredException() {
        super("Refresh token expired!", HttpStatus.UNAUTHORIZED);
    }
}
