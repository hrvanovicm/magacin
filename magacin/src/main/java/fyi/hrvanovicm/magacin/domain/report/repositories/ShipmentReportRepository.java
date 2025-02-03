package fyi.hrvanovicm.magacin.domain.report.repositories;

import fyi.hrvanovicm.magacin.domain.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentReportRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
}
