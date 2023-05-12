package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class RegistrationApprovalNonExistingException extends BaseException{
    public RegistrationApprovalNonExistingException(){
        super("Your account activation doesn't exists", HttpStatus.NOT_FOUND);
    }
}
