package fyi.hrvanovicm.magacin.domain.report.shipment;

import fyi.hrvanovicm.magacin.domain.report.ReportRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ShipmentReportRequest extends ReportRequest {
    String receiptCompanyName;
}
