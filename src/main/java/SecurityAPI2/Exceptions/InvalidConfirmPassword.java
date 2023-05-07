package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class InvalidConfirmPassword extends BaseException{
    public InvalidConfirmPassword() {
        super("Confirm password and password are not matching!", HttpStatus.BAD_REQUEST);
    }
}
