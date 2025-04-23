package fyi.hrvanovicm.magacin.domain.report;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@NoArgsConstructor
@Entity
@Table(name = "receipt_report")
public class ReceiptReportEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, unique = true)
    private ReportEntity report;

    @Column
    private String supplierCompanyName;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isSupplierProduction;

    @Column
    private String supplierReportCode;
}
