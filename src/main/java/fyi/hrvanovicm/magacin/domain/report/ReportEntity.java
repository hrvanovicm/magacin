package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.common.embedded.Audit;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductEntity;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportEntity;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportEntity;
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
    private ReceiptReportEntity receiptReport;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private ShipmentReportEntity shipmentReport;

    @OneToMany(mappedBy = "report", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ReportProductEntity> products = new ArrayList<>();

    @Embedded
    private Audit audit;

    @Override
    public String toString() {
        return this.code;
    }
}
