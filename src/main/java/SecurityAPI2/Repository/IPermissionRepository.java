package SecurityAPI2.Repository;

import SecurityAPI2.Model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface IPermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findPermissionsByRolesName(String role);
    @Query(nativeQuery = true, value = """
        SELECT DISTINCT p.*
        FROM permissions p LEFT JOIN permissions_roles pr ON p.id = pr.permissions_id
        WHERE p.id NOT IN (select permissions_id from permissions_roles prn where prn.roles_name = :role)
""")
    List<Permission> findPermissionsNotGranted(@Param("role") String role);
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from permissions_roles where roles_name=:role")
    void removeAllPermissionsByRole(@Param("role") String role);
}
