package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class SkillValueInvalid extends BaseException{
    public SkillValueInvalid() {
        super("Skill value must be between 1 and 5!", HttpStatus.BAD_REQUEST);
    }
}
