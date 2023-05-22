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
      SELECT DISTINCT u 
      FROM 
        users u, users_roles ur, roles r 
      WHERE 
        u.id=users u.status = :status and 
        u.id = ur.users_id and
        ur.roles_id = r.id and
        r.name in :roles           
    """)
    List<User> findByRoleAndStatus(@Param("roles") List<UserRole> userRoles, @Param("status") Status status);
}
