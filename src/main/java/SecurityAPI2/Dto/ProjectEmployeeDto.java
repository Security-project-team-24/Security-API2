package SecurityAPI2.Dto;

import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public ProjectEmployeeDto(ProjectEmployee employee) {
        this.id = employee.getId();
        this.employee = new UserDto(employee.getEmployee());
        this.jobDescription = employee.getJobDescription();
        this.project = employee.getProject();
        this.startDate = employee.getStartDate();
        this.endDate = employee.getEndDate();
    }

    public ProjectEmployee toModel() {
        return ProjectEmployee.builder()
                .id(id)
                .employee(employee.toModel())
                .jobDescription(jobDescription)
                .project(project)
                .startDate(startDate)
                .endDate(endDate)
                .build();
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
