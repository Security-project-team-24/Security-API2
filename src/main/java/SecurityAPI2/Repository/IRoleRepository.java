package SecurityAPI2.Repository;

import SecurityAPI2.Model.Engineer;
import SecurityAPI2.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRoleRepository extends JpaRepository<Role, String> {

}
