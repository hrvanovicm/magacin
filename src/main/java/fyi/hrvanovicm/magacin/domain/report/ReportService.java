package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.products.ProductRepository;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductEntity;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductRequest;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportRequest;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportService {
    final ReportRepository reportRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository, ProductRepository productRepository) {
        this.reportRepository = reportRepository;
        this.productRepository = productRepository;
    }

    public List<ReportResponse> getAll(Specification<ReportEntity> specs) {
        return reportRepository
                .findAll(specs)
                .stream()
                .map(ReportResponse::fromEntity)
                .toList();
    }

    public List<ReportType> getAllReportTypes() {
        return Arrays.stream(ReportType.values()).collect(Collectors.toList());
    }

    public List<String> getAllCompanyNames() {
        return reportRepository
                .findDistinctSupplierCompanyNames()
                .stream()
                .collect(Collectors.toList());
    }

    public List<String> getAllSignedUserNames() {
        return reportRepository
                .findDistinctSignedUserNames()
                .stream()
                .collect(Collectors.toList());
    }

    public ReportDetailsResponse get(Long reportId) {
        return reportRepository.findById(reportId)
                .map(ReportDetailsResponse::fromEntity)
                .orElseThrow();
    }

    private void saveProducts(
            ReportEntity report,
            @Valid List<ReportProductRequest> request
    ) {
        List<ReportProductEntity> products = request.stream().map(req -> {
            ProductEntity product = productRepository.findById(req.getProductId()).orElseThrow();

           var productEntity = new ReportProductEntity();
           productEntity.setId(req.getId());
           productEntity.setReport(report);
           productEntity.setProduct(product);
           productEntity.setAmount(req.getAmount());

           return productEntity;
        }).collect(Collectors.toList());

        report.setProducts(products);
    }

    @Transactional
    public ReportDetailsResponse saveReceipt(
        @Valid ReceiptReportRequest request
    ) {
        ReportEntity report = (request.getId() == null)
                ? new ReportEntity()
                : reportRepository.findById(request.getId()).orElseThrow();

        report.setCode(request.getCode());
        report.setDate(request.getDate().toString());
        report.setDescriptionHtml(request.getDescriptionHtml());
        report.setPlaceOfPublish(request.getPlaceOfPublish());
        report.setSignedByName(request.getSignedByName());

        report.getReceiptReport().setSupplierCompanyName(request.getSupplierCompanyName());
        report.getReceiptReport().setSupplierReportCode(request.getSupplierReportCode());

        this.saveProducts(report, request.getProducts());
        reportRepository.save(report);

        return ReportDetailsResponse.fromEntity(report);
    }

    @Transactional
    public ReportDetailsResponse saveShipment(
            @Valid ShipmentReportRequest request
    ) {
        ReportEntity report = (request.getId() == null)
                ? new ReportEntity()
                : reportRepository.findById(request.getId()).orElseThrow();

        report.setCode(request.getCode());
        report.setDate(request.getDate().toString());
        report.setDescriptionHtml(request.getDescriptionHtml());
        report.setPlaceOfPublish(request.getPlaceOfPublish());
        report.setSignedByName(request.getSignedByName());

        report.getShipmentReport().setReceiptCompanyName(request.getReceiptCompanyName());

        reportRepository.save(report);

        this.saveProducts(report, request.getProducts());
        reportRepository.save(report);

        return ReportDetailsResponse.fromEntity(report);
    }
}
