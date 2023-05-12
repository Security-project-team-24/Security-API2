package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class HmacTokenExpiredException extends BaseException{
    public HmacTokenExpiredException(){
        super("Your account link expired.", HttpStatus.GONE);
    }
}
