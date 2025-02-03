package fyi.hrvanovicm.magacin.domain.company;

import fyi.hrvanovicm.magacin.domain.common.address.Address;
import fyi.hrvanovicm.magacin.domain.common.embedded.Audit;
import fyi.hrvanovicm.magacin.domain.company.utils.CompanyValidationRulesUtil;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@NoArgsConstructor
@Entity
public class Company {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = CompanyValidationRulesUtil.NAME_MAX_CHARACTERS)
    private String name;

    @Column(length = CompanyValidationRulesUtil.DESCRIPTION_MAX_CHARACTERS)
    private String description;

    @ManyToMany
    @JoinTable(
        name = "company_has_address",
        joinColumns = @JoinColumn(name = "company_id"),
        inverseJoinColumns = @JoinColumn(name = "address_id"),
        uniqueConstraints = {
            @UniqueConstraint(columnNames = { "company_id", "address_id" }),
        }
    )
    private Set<Address> addresses = new HashSet<>();

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    private Boolean isSupplier;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    private Boolean isRecipient;

    @Embedded
    private Audit audit;
}
