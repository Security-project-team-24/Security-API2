package SecurityAPI2.Dto;

import SecurityAPI2.Model.Address;
import SecurityAPI2.Model.Enum.UserRole;
import lombok.*;
import SecurityAPI2.utils.ValidPassword;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RegisterDto {
    private Long id;
    private String email;
    @ValidPassword
    private String password;
    private String confirmPassword;
    private String name;
    private String surname;
    private String phoneNumber;
    private List<String> roles;
    private Address address;



}
