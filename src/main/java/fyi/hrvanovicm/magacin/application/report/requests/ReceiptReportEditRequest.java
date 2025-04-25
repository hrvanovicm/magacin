package fyi.hrvanovicm.magacin.application.report.requests;

import fyi.hrvanovicm.magacin.domain.report.ReceiptReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportType;
import fyi.hrvanovicm.magacin.domain.report.ReportValidationRulesUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public final class ReceiptReportEditRequest extends ReportEditRequest {
    @Size(
            max = ReportValidationRulesUtils.COMPANY_NAME_MAX_CHARACTERS,
            message = "Polje dobavljač sadrži previše karaktera!"
    )
    String supplierCompanyName;

    @Size(
            max = ReportValidationRulesUtils.COMPANY_NAME_MAX_CHARACTERS,
            message = "Polje šifra dobavljača sadrži previše karaktera!"
    )
    String supplierReportCode;

    @NotNull(message = "Polje da li je proizvodnja ne može biti prazno.")
    Boolean isSupplierProduction;

    @Override
    public void fill(ReportEntity report) {
        super.fill(report);

        report.setType(ReportType.RECEIPT);
        if(report.getReceiptReport() == null) {
            report.setReceiptReport(new ReceiptReportEntity());
            report.getReceiptReport().setReport(report);
        }

        report.getReceiptReport().setSupplierCompanyName(this.getSupplierCompanyName());
        report.getReceiptReport().setSupplierReportCode(this.getSupplierReportCode());
        report.getReceiptReport().setIsSupplierProduction(this.getIsSupplierProduction());
    }
}
