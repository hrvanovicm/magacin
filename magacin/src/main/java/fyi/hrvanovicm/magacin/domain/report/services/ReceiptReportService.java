package fyi.hrvanovicm.magacin.domain.report.services;

import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.article.dto.request.ArticleCreateRequestDTO;
import fyi.hrvanovicm.magacin.domain.article.dto.request.ArticleUpdateRequestDTO;
import fyi.hrvanovicm.magacin.domain.article.dto.response.ArticleResponse;
import fyi.hrvanovicm.magacin.domain.report.repositories.ReceiptReportRepository;
import fyi.hrvanovicm.magacin.domain.report.repositories.ReportRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class ReceiptReportService {
    final ReportRepository reportRepository;
    final ReceiptReportRepository receiptReportRepository;

    @Autowired
    public ReceiptReportService(ReportRepository reportRepository, ReceiptReportRepository receiptReportRepository) {
        this.reportRepository = reportRepository;
        this.receiptReportRepository = receiptReportRepository;
    }

//    public List<ArticleResponse> getAll(Specification<Article> specs) {
//        return receiptReportRepository
//                .findAll(specs)
//                .stream()
//                .map(ArticleResponse::fromEntity)
//                .toList();
//    }
//
//    public Page<ArticleResponse> getAll(
//            Specification<Article> specs,
//            Pageable pageable
//    ) {
//        return repository
//                .findAll(specs, pageable)
//                .map(ArticleResponse::fromEntity);
//    }
//
//    public Optional<Article> getById(long articleId) {
//        return repository.findById(articleId);
//    }
//
//    @Transactional
//    public ArticleResponse create(@Valid ArticleCreateRequestDTO request) {
//        var unitMeasure = unitMeasureService
//                .getById(request.getJmId())
//                .orElseThrow()
//                .toEntity();
//        var article = request.toEntity(unitMeasure);
//
//        repository.save(article);
//
//        return ArticleResponse.fromEntity(article);
//    }
//
//    @Transactional
//    public ArticleResponse update(
//            @NotNull Long articleId,
//            @Valid ArticleUpdateRequestDTO request
//    ) {
//        var unitMeasure = unitMeasureService
//                .getById(request.getJmId())
//                .orElseThrow()
//                .toEntity();
//        var article = request.toEntity(articleId, unitMeasure);
//
//        repository.save(article);
//
//        return ArticleResponse.fromEntity(article);
//    }
//
//    @Transactional
//    public void delete(@NotNull Long id) {
//        var article = repository.findById(id).orElseThrow();
//        repository.delete(article);
//    }
//
//    @Transactional
//    public void forceDelete(@NotNull Long id) {
//        var article = repository.findById(id).orElseThrow();
//        repository.forceDeleteById(article.getId());
//    }
}
