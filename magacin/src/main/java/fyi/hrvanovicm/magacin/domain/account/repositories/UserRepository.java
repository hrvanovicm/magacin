package fyi.hrvanovicm.magacin.domain.account.repositories;

import fyi.hrvanovicm.magacin.domain.account.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaSpecificationExecutor<User>, JpaRepository<User, Long> {
    @Modifying
    @Query("DELETE FROM User WHERE id = :userId")
    void forceDeleteById(@Param("userId") long id);
}
