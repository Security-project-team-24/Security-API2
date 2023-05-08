package SecurityAPI2.Dto;

import SecurityAPI2.Model.Skill;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class SkillDto {
    private ArrayList<Skill> skills;
}
