package fyi.hrvanovicm.magacin.domain.article.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fyi.hrvanovicm.magacin.domain.article.Article;

@Repository
public interface ArticleRepository extends JpaSpecificationExecutor<Article>, JpaRepository<Article, Long> {
    @Modifying
    @Query("DELETE FROM Article WHERE id = :articleId")
    void forceDeleteById(@Param("articleId") long id);
}
