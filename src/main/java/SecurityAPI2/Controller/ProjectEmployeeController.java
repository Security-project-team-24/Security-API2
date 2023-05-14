package SecurityAPI2.Controller;

import SecurityAPI2.Dto.ProjectEmployeeDto;
import SecurityAPI2.Dto.ProjectEmployeeRequest;
import SecurityAPI2.Dto.UserDto;
import SecurityAPI2.Mapper.ProjectEmployeeMapper;
import SecurityAPI2.Mapper.UserMapper;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;
import SecurityAPI2.Service.ProjectEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/project-employee")
public class ProjectEmployeeController {
    @Autowired
    private ProjectEmployeeService projectEmployeeService;
    @Autowired
    private ProjectEmployeeMapper projectEmployeeMapper;
    @Autowired
    private UserMapper userMapper;
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    public ResponseEntity<Long> addProjectEmployee(@Valid @RequestBody ProjectEmployeeRequest req) {
        ProjectEmployee projectEmployee = projectEmployeeService.addProjectEmployee(req);
        return ResponseEntity.ok(projectEmployee.getId());
    }
    @GetMapping("/{projectId}/engineers")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    public ResponseEntity<List<ProjectEmployeeDto>> findAllEngineersOnProject(@Valid @PathVariable Long projectId) {
        List<ProjectEmployee> engineers = projectEmployeeService.findAllEngineersOnProject(projectId);
        return ResponseEntity.ok(projectEmployeeMapper.projectEmployeesToProjectEmployeeDtos(engineers));
    }

    @GetMapping("/available/{projectId}")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDto>> findAllEmployeesNotWorkingOnProject(@Valid @PathVariable Long projectId) {
        List<User> employees = projectEmployeeService.findAllEmployeesNotWorkingOnProject(projectId);
        return ResponseEntity.ok(userMapper.usersToUserDtos(employees));
    }
}
