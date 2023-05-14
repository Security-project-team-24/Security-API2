package SecurityAPI2.Exceptions.TokenExceptions;

import SecurityAPI2.Exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class HmacTokenInvalidException extends BaseException {
    public HmacTokenInvalidException(){
        super("Your account link invalid.", HttpStatus.BAD_REQUEST);
    }
}
