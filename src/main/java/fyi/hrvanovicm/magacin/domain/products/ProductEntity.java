package fyi.hrvanovicm.magacin.domain.products;

import fyi.hrvanovicm.magacin.shared.embeddable.Audit;
import fyi.hrvanovicm.magacin.domain.report.ReportProductEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "products")

public class ProductEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_measure_id", nullable = false)
    private UnitMeasureEntity unitMeasure;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<ProductTagEntity> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column
    private ProductCategory category;

    @Column(nullable = false, length = ProductValidationRulesUtils.NAME_MAX_CHARACTERS)
    private String name;

    @Column(nullable = false, length = ProductValidationRulesUtils.CODE_MAX_CHARACTERS, unique = true)
    private String code;

    @Column(length = ProductValidationRulesUtils.DESCRIPTION_MAX_CHARACTERS)
    private String descriptionHtml;

    @Column(columnDefinition = "DECIMAL(10,2)", nullable = false)
    @ColumnDefault(value = "0")
    private Float inStockAmount;

    @Column(columnDefinition = "DECIMAL(10,2)", nullable = false)
    @ColumnDefault(value = "0")
    private Float inStockWarningAmount;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductReceptionEntity> receptions = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ReportProductEntity> reports = new ArrayList<>();

    @Embedded
    private Audit audit;
}
