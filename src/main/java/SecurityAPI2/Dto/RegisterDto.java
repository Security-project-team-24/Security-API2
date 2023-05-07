package SecurityAPI2.Dto;

import SecurityAPI2.Model.Address;
import SecurityAPI2.Model.Enum.Role;
import lombok.Getter;
import lombok.Setter;
import SecurityAPI2.utils.ValidPassword;

@Getter
@Setter
public class RegisterDto {
    private Long id;
    private String email;
    @ValidPassword
    private String password;
    private String confirmPassword;
    private String name;
    private String surname;
    private String phoneNumber;
    private Role role;
    private Address address;
}
