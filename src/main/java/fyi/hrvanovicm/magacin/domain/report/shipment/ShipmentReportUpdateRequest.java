package fyi.hrvanovicm.magacin.domain.report.shipment;

import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ShipmentReportUpdateRequest {
    @NotBlank
    String code;

    @NotBlank
    LocalDate date;

    String descriptionHtml;
    String placeOfPublish;
    String signedByName;

    String receiptCompanyName;

    public ReportEntity toEntity(Long id, Long shipmentId) {
        var report = new ReportEntity();
        var shipment = new ShipmentReportEntity();

        report.setId(id);
        report.setCode(code);
        report.setDate(date.toString());
        report.setDescriptionHtml(descriptionHtml);
        report.setPlaceOfPublish(placeOfPublish);
        report.setSignedByName(signedByName);

        shipment.setId(shipmentId);
        shipment.setReceiptCompanyName(receiptCompanyName);
        report.setShipmentReport(shipment);

        return report;
    }
}
