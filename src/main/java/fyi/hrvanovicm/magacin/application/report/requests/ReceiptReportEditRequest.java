package fyi.hrvanovicm.magacin.application.report.requests;

import fyi.hrvanovicm.magacin.domain.report.ReceiptReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReceiptReportEditRequest extends ReportEditRequest {
    String supplierCompanyName;
    String supplierReportCode;
    Boolean isSupplierProduction;

    @Override
    public void fill(ReportEntity report) {
        super.fill(report);

        report.setType(ReportType.RECEIPT);
        if(report.getReceiptReport() == null) {
            report.setReceiptReport(new ReceiptReportEntity());
        }

        report.getReceiptReport().setSupplierCompanyName(this.getSupplierCompanyName());
        report.getReceiptReport().setSupplierReportCode(this.getSupplierReportCode());
        report.getReceiptReport().setIsSupplierProduction(this.getIsSupplierProduction());
    }
}
