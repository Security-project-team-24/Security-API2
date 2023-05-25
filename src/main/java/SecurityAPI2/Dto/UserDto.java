package SecurityAPI2.Dto;

import SecurityAPI2.Model.Address;
import SecurityAPI2.Model.Enum.UserRole;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.Role;
import SecurityAPI2.Model.User;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    Long id;
    String email;
    String name;
    String surname;
    String phoneNumber;
    List<String> roles;
    Address address;
    Status status;
    boolean firstLogged;


    public User toModel() {
        List<Role> mappedRoles = this.roles.stream()
                .map(role -> new Role(role))
                .collect(Collectors.toList());
        Address mappedAddress = Address.builder()
                .city(address.getCity())
                .country(address.getCountry())
                .street(address.getStreet())
                .streetNumber(address.getStreetNumber())
                .zipCode(address.getZipCode())
                .build();

        return User.builder()
                .id(id)
                .email(email)
                .name(name)
                .surname(surname)
                .phoneNumber(phoneNumber)
                .address(mappedAddress)
                .roles(new HashSet<>(mappedRoles))
                .status(status)
                .build();
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.status = user.getStatus();
        this.address = Address.builder()
                .id(user.getAddress().getId())
                .city(user.getAddress().getCity())
                .country(user.getAddress().getCountry())
                .zipCode(user.getAddress().getZipCode())
                .streetNumber(user.getAddress().getStreetNumber())
                .street(user.getAddress().getStreet())
                .build();
        this.roles = user.getRoles()
                .stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
        this.firstLogged = user.isFirstLogged();

    }

    public static List<UserDto> toDtos(List<User> users) {
        if(users == null) return new ArrayList<>();
        return users
                .stream()
                .map(user -> new UserDto(user))
                .toList();
    }

}



