package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.common.embedded.Audit;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReport;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReport;
import lombok.*;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "report")
public class ReportEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column
    private ReportType type;

    @Column()
    private String code;

    @Column()
    private String descriptionHtml;

    @Column
    private String date;

    @Column
    private String placeOfPublish;

    @Column
    private String signedByName;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private ReceiptReport receiptReport;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private ShipmentReport shipmentReport;

    @OneToMany(mappedBy = "report", fetch = FetchType.EAGER)
    private List<ReportProductEntity> reportProducts = new ArrayList<>();

    @Embedded
    private Audit audit;
}
