package fyi.hrvanovicm.magacin.domain.account;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class UserProfile {
    public static final int FIRSTNAME_MAX_CHARACTERS = 64;
    public static final int LASTNAME_MAX_CHARACTERS = 64;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = FIRSTNAME_MAX_CHARACTERS)
    private String firstName;

    @Column(length = LASTNAME_MAX_CHARACTERS)
    private String lastName;
}
