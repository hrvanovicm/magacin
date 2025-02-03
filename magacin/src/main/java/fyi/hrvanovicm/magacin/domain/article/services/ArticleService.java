package fyi.hrvanovicm.magacin.domain.article.services;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.article.dto.request.ArticleCreateRequestDTO;
import fyi.hrvanovicm.magacin.domain.article.dto.request.ArticleUpdateRequestDTO;
import fyi.hrvanovicm.magacin.domain.article.dto.response.ArticleResponse;
import fyi.hrvanovicm.magacin.domain.unit_measure.services.UnitMeasureService;
import fyi.hrvanovicm.magacin.domain.article.repositories.ArticleRepository;
import jakarta.validation.Valid;

@Component
public class ArticleService {

    private final ArticleRepository repository;
    private final UnitMeasureService unitMeasureService;

    @Autowired
    public ArticleService(
        final ArticleRepository articleRepository,
        final UnitMeasureService unitMeasureService
    ) {
        this.repository = articleRepository;
        this.unitMeasureService = unitMeasureService;
    }

    public List<ArticleResponse> getAll(Specification<Article> specs) {
        return repository
            .findAll(specs)
            .stream()
            .map(ArticleResponse::fromEntity)
            .toList();
    }

    public Page<ArticleResponse> getAll(
        Specification<Article> specs,
        Pageable pageable
    ) {
        return repository
            .findAll(specs, pageable)
            .map(ArticleResponse::fromEntity);
    }

    public Optional<Article> getById(long articleId) {
        return repository.findById(articleId);
    }

    @Transactional
    public ArticleResponse create(@Valid ArticleCreateRequestDTO request) {
        var unitMeasure = unitMeasureService
            .getById(request.getJmId())
            .orElseThrow()
            .toEntity();
        var article = request.toEntity(unitMeasure);

        repository.save(article);

        return ArticleResponse.fromEntity(article);
    }

    @Transactional
    public ArticleResponse update(
        @NotNull Long articleId,
        @Valid ArticleUpdateRequestDTO request
    ) {
        var unitMeasure = unitMeasureService
            .getById(request.getJmId())
            .orElseThrow()
            .toEntity();
        var article = request.toEntity(articleId, unitMeasure);

        repository.save(article);

        return ArticleResponse.fromEntity(article);
    }

    @Transactional
    public void delete(@NotNull Long id) {
        var article = repository.findById(id).orElseThrow();
        repository.delete(article);
    }

    @Transactional
    public void forceDelete(@NotNull Long id) {
        var article = repository.findById(id).orElseThrow();
        repository.forceDeleteById(article.getId());
    }
}
