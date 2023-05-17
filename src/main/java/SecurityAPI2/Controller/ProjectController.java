package SecurityAPI2.Controller;

import SecurityAPI2.Dto.PageDto;
import SecurityAPI2.Dto.ProjectDto;
import SecurityAPI2.Mapper.ProjectMapper;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ProjectMapper projectMapper;
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    public ResponseEntity<ProjectDto> create(@Valid @RequestBody ProjectDto dto) {
        Project project = projectService.Create(projectMapper.projectDtoToProject(dto));
        return ResponseEntity.ok(projectMapper.projectToProjectDto(project));
    }
    @GetMapping("/{pageSize}/{pageNumber}")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    public ResponseEntity<PageDto<ProjectDto>> findAll(@Valid @PathVariable int pageSize, @Valid @PathVariable int pageNumber) {
        Page<Project> projectsPage = projectService.FindAll(pageSize, pageNumber);
        PageDto<ProjectDto> dto = new PageDto<>();
        dto.setContent(projectMapper.projectsToProjectDtos(projectsPage.getContent()));
        dto.setTotalPages(projectsPage.getTotalPages());
        return ResponseEntity.ok(dto);
    }
}
