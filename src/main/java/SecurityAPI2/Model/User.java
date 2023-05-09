package SecurityAPI2.Model;

import SecurityAPI2.Model.Enum.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import SecurityAPI2.Model.Enum.Role;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "name")
    private String name;
    @Column(name = "surname")
    private String surname;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    @Column(name = "role")
    private Role role;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
    @Column(name = "status")
    private Status status;
    @Column(name = "firstLogged")
    boolean firstLogged;


    public User(String email, String password, String name, String surname, String phoneNumber, Role role, Address address){
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.address = address;
    }
}
