package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.common.embedded.Audit;
import fyi.hrvanovicm.magacin.domain.account.User;
import fyi.hrvanovicm.magacin.domain.report.values.ReportType;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
public class Report {
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
    private String description;

    @Column
    private LocalDate date;

    @Column
    private String placeOfPublish;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private User signedBy;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReceiptReport receiptReport;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShipmentReport shipmentReport;

    @Embedded
    private Audit audit;
}
