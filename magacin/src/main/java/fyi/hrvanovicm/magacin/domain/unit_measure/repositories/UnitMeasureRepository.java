package fyi.hrvanovicm.magacin.domain.unit_measure.repositories;

import fyi.hrvanovicm.magacin.domain.company.Company;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitMeasureRepository extends PagingAndSortingRepository<UnitMeasure, Long>, JpaRepository<UnitMeasure, Long> {
    @Modifying
    @Query("DELETE FROM UnitMeasure WHERE id = :unitMeasureId")
    void forceDeleteById(@Param("unitMeasureId") Long id);
}
