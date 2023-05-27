package SecurityAPI2.Dto;

import SecurityAPI2.Model.Skill;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SkillDto {
    @Valid
    private ArrayList<Skill> skills;

}
