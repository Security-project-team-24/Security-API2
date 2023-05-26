package SecurityAPI2.Dto;

import SecurityAPI2.Model.Engineer;
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
    LocalDate seniority;

    public EngineerDto(Engineer engineer){
        this.id = engineer.getId();
        this.cv_url = engineer.getCvUrl();
        this.seniority = engineer.getSeniority();
    }
}
