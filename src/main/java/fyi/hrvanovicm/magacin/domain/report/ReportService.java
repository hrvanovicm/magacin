package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.products.ProductBasicResponse;
import fyi.hrvanovicm.magacin.domain.products.ProductUpdateRequest;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportUpdateRequest;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportUpdateRequest;
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

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
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

    @Transactional
    public ReportDetailsResponse updateReceipt(
            @NotNull Long reportId,
            @Valid ReceiptReportUpdateRequest request
    ) {
        var report = reportRepository.findById(reportId).orElseThrow();

        report.setCode(request.getCode());
        report.setDate(request.getDate().toString());
        report.setDescriptionHtml(request.getDescriptionHtml());
        report.setPlaceOfPublish(request.getPlaceOfPublish());
        report.setSignedByName(request.getSignedByName());

        report.getReceiptReport().setSupplierCompanyName(request.getSupplierCompanyName());
        report.getReceiptReport().setSupplierReportCode(request.getSupplierReportCode());

        reportRepository.save(report);

        return ReportDetailsResponse.fromEntity(report);
    }

    @Transactional
    public ReportDetailsResponse updateShipment(
            @NotNull Long reportId,
            @Valid ShipmentReportUpdateRequest request
    ) {
        var report = reportRepository.findById(reportId).orElseThrow();

        report.setCode(request.getCode());
        report.setDate(request.getDate().toString());
        report.setDescriptionHtml(request.getDescriptionHtml());
        report.setPlaceOfPublish(request.getPlaceOfPublish());
        report.setSignedByName(request.getSignedByName());

        report.getShipmentReport().setReceiptCompanyName(request.getReceiptCompanyName());

        reportRepository.save(report);

        return ReportDetailsResponse.fromEntity(report);
    }
}
