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
}
