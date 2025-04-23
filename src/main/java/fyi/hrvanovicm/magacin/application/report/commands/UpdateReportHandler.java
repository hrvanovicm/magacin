package fyi.hrvanovicm.magacin.application.report.commands;

import fyi.hrvanovicm.magacin.application.product.dto.ProductDetailsDTO;
import fyi.hrvanovicm.magacin.application.product.requests.ProductEditRequest;
import fyi.hrvanovicm.magacin.application.report.dto.ReportDetailsDTO;
import fyi.hrvanovicm.magacin.application.report.requests.ReceiptReportEditRequest;
import fyi.hrvanovicm.magacin.application.report.requests.ReportEditRequest;
import fyi.hrvanovicm.magacin.application.report.requests.ReportProductRequest;
import fyi.hrvanovicm.magacin.application.report.requests.ShipmentReportRequest;
import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.products.ProductReceptionEntity;
import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportProductEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportProductUsedReceptionsEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportService;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdateReportHandler {
    private final ReportService reportService;
    private final ProductService productService;

    @Autowired
    public UpdateReportHandler(ReportService reportService, ProductService productService) {
        this.reportService = reportService;
        this.productService = productService;
    }

    @Transactional
    public ReportDetailsDTO handle(long reportId, @Valid ReportEditRequest request) {
        var report = this.reportService.findById(reportId).orElseThrow(EntityNotFoundException::new);
        request.fill(report);
        this.reportService.save(report);

        this.handleReportProducts(report, request.getProducts());

        report = this.reportService.findById(reportId).orElseThrow(EntityNotFoundException::new);
        return ReportDetailsDTO.fromEntity(report);
    }

    private void handleReportProducts(ReportEntity report, List<ReportProductRequest> productsRequest) {
        var products = productsRequest.stream().map(request -> {
            var product = this.productService.getById(request.getProductId()).orElseThrow(EntityNotFoundException::new);
            var receptions = request.getReceptions().stream().map(receptionRequest -> {
                var rawMaterialProduct = this.productService.getById(receptionRequest.getRawMaterialId()).orElseThrow(EntityNotFoundException::new);
                var reportProductReception = new ReportProductUsedReceptionsEntity();
                reportProductReception.setProduct(product);
                reportProductReception.setRawMaterialProduct(rawMaterialProduct);
                reportProductReception.setAmount(receptionRequest.getAmount());
                return reportProductReception;
            }).toList();

            var reportProduct = new ReportProductEntity();
            reportProduct.setProduct(product);
            reportProduct.setReport(report);
            reportProduct.setAmount(request.getAmount());
            reportProduct.setReceptions(receptions);
            return reportProduct;
        }).toList();

        this.reportService.saveProducts(report.getId(), products);
    }
}
