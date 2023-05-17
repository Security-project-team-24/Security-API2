package SecurityAPI2.Repository;

import SecurityAPI2.Model.Enum.Role;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProjectEmployeeRepository extends JpaRepository<ProjectEmployee, Long>  {
    List<ProjectEmployee> findByProjectIdAndEmployee_Role(Long projectId, Role employeeRole);
    List<ProjectEmployee> findAllByEmployeeId(Long employeeId);

    ProjectEmployee findByProjectIdAndEmployeeId(Long projectId, Long employeeId);

    void removeProjectEmployeeByProjectIdAndEmployeeId(Long projectId, Long employeeId);
}
