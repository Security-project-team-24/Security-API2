package SecurityAPI2.Service;

import SecurityAPI2.Dto.ProjectEmployeeRequest;
import SecurityAPI2.Model.Enum.Role;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;
import SecurityAPI2.Repository.IProjectEmployeeRepository;
import SecurityAPI2.Repository.IProjectRepository;
import SecurityAPI2.Repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectEmployeeService {
    private final IProjectEmployeeRepository projectEmployeeRepository;
    private final IProjectRepository projectRepository;
    private final IUserRepository userRepository;
    public ProjectEmployee addProjectEmployee(ProjectEmployeeRequest request) {
        Project project = projectRepository.getById(request.getProjectId());
        User employee = userRepository.getById(request.getEmployeeId());
        ProjectEmployee projectEmployee = new ProjectEmployee(employee, project, request.getJobDescription(), request.getStartDate(), request.getEndDate());
        projectEmployeeRepository.save(projectEmployee);
        return projectEmployee;
    }

    public List<ProjectEmployee> findAllEngineersOnProject(Long projectId){
        return projectEmployeeRepository.findByProjectIdAndEmployee_Role(projectId, Role.ENGINEER);
    }

    public List<ProjectEmployee> findAllEngineerProjects(Long employeeId) {
        return projectEmployeeRepository.findAllByEmployeeId(employeeId);
    }

    public ProjectEmployee updateJobDescription(User user, Long projectId, String description) {
        ProjectEmployee projectEmployee = projectEmployeeRepository.findByProjectIdAndEmployeeId(projectId, user.getId());
        projectEmployee.setJobDescription(description);
        return projectEmployeeRepository.save(projectEmployee);

    }
    public List<User> findAllEmployeesNotWorkingOnProject(Long projectId){
        List<Role> roles = Arrays.asList(Role.ENGINEER, Role.PROJECTMANAGER);
        List<User> allEmployees = userRepository.findByRoleInAndActivated(roles, true);

        Project project = projectRepository.findById(projectId).get();
        List<User> projectEmployees = new ArrayList<>();
        for (ProjectEmployee p: project.getProjectEmployees()) {
            projectEmployees.add(p.getEmployee());
        }
        List<User> usersNotWorkingOnProject = allEmployees.stream()
                .filter(user -> !projectEmployees.contains(user))
                .collect(Collectors.toList());
        return usersNotWorkingOnProject;
    }
}
