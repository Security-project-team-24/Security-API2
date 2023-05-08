package SecurityAPI2.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Setter
public class LoginDto {
        String email;
        String password;
}
