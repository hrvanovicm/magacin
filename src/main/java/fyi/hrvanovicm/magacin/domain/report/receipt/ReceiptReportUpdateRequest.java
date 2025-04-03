package fyi.hrvanovicm.magacin.domain.report.receipt;

import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReceiptReportUpdateRequest {
    @NotBlank
    String code;

    @NotBlank
    LocalDate date;

    String descriptionHtml;
    String placeOfPublish;
    String signedByName;

    String supplierCompanyName;
    String supplierReportCode;

    public ReportEntity toEntity(Long id, Long receiptId) {
        var report = new ReportEntity();
        var receipt = new ReceiptReport();

        report.setId(id);
        report.setCode(code);
        report.setDate(date.toString());
        report.setDescriptionHtml(descriptionHtml);
        report.setPlaceOfPublish(placeOfPublish);
        report.setSignedByName(signedByName);

        receipt.setId(receiptId);
        receipt.setSupplierCompanyName(supplierCompanyName);
        receipt.setSupplierReportCode(supplierReportCode);
        report.setReceiptReport(receipt);

        return report;
    }
}
