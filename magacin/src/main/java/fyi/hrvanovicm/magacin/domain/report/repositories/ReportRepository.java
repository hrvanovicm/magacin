package fyi.hrvanovicm.magacin.domain.report.repositories;

import fyi.hrvanovicm.magacin.domain.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
}
