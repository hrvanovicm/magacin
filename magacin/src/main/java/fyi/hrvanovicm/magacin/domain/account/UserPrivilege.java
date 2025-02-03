package fyi.hrvanovicm.magacin.domain.account;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class UserPrivilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<UserRole> roles;
}