package SecurityAPI2.Controller;

import SecurityAPI2.Dto.ProjectDto;
import SecurityAPI2.Mapper.ProjectMapper;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    @GetMapping("")
    public ResponseEntity<List<ProjectDto>> findAll() {
        List<Project> projects = projectService.FindAll();
        return ResponseEntity.ok(projectMapper.projectsToProjectDtos(projects));
    }
}
