package SecurityAPI2.Dto;

import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.ProjectEmployee;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProjectDto {
    @Id
    Long id;
    String name;
    double duration;
    List<ProjectEmployeeDto> projectEmployees;

    public ProjectDto(Project project) {
        this.id = project.getId();
        this.duration = project.getDuration();
        this.projectEmployees = ProjectEmployeeDto.toDtos(project.getProjectEmployees());
    }

    public Project toModel() {
        return Project
                .builder()
                .id(id)
                .duration(duration)
                .projectEmployees(ProjectEmployeeDto.toModels(projectEmployees))
                .build();
    }

    public static List<ProjectDto> toDtos(List<Project> projects) {
        if(projects == null) return new ArrayList<>();
        return projects
                .stream()
                .map(project -> new ProjectDto(project))
                .toList();
    }

}
