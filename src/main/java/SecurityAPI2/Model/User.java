package SecurityAPI2.Model;

import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.Enum.UserRole;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    @Column(name = "email",unique = true)
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "name")
    private String name;
    @Column(name = "surname")
    private String surname;
    @Column(name = "phoneNumber")
    private String phoneNumber;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_name"))
    private Set<Role> roles;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
    @Column(name = "status")
    private Status status;
    @Column(name = "firstLogged")
    boolean firstLogged;
    @Column(name = "authSecret")
    private String authSecret;

    public User(String email, String password, String name, String surname, String phoneNumber, Address address, List<Role> roles){
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.roles = new HashSet<Role>(roles);
        this.address = address;
    }

    public User(String email, String password, String name, String surname, String phoneNumber, Address address, List<Role> roles, String authSecret){
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.roles = new HashSet<Role>(roles);
        this.address = address;
        this.authSecret = authSecret;
    }

    public boolean isApproved() {
        return this.status == Status.APPROVED;
    }

    public boolean hasRole(UserRole match) {
        return this.roles.stream()
                .anyMatch(role -> role.getName().equals(match.getValue()));
    }

    public boolean isActivated() { return this.status == Status.ACTIVATED;}
}
