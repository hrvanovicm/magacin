package fyi.hrvanovicm.magacin.domain.unit_measure;

import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.common.embedded.Audit;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.hibernate.type.YesNoConverter;

@Data
@NoArgsConstructor
@Entity
@Table(name = "unit_measurements")
public class UnitMeasure {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = UnitMeasureValidationUtils.NAME_MAX_CHARACTERS)
    String name;

    @Column(nullable = false, length = UnitMeasureValidationUtils.SHORTNAME_MAX_CHARACTERS)
    String shortName;

    @OneToMany(
        mappedBy = "unitMeasure",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    Set<ProductEntity> products = new HashSet<>();

    @Column(nullable = false)
    Boolean isInteger;

    @Embedded
    Audit audit;
}
