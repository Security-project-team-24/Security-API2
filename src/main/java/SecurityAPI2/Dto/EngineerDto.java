package SecurityAPI2.Dto;

import SecurityAPI2.Model.Engineer;
import SecurityAPI2.Model.Enum.Seniority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EngineerDto {
    Long id;
    String cv_url;
    Seniority seniority;

    public EngineerDto(Engineer engineer){
        this.id = engineer.getId();
        this.cv_url = engineer.getCvUrl();
        this.seniority = engineer.getSeniority();
    }
}
