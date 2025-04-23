package fyi.hrvanovicm.magacin.domain.unit_measure;

import fyi.hrvanovicm.magacin.shared.embeddable.Audit;
import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "unit_measurements")
public class UnitMeasureEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = UnitMeasureValidationUtils.NAME_MAX_CHARACTERS)
    String name;

    @Column(nullable = false, length = UnitMeasureValidationUtils.SHORTNAME_MAX_CHARACTERS)
    String shortName;

    @OneToMany(mappedBy = "unitMeasure", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    List<ProductEntity> products = new ArrayList<>();

    @Column(nullable = false)
    @ColumnDefault("false")
    Boolean isInteger;

    @Embedded
    Audit audit;
}
