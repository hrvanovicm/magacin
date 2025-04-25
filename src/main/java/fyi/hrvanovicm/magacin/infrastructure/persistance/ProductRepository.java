package fyi.hrvanovicm.magacin.infrastructure.persistance;

import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaSpecificationExecutor<ProductEntity>, JpaRepository<ProductEntity, Long> {
    @Query("SELECT DISTINCT name FROM ProductEntity")
    List<String> findDistinctProductNames();

    @Query("SELECT DISTINCT name FROM ProductTagEntity")
    List<String> findDistinctTagNames();
}
