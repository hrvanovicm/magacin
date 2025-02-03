package fyi.hrvanovicm.magacin.application.http.v1;

import fyi.hrvanovicm.magacin.application.requests.ArticleSearchCriteriaDTO;
import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.article.dto.request.ArticleCreateRequestDTO;
import fyi.hrvanovicm.magacin.domain.article.dto.request.ArticleUpdateRequestDTO;
import fyi.hrvanovicm.magacin.domain.article.dto.response.ArticleResponse;
import fyi.hrvanovicm.magacin.domain.article.services.ArticleService;
import fyi.hrvanovicm.magacin.domain.common.exceptions.ResourceNotFoundException;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
public class ArticleRestController {

    private final ArticleService articleService;

    @Autowired
    public ArticleRestController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("")
    public ResponseEntity<Page<ArticleResponse>> getArticlesPaginated(
        @Valid ArticleSearchCriteriaDTO searchable,
        Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                articleService.getAll(searchable.toSpecification(), pageable)
        );
    }

    @GetMapping("all")
    public ResponseEntity<List<ArticleResponse>> getAllArticles(
        ArticleSearchCriteriaDTO searchable
    ) {
        var articles = articleService.getAll(searchable.toSpecification());
        return ResponseEntity.status(HttpStatus.OK).body(articles);
    }

    @PostMapping("")
    public ResponseEntity<ArticleResponse> createArticle(
        @RequestBody ArticleCreateRequestDTO request
    ) {
        ArticleResponse createdArticle = articleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<ArticleResponse> updateArticleById(
        @PathVariable Long articleId,
        @RequestBody ArticleUpdateRequestDTO request
    ) {
        ArticleResponse updatedArticle = articleService.update(
            articleId,
            request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedArticle);
    }

    @GetMapping("/{articleId}")
    public Article getArticleById(
        @PathVariable(name = "articleId") Long articleId
    ) {
        var result = articleService.getById(articleId);

        if (result.isEmpty()) {
            throw new ResourceNotFoundException(Article.class, articleId);
        }

        return result.get();
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteArticleById(
        @PathVariable(name = "articleId") Long articleId
    ) {
        articleService.delete(articleId);

        return ResponseEntity.noContent().build();
    }
}
