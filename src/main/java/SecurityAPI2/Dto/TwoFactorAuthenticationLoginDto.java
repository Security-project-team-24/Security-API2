package SecurityAPI2.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TwoFactorAuthenticationLoginDto {
    String email;
    String password;
    String code;
}
