package SecurityAPI2.Repository;

import SecurityAPI2.Model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findPermissionsByRolesName(String role);
    @Query(nativeQuery = true, value = """
        SELECT p.*
        FROM permissions p, permissions_roles pr
        WHERE p.id = pr.permissions_id
        AND p.id NOT IN (select permissions_id from permissions_roles prn where prn.roles_name = :role)
""")
    List<Permission> findPermissionsNotGranted(@Param("role") String role);

}
