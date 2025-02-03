package fyi.hrvanovicm.magacin.domain.common.address;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column
    private String country;

    @Column
    private String city;

    @Column
    private String address;

    @Column
    private String phoneNumber;

    @Column
    private String mobileNumber;

    @Column
    private String email;
}
