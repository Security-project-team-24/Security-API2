package SecurityAPI2.Dto;

import SecurityAPI2.Model.Address;
import SecurityAPI2.Model.Enum.Role;
import SecurityAPI2.Model.Enum.Status;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
public class UserDto {
    Long id;
    String email;
    String name;
    String surname;
    String phoneNumber;
    Role role;
    Address address;
    Status status;
    boolean firstLogged;
    boolean activated;
}
