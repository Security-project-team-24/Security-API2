package SecurityAPI2.Dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

@Getter
@Setter
public class ProjectEmployeeRequest {
    Long employeeId;
    String jobDescription;
    Long projectId;
}
