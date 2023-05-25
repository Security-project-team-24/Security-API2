package SecurityAPI2.Service;

import SecurityAPI2.Dto.ProjectEmployeeRequest;
import SecurityAPI2.Model.Enum.UserRole;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;
import SecurityAPI2.Repository.IProjectEmployeeRepository;
import SecurityAPI2.Repository.IProjectRepository;
import SecurityAPI2.Repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return projectEmployeeRepository.findByProjectIdAndEmployee_Role(projectId, UserRole.ENGINEER.getValue());
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
        List<UserRole> userRoles = Arrays.asList(UserRole.ENGINEER, UserRole.PROJECT_MANAGER);
        System.out.println(userRoles);
        System.out.println(Status.ACTIVATED.getValue());
        List<String> roles = userRoles.stream().map(UserRole::getValue).toList();
        List<User> allEmployees = userRepository.findByRoleAndStatus(roles, Status.ACTIVATED.getValue());

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

    public List<ProjectEmployee> findManagerProjects(User user){
        return projectEmployeeRepository.findAllByEmployeeId(user.getId());
    }

    @Transactional
    public void removeEmployeeFromProject(Long projectId, Long employeeId){
        projectEmployeeRepository.removeProjectEmployeeByProjectIdAndEmployeeId(projectId, employeeId);
    }
}
