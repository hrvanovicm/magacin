package fyi.hrvanovicm.magacin.domain.report;

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
@Table(name = "report_products")
public class ReportProductEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id")
    private ReportEntity report;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<ReportProductUsedReceptionsEntity> usedReceptions = new ArrayList<>();

    @Column(columnDefinition = "DECIMAL(10,2)", nullable = false)
    @ColumnDefault(value = "0")
    private Float amount;

    @Embedded
    private Audit audit;
}
