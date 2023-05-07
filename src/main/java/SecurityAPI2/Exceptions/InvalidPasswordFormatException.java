package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class InvalidPasswordFormatException extends BaseException{

    public InvalidPasswordFormatException() {
        super("Password should container uppercase letters, " +
                "lowercase letters, numbers and special characters. Also password should be at least 10 characcters long.", HttpStatus.BAD_REQUEST);
    }
}
