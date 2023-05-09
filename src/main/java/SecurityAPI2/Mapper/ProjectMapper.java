package SecurityAPI2.Mapper;

import SecurityAPI2.Dto.ProjectDto;
import SecurityAPI2.Model.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    List<ProjectDto> projectsToProjectDtos(List<Project> project);
    Project projectDtoToProject(ProjectDto projectDto);
    ProjectDto projectToProjectDto(Project project);
}