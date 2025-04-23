package fyi.hrvanovicm.magacin.domain.report;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "shipment_report")
public class ShipmentReportEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, unique = true)
    private ReportEntity report;

    @Column
    private String receiptCompanyName;
}
