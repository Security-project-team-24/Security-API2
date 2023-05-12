package SecurityAPI2.Model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String skill;
    @Column(name = "strength")
    @Min(1)
    @Max(5)
    private int strength;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "engineer_id", referencedColumnName = "id")
    private Engineer engineer;

    public Skill(String name, int strength, Engineer engineer){
        this.skill = name;
        this.strength = strength;
        this.engineer = engineer;
    }
}
