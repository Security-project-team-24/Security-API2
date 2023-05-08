package SecurityAPI2.Repository;

import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProjectEmployeeRepository extends JpaRepository<ProjectEmployee, Long>  {
}
