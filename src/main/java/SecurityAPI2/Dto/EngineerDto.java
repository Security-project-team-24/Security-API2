package SecurityAPI2.Dto;

import SecurityAPI2.Model.Engineer;
import SecurityAPI2.Model.Enum.Seniority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EngineerDto {
    Long id;
    String cv_url;
    Seniority seniority;
    UserDto user;
    LocalDate hireDate;

    public EngineerDto(Engineer engineer){
        this.id = engineer.getId();
        this.cv_url = engineer.getCvUrl();
        this.seniority = engineer.getSeniority();
        this.user = new UserDto(engineer.getUser());
        this.hireDate = engineer.getHireDate();
    }

    public static List<EngineerDto> engineerDtosFromEngineers(List<Engineer> engineers){
        List<EngineerDto> dtos = new ArrayList<>();
        for(Engineer engineer : engineers) {
            dtos.add(new EngineerDto(engineer));
        }
        return dtos;
    }
}
