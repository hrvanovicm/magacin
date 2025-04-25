package fyi.hrvanovicm.magacin.application.report.requests;

import fyi.hrvanovicm.magacin.domain.report.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public final class ShipmentReportEditRequest extends ReportEditRequest {
    @Size(
            max = ReportValidationRulesUtils.COMPANY_NAME_MAX_CHARACTERS,
            message = "Polje primaoc sadrži previše karaktera!"
    )
    String receiptCompanyName;

    @Override
    public void fill(ReportEntity report) {
        super.fill(report);

        report.setType(ReportType.SHIPMENT);
        if(report.getShipmentReport() == null) {
            report.setShipmentReport(new ShipmentReportEntity());
            report.getShipmentReport().setReport(report);
        }

        report.getShipmentReport().setReceiptCompanyName(this.getReceiptCompanyName());
    }
}
