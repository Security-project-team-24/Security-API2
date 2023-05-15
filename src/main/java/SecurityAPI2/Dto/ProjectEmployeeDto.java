package SecurityAPI2.Dto;

import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.User;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProjectEmployeeDto {
    @Id
    Long id;
    UserDto employee;
    String jobDescription;
    Project project;
    Date startDate;
    Date endDate;
}
