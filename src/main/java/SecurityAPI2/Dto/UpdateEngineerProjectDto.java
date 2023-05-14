package SecurityAPI2.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateEngineerProjectDto {
    @Id
    Long projectId;
    String description;
}
