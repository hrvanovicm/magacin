package fyi.hrvanovicm.magacin.domain.unit_measure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitMeasureRepository extends PagingAndSortingRepository<UnitMeasureEntity, Long>, JpaRepository<UnitMeasureEntity, Long> {
}
