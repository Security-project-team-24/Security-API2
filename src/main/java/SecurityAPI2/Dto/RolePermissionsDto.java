package SecurityAPI2.Dto;

import SecurityAPI2.Model.Permission;
import lombok.*;

import java.security.Permissions;
import java.util.List;

@Getter
@Setter
public class RolePermissionsDto {

    List<PermissionDto> notGranted;
    List<PermissionDto> granted;

    public RolePermissionsDto(List<Permission> notGranted, List<Permission> granted) {
        this.notGranted = notGranted
                .stream()
                .map(p -> new PermissionDto(p.getId(), p.getName()))
                .toList();
        this.granted = granted
                .stream()
                .map(p -> new PermissionDto(p.getId(), p.getName()))
                .toList();
    }

}
