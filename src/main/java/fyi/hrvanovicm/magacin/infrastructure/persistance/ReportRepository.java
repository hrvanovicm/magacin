package fyi.hrvanovicm.magacin.infrastructure.persistance;

import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long>, JpaSpecificationExecutor<ReportEntity> {
    @Query("SELECT DISTINCT rr.supplierCompanyName as name FROM ReceiptReportEntity rr " +
            "UNION SELECT DISTINCT sr.receiptCompanyName as name FROM ShipmentReportEntity sr")
    List<String> findDistinctSupplierCompanyNames();

    @Query("SELECT DISTINCT signedByName FROM ReportEntity")
    List<String> findDistinctSignedUserNames();
}
