package fyi.hrvanovicm.magacin.domain.report.receipt;

import fyi.hrvanovicm.magacin.domain.report.ReportRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReceiptReportRequest extends ReportRequest {
    String supplierCompanyName;
    String supplierReportCode;
    Boolean isSupplierProduction;
}
