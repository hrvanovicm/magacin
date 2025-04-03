package fyi.hrvanovicm.magacin.domain.report.shipment;

import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class ShipmentReport {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    private ReportEntity report;

    @Column
    private String receiptCompanyName;
}
