package fyi.hrvanovicm.magacin.application.http.v1;

import fyi.hrvanovicm.magacin.application.requests.ArticleSearchCriteriaDTO;
import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.article.dto.request.ArticleCreateRequestDTO;
import fyi.hrvanovicm.magacin.domain.article.dto.request.ArticleUpdateRequestDTO;
import fyi.hrvanovicm.magacin.domain.article.dto.response.ArticleResponse;
import fyi.hrvanovicm.magacin.domain.article.services.ArticleService;
import fyi.hrvanovicm.magacin.domain.common.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles/categories")
public class ArticleCategoryController {

    private final ArticleService articleService;

    @Autowired
    public ArticleCategoryController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("")
    public ResponseEntity<Page<ArticleResponse>> getCategories(
        @Valid ArticleSearchCriteriaDTO searchable,
        Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                articleService.getAll(searchable.toSpecification(), pageable)
        );
    }
}
