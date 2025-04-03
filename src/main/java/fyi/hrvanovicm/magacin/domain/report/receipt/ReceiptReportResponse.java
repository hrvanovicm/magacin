package fyi.hrvanovicm.magacin.domain.report.receipt;

import fyi.hrvanovicm.magacin.domain.products.ProductBasicResponse;
import fyi.hrvanovicm.magacin.domain.report.ReportProductEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReceiptReportResponse {
    private Long id;
    private String supplierCompanyName;
    private String supplierReportCode;

    public static ReceiptReportResponse fromEntity(ReceiptReport entity) {
        var dto = new ReceiptReportResponse();

        dto.setId(entity.getId());
        dto.setSupplierReportCode(entity.getSupplierReportCode());
        dto.setSupplierCompanyName(entity.getSupplierCompanyName());

        return dto;
    }
}
