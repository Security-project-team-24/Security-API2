package SecurityAPI2.Repository;

import SecurityAPI2.Model.Enum.UserRole;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    Page<User> findAllByStatus(Pageable pageable,Status status);
    Page<User> findAll(Pageable pageable);




    @Query(nativeQuery = true, value = """
      SELECT DISTINCT u.* 
      FROM 
        users u, users_roles ur, roles r 
      WHERE 
        u.id = ur.users_id and
        ur.roles_name = r.name and
        u.status = ?2 and 
        r.name in ?1           
    """)
    List<User> findByRoleAndStatus(List<String> userRoles, int status);
}
