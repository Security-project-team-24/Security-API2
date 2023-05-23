package SecurityAPI2.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PermissionDto {
    private Long id;
    private String name;

    public PermissionDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
