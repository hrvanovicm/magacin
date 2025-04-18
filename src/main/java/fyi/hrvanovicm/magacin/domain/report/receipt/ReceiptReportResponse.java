package fyi.hrvanovicm.magacin.domain.report.receipt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReceiptReportResponse {
    private Long id;
    private String supplierCompanyName;
    private String supplierReportCode;

    public static ReceiptReportResponse fromEntity(ReceiptReportEntity entity) {
        var dto = new ReceiptReportResponse();

        dto.setId(entity.getId());
        dto.setSupplierReportCode(entity.getSupplierReportCode());
        dto.setSupplierCompanyName(entity.getSupplierCompanyName());

        return dto;
    }
}
