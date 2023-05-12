package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class RegistrationDisapprovedInNearPastException extends BaseException{
    public RegistrationDisapprovedInNearPastException() {
        super("Your registration was already declined in last 2 weeks", HttpStatus.BAD_REQUEST);
    }
}
