package SecurityAPI2.Dto;

import SecurityAPI2.Model.Engineer;
import SecurityAPI2.Model.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EngineerSkillDto {
     Long id;
     String skill;
     int strength;

     public static List<EngineerSkillDto> mapSkillToDto(List<Skill> skills){
         List<EngineerSkillDto> dtos = new ArrayList<EngineerSkillDto>();
         for(Skill skill: skills){
             dtos.add(new EngineerSkillDto(skill.getId(), skill.getSkill(), skill.getStrength()));
         }
         return dtos;
     }
}
