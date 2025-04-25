package fyi.hrvanovicm.magacin.application.report.commands;

import fyi.hrvanovicm.magacin.application.BaseHandler;
import fyi.hrvanovicm.magacin.application.report.dto.ReportDetailsDTO;
import fyi.hrvanovicm.magacin.application.report.requests.ReportEditRequest;
import fyi.hrvanovicm.magacin.application.report.requests.ReportProductRequest;
import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportProductEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportProductUsedReceptionsEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateReportHandler extends BaseHandler {
    private final ReportService reportService;
    private final ProductService productService;

    @Autowired
    public CreateReportHandler(ReportService reportService, ProductService productService) {
        this.reportService = reportService;
        this.productService = productService;
    }

    @Transactional
    public ReportDetailsDTO handle(@Valid ReportEditRequest request) {
        var report = new ReportEntity();
        request.fill(report);
        this.reportService.save(report);

        this.handleReportProducts(report, request.getProducts());

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
            reportProduct.setUsedReceptions(receptions);
            return reportProduct;
        }).toList();

        this.reportService.saveProducts(report.getId(), products);
    }
}
