package fyi.hrvanovicm.magacin.domain.account;

import fyi.hrvanovicm.magacin.domain.account.utils.UserValidationUtils;
import fyi.hrvanovicm.magacin.domain.common.embedded.Audit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.hibernate.type.YesNoConverter;

import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@SoftDelete(strategy = SoftDeleteType.ACTIVE, converter = YesNoConverter.class)
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = UserValidationUtils.USERNAME_MAX_CHARACTERS)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private UserProfile profile;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"})
    )
    private Set<UserRole> roles;

    @Embedded
    private Audit audit;
}
