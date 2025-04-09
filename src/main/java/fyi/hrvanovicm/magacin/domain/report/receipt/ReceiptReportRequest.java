package fyi.hrvanovicm.magacin.domain.report.receipt;

import fyi.hrvanovicm.magacin.domain.report.product.ReportProductEntity;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReceiptReportRequest {
    @PositiveOrZero
    Long id;

    @NotBlank
    String code;

    @NotBlank
    LocalDate date;

    String descriptionHtml;
    String placeOfPublish;
    String signedByName;

    String supplierCompanyName;
    String supplierReportCode;

    List<ReportProductRequest> products;
}
