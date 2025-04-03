package fyi.hrvanovicm.magacin.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long>, JpaSpecificationExecutor<ReportEntity> {
    @Query("SELECT DISTINCT rr.supplierCompanyName as name FROM ReceiptReport rr " +
            "UNION SELECT DISTINCT sr.receiptCompanyName as name FROM ShipmentReport sr")
    List<String> findDistinctSupplierCompanyNames();

    @Query("SELECT DISTINCT signedByName FROM ReportEntity")
    List<String> findDistinctSignedUserNames();
}
