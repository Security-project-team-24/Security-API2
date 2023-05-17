package SecurityAPI2.Exceptions.TokenExceptions;

import SecurityAPI2.Exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class LoginTokenInvalidException extends BaseException {
    public LoginTokenInvalidException(){
        super("Your login link invalid.", HttpStatus.BAD_REQUEST);
    }
}
