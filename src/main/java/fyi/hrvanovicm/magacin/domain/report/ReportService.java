package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.infrastructure.persistance.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReportService {
    final ReportRepository reportRepository;
    private final ProductService productService;

    @Autowired
    public ReportService(ReportRepository reportRepository, ProductService productService) {
        this.reportRepository = reportRepository;
        this.productService = productService;
    }

    public List<ReportEntity> findAll(Specification<ReportEntity> specs) {
        return reportRepository.findAll(specs);
    }

    public List<ReportType> findAllReportTypes() {
        return Arrays.stream(ReportType.values()).collect(Collectors.toList());
    }

    public List<String> findAllCompanyNames() {
        return reportRepository
                .findDistinctSupplierCompanyNames()
                .stream()
                .collect(Collectors.toList());
    }

    public List<String> findAllSignedUserNames() {
        return reportRepository
                .findDistinctSignedUserNames()
                .stream()
                .collect(Collectors.toList());
    }

    public Optional<ReportEntity> findById(long reportId) {
        return reportRepository.findById(reportId);
    }

    public boolean exists(long reportId) {
        return reportRepository.existsById(reportId);
    }

    public void saveProducts(
            long reportId,
            List<ReportProductEntity> products
    ) {
        var report = this.reportRepository.findById(reportId).orElseThrow();

        this.resetStockAmounts(report);

        report.getProducts().clear();
        report.getProducts().addAll(products.stream().peek(product -> {
            if(!report.getType().equals(ReportType.RECEIPT) || !report.getReceiptReport().getIsSupplierProduction()) {
                product.getUsedReceptions().clear();
            }
        }).collect(Collectors.toList()));
        reportRepository.save(report);

        this.updateStockAmounts(report);
    }

    private void resetStockAmounts(ReportEntity report) {
        report.getProducts().forEach(product -> {
            product.getUsedReceptions().forEach(reception -> {
                this.productService.increaseStock(reception.getRawMaterialProduct().getId(), reception.getAmount());
            });

            if (report.getType() == ReportType.RECEIPT) {
                this.productService.decreaseStock(product.getProduct().getId(), product.getAmount());
            } else if (report.getType() == ReportType.SHIPMENT) {
                this.productService.increaseStock(product.getProduct().getId(), product.getAmount());
            }
        });
    }

    /**
     * On every call in stock will be decreased or increased.
     * Be aware because if you don't call the method 'resetStockAmounts'
     * you can increase/decrease multiple times instead of one.
     */
    private void updateStockAmounts(ReportEntity report) {
        report.getProducts().forEach(product -> {
            product.getUsedReceptions().forEach(reception -> {
                this.productService.decreaseStock(reception.getRawMaterialProduct().getId(), reception.getAmount());
            });

            if (report.getType() == ReportType.RECEIPT) {
                this.productService.increaseStock(product.getProduct().getId(), product.getAmount());
            } else if (report.getType() == ReportType.SHIPMENT) {
                this.productService.decreaseStock(product.getProduct().getId(), product.getAmount());
            }
        });
    }

    public void save(ReportEntity report) {
        this.reportRepository.save(report);
    }

    public void delete(Long id) {
        var report = reportRepository.findById(id).orElseThrow();

        // First reset product stock amount.
        this.resetStockAmounts(report);

        reportRepository.delete(report);
    }
}
