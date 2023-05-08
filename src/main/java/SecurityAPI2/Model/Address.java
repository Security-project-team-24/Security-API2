package SecurityAPI2.Model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String street;
    @Column
    private String streetNumber;
    @Column
    private String city;
    @Column
    private String zipCode;
    @Column
    private String country;
}
