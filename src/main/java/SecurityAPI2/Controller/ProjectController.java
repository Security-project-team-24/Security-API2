package SecurityAPI2.Controller;

import SecurityAPI2.Dto.ProjectDto;
import SecurityAPI2.Dto.RegisterDto;
import SecurityAPI2.Exceptions.InvalidPasswordFormatException;
import SecurityAPI2.Mapper.ProjectMapper;
import SecurityAPI2.Mapper.UserMapper;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Service.ProjectService;
import SecurityAPI2.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectMapper projectMapper;
    @PostMapping("")
    public ResponseEntity<ProjectDto> create(@Valid @RequestBody ProjectDto dto) {
        Project project = projectService.Create(projectMapper.projectDtoToProject(dto));
        return ResponseEntity.ok(projectMapper.projectToProjectDto(project));
    }
}
