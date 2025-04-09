package fyi.hrvanovicm.magacin.domain.report.product;

import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@NoArgsConstructor
@Entity
@Table(name = "report_product_used_receptions")
public class ReportProductUsedReceptionsEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private ReportEntity report;

    @ManyToOne(fetch = FetchType.EAGER)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.EAGER)
    private ProductEntity rawMaterialProduct;

    @Column(columnDefinition = "DECIMAL(10,2)", nullable = false)
    @ColumnDefault(value = "0")
    private Float amount;
}
