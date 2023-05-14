package SecurityAPI2.Mapper;

import SecurityAPI2.Dto.ProjectDto;
import SecurityAPI2.Dto.ProjectEmployeeDto;
import SecurityAPI2.Model.ProjectEmployee;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectEmployeeMapper {
    List<ProjectEmployeeDto> projectEmployeesToProjectEmployeeDtos(List<ProjectEmployee> projectEmployeeList);
    ProjectEmployeeDto projectEmployeeToProjectEmployeeDto(ProjectEmployee projectEmployee);
}
