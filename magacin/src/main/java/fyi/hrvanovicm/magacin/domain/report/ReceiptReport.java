package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.company.Company;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class ReceiptReport {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    private Report report;

    @OneToOne(optional = false)
    private Company supplier;

    @Column
    private String supplierReportCode;
}
