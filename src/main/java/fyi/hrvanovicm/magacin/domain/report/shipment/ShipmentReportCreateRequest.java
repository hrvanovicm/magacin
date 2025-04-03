package fyi.hrvanovicm.magacin.domain.report.shipment;

import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReport;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ShipmentReportCreateRequest {
    @NotBlank
    String code;

    @NotBlank
    LocalDate date;

    String descriptionHtml;
    String placeOfPublish;
    String signedByName;

    String receiptCompanyName;

    public ReportEntity toEntity() {
        var report = new ReportEntity();
        var receipt = new ShipmentReport();

        report.setCode(code);
        report.setDate(date.toString());
        report.setDescriptionHtml(descriptionHtml);
        report.setPlaceOfPublish(placeOfPublish);
        report.setSignedByName(signedByName);

        receipt.setReceiptCompanyName(receiptCompanyName);
        report.setShipmentReport(receipt);

        return report;
    }
}
