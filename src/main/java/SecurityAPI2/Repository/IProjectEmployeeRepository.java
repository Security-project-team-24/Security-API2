package SecurityAPI2.Repository;

import SecurityAPI2.Model.ProjectEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IProjectEmployeeRepository extends JpaRepository<ProjectEmployee, Long>  {

    @Query(nativeQuery = true, value = "SELECT DISTINCT pe.* FROM project_employee pe WHERE pe.project_id = :project_id AND :role IN (SELECT name FROM users_roles WHERE users_id = pe.employee_id)")
    List<ProjectEmployee> findByProjectIdAndEmployee_Role(@Param("project_id") Long projectId, @Param("role") String role);
    List<ProjectEmployee> findAllByEmployeeId(Long employeeId);

    ProjectEmployee findByProjectIdAndEmployeeId(Long projectId, Long employeeId);

    void removeProjectEmployeeByProjectIdAndEmployeeId(Long projectId, Long employeeId);
}
