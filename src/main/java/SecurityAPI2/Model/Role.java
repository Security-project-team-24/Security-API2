package SecurityAPI2.Model;

import com.google.cloud.Identity;
import lombok.*;

import javax.annotation.processing.Generated;
import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "roles")
public class Role {

    @Id
    @Column(name = "name", unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "permissions_roles",
            joinColumns = @JoinColumn(name = "roles_name"),
            inverseJoinColumns = @JoinColumn(name = "permissions_id")
    )
    private Set<Permission> permissions;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;


    public Role(String name) {
        this.name = name;
    }

}
