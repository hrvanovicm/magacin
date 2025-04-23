package fyi.hrvanovicm.magacin.application.report.requests;

import fyi.hrvanovicm.magacin.domain.report.ReceiptReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportType;
import fyi.hrvanovicm.magacin.domain.report.ShipmentReportEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ShipmentReportRequest extends ReportEditRequest {
    String receiptCompanyName;

    @Override
    public void fill(ReportEntity report) {
        super.fill(report);

        report.setType(ReportType.SHIPMENT);
        if(report.getShipmentReport() == null) {
            report.setShipmentReport(new ShipmentReportEntity());
        }

        report.getShipmentReport().setReceiptCompanyName(this.getReceiptCompanyName());
    }
}
