package SecurityAPI2.Dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
public class ProjectEmployeeRequest {
    Long employeeId;
    String jobDescription;
    Long projectId;
    Date startDate;
    Date endDate;
}
