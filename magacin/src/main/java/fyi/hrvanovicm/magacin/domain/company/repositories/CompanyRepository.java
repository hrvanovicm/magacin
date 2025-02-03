package fyi.hrvanovicm.magacin.domain.company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fyi.hrvanovicm.magacin.domain.company.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    @Modifying
    @Query("DELETE FROM Company WHERE id = :companyId")
    void forceDeleteById(@Param("companyId") long id);
}
