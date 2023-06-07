package SecurityAPI2.Dto;

import SecurityAPI2.Model.Engineer;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.ProjectEmployee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProjectEngineerDto {
    @Id
    Long id;
    EngineerDto engineer;
    UserDto employee;
    String jobDescription;
    Project project;
    Date startDate;
    Date endDate;
    public ProjectEngineerDto(ProjectEmployee employee, Engineer engineer) {
        this.id = employee.getId();
        this.engineer = new EngineerDto(engineer);
        this.jobDescription = employee.getJobDescription();
        this.project = employee.getProject();
        this.startDate = employee.getStartDate();
        this.endDate = employee.getEndDate();
        this.employee =new UserDto(employee.getEmployee());
    }



    public static List<ProjectEmployee> toModels(List<ProjectEmployeeDto> employees) {
        if(employees == null) return new ArrayList<>();
        return employees
                .stream()
                .map(employee -> employee.toModel())
                .toList();
    }

    public static List<ProjectEmployeeDto> toDtos(List<ProjectEmployee> employees) {
        if(employees == null) return new ArrayList<>();
        return employees
                .stream()
                .map(employee -> new ProjectEmployeeDto(employee))
                .toList();
    }
}
