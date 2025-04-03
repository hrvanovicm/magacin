package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.common.embedded.Audit;
import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@NoArgsConstructor
@Entity
public class ReportProductEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private ReportEntity report;

    @ManyToOne(fetch = FetchType.EAGER)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.EAGER)
    private ProductReceptionEntity productReception;

    @Column(columnDefinition = "DECIMAL(10,2)", nullable = false)
    @ColumnDefault(value = "0")
    private Float amount;

    @Embedded
    private Audit audit;
}
