package SecurityAPI2.Exceptions.TokenExceptions;

import SecurityAPI2.Exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class HmacTokenExpiredException extends BaseException {
    public HmacTokenExpiredException(){
        super("Your account link expired.", HttpStatus.GONE);
    }
}
