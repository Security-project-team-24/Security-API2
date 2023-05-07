package SecurityAPI2.Dto;

import SecurityAPI2.Model.ProjectEmployee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
public class ProjectDto {
    @Id
    Long id;
    String name;
    double duration;
    List<ProjectEmployee> projectEmployees;
}
