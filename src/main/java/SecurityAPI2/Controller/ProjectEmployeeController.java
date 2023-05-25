package SecurityAPI2.Controller;

import SecurityAPI2.Dto.ProjectEmployeeDto;
import SecurityAPI2.Dto.ProjectEmployeeRequest;
import SecurityAPI2.Dto.UpdateEngineerProjectDto;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;
import SecurityAPI2.Dto.UserDto;
import SecurityAPI2.Service.AuthService;
import SecurityAPI2.Service.ProjectEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/project-employee")
@RequiredArgsConstructor
public class ProjectEmployeeController {
    private final ProjectEmployeeService projectEmployeeService;
    private final AuthService authService;
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and hasAuthority('create_project_employee')")
    public ResponseEntity<Long> addProjectEmployee(@Valid @RequestBody ProjectEmployeeRequest req) {
        ProjectEmployee projectEmployee = projectEmployeeService.addProjectEmployee(req);
        return ResponseEntity.ok(projectEmployee.getId());
    }
    @GetMapping("/{projectId}/engineers")
    @PreAuthorize("isAuthenticated() and hasAuthority('read_all_project_engineers')")
    public ResponseEntity<List<ProjectEmployeeDto>> findAllEngineersOnProject(@Valid @PathVariable Long projectId) {
        List<ProjectEmployee> engineers = projectEmployeeService.findAllEngineersOnProject(projectId);
        List<ProjectEmployeeDto> dtos = ProjectEmployeeDto.toDtos(engineers);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/projects")
    @PreAuthorize("isAuthenticated() and hasAuthority('read_all_engineer_projects')")
    public ResponseEntity<List<ProjectEmployeeDto>> findAllEngineerProjects(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        final User user = authService.getUserFromToken(authHeader);
        List<ProjectEmployee> projects = projectEmployeeService.findAllEngineerProjects(user.getId());
        List<ProjectEmployeeDto> dtos = ProjectEmployeeDto.toDtos(projects);
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/description/update")
    @PreAuthorize("isAuthenticated() and hasAuthority('update_job_description')")
    public ResponseEntity<ProjectEmployeeDto> updateJobDescription(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader, @Valid @RequestBody UpdateEngineerProjectDto updateEngineerProjectDto) {
        final User user = authService.getUserFromToken(authHeader);
        ProjectEmployee updatedProjectEmployee = projectEmployeeService.updateJobDescription(user, updateEngineerProjectDto.getProjectId(), updateEngineerProjectDto.getDescription());
       ProjectEmployeeDto dto = new ProjectEmployeeDto(updatedProjectEmployee);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/available/{projectId}")
    @PreAuthorize("isAuthenticated() and hasAuthority('read_workers_not_employed')")
    public ResponseEntity<List<UserDto>> findAllEmployeesNotWorkingOnProject(@Valid @PathVariable Long projectId) {
        List<User> employees = projectEmployeeService.findAllEmployeesNotWorkingOnProject(projectId);
        return ResponseEntity.ok(UserDto.toDtos(employees));
    }

    @GetMapping("/projects/manager")
    @PreAuthorize("isAuthenticated() and hasAuthority('read_all_manager_projects')")
    public ResponseEntity<List<ProjectEmployeeDto>> findManagerProjects(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        final User user = authService.getUserFromToken(authHeader);
        List<ProjectEmployee> projects = projectEmployeeService.findManagerProjects(user);
        List<ProjectEmployeeDto> dtos = ProjectEmployeeDto.toDtos(projects);
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("")
    @PreAuthorize("isAuthenticated() and hasAuthority('delete_project_employee')")
    public ResponseEntity<?> removeEmployeeFromProject(@Valid @RequestBody ProjectEmployeeRequest projectEmployeeRequest){
        projectEmployeeService.removeEmployeeFromProject(projectEmployeeRequest.getProjectId(), projectEmployeeRequest.getEmployeeId());
        return ResponseEntity.ok().build();
    }
}
