package SecurityAPI2.Exceptions.TokenExceptions;

import SecurityAPI2.Exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class LoginTokenExpiredException extends BaseException {
    public LoginTokenExpiredException(){
        super("Your login link expired.", HttpStatus.GONE);
    }
}
