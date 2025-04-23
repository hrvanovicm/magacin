package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.products.ProductRepository;
import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductEntity;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductRequest;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductUsedReceptionsEntity;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportEntity;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportRequest;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportEntity;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class ReportService {
    final ReportRepository reportRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Autowired
    public ReportService(ReportRepository reportRepository, ProductRepository productRepository, ProductService productService) {
        this.reportRepository = reportRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public List<Report> getAll(Specification<ReportEntity> specs) {
        return reportRepository
                .findAll(specs)
                .stream()
                .map(Report::fromEntity)
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

    @Transactional(readOnly = true)
    public ReportDetails get(Long reportId) {
        return reportRepository.findById(reportId)
                .map(ReportDetails::fromEntity)
                .orElseThrow();
    }

    private void saveProducts(
            ReportEntity report,
            @Valid List<ReportProductRequest> request
    ) {
        this.resetStockAmounts(report);
        report.getProducts().clear();
        report.getProducts().addAll(
          request.stream().map((req) -> {
              ProductEntity product = productRepository.findById(req.getProductId()).orElseThrow();

              var ent = new ReportProductEntity();
              ent.setReport(report);
              ent.setProduct(product);
              ent.setReceptions(req.getReceptions().stream().map((rec -> {
                  ProductEntity rawMaterial = productRepository.findById(rec.getRawMaterialId()).orElseThrow();
                  var reportProductReception = new ReportProductUsedReceptionsEntity();

                  reportProductReception.setReport(report);
                  reportProductReception.setProduct(product);
                  reportProductReception.setRawMaterialProduct(rawMaterial);
                  reportProductReception.setAmount(rec.getAmount());

                  return reportProductReception;
              })).toList());
              ent.setAmount(req.getAmount());

              return ent;
          }).toList()
        );

        this.updateStockAmounts(report);
    }

    @Transactional
    public ReportDetails saveReceipt(
        @Valid ReceiptReportRequest request
    ) {
        return this.saveReport(request, (report) -> {
            if(report.getShipmentReport() == null) {
                report.setReceiptReport(new ReceiptReportEntity());
                report.getReceiptReport().setReport(report);
            }

            report.setType(ReportType.RECEIPT);
            report.getReceiptReport().setSupplierCompanyName(request.getSupplierCompanyName());
            report.getReceiptReport().setSupplierReportCode(request.getSupplierReportCode());
            report.getReceiptReport().setIsSupplierProduction(request.getIsSupplierProduction());
        });
    }

    @Transactional
    public ReportDetails saveShipment(
            @Valid ShipmentReportRequest request
    ) {
        return this.saveReport(request, (report) -> {
            if(report.getShipmentReport() == null) {
                report.setShipmentReport(new ShipmentReportEntity());
                report.getShipmentReport().setReport(report);
            }

            report.setType(ReportType.SHIPMENT);
            report.getShipmentReport().setReceiptCompanyName(request.getReceiptCompanyName());
        });
    }

    private void resetStockAmounts(ReportEntity report) {
        report.getProducts().forEach(product -> {
            product.getReceptions().forEach(reception -> {
                this.productService.increaseStock(reception.getProduct().getId(), reception.getAmount());
            });

            if (report.getType() == ReportType.RECEIPT) {
                this.productService.decreaseStock(product.getProduct().getId(), product.getAmount());
            } else if (report.getType() == ReportType.SHIPMENT) {
                this.productService.increaseStock(product.getProduct().getId(), product.getAmount());
            }
        });
    }

    private void updateStockAmounts(ReportEntity report) {
        report.getProducts().forEach(product -> {
            product.getReceptions().forEach(reception -> {
                this.productService.decreaseStock(reception.getProduct().getId(), reception.getAmount());
            });

            if (report.getType() == ReportType.RECEIPT) {
                this.productService.increaseStock(product.getProduct().getId(), product.getAmount());
            } else if (report.getType() == ReportType.SHIPMENT) {
                this.productService.decreaseStock(product.getProduct().getId(), product.getAmount());
            }
        });
    }

    private ReportDetails saveReport(ReportRequest request, Consumer<ReportEntity> reportCallback) {
        ReportEntity report = Optional.ofNullable(request.getId())
                .flatMap(reportRepository::findById)
                .orElseGet(ReportEntity::new);

        report.setCode(request.getCode());
        report.setDate(request.getDate().toString());
        report.setDescriptionHtml(request.getDescriptionHtml());
        report.setPlaceOfPublish(request.getPlaceOfPublish());
        report.setSignedByName(request.getSignedByName());

        reportRepository.save(report);

        reportCallback.accept(report);

        this.saveProducts(report, request.getProducts());
        reportRepository.save(report);

        return ReportDetails.fromEntity(report);
    }

    @Transactional
    public void delete(Long id) {
        var report = reportRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);

        // Save products will change in stock amount.
        this.saveProducts(report, List.of());

        reportRepository.delete(report);
    }
}
