package SecurityAPI2.Controller;

import SecurityAPI2.Dto.PageDto;
import SecurityAPI2.Dto.ProjectDto;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and hasAuthority('create_project')")
    public ResponseEntity<ProjectDto> create(@Valid @RequestBody ProjectDto dto) {
        Project project = dto.toModel();
        Project created = projectService.create(project);
        return ResponseEntity.ok(new ProjectDto(created));
    }
    @GetMapping("/{pageSize}/{pageNumber}")
    @PreAuthorize("isAuthenticated() and hasAuthority('read_all_projects')")
    public ResponseEntity<PageDto<ProjectDto>> findAll(@Valid @PathVariable int pageSize, @Valid @PathVariable int pageNumber) {
        Page<Project> projectsPage = projectService.findAll(pageSize, pageNumber);
        PageDto<ProjectDto> dto = new PageDto<>();
        List<ProjectDto> dtos = ProjectDto.toDtos(projectsPage.getContent());
        dto.setContent(dtos);
        dto.setTotalPages(projectsPage.getTotalPages());
        return ResponseEntity.ok(dto);
    }
}
