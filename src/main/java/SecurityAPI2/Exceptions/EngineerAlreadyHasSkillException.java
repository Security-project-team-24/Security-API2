package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class EngineerAlreadyHasSkillException extends BaseException{
    public EngineerAlreadyHasSkillException() {
        super("Engineer already has this skill!", HttpStatus.BAD_REQUEST);
    }
}
