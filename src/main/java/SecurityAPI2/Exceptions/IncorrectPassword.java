package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class IncorrectPassword extends BaseException{
    public IncorrectPassword() {
        super("Incorrect current password!", HttpStatus.BAD_REQUEST);
    }
}
