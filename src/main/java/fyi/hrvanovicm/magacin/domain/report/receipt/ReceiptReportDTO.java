package fyi.hrvanovicm.magacin.domain.report.receipt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReceiptReportDTO {
    private Long id;
    private String supplierCompanyName;
    private String supplierReportCode;
    private Boolean isSupplierProduction;

    public static ReceiptReportDTO fromEntity(ReceiptReportEntity entity) {
        var dto = new ReceiptReportDTO();

        dto.setId(entity.getId());
        dto.setSupplierReportCode(entity.getSupplierReportCode());
        if(entity.getIsSupplierProduction()) {
            dto.setSupplierCompanyName("Proizvodnja");
        }
        dto.setIsSupplierProduction(entity.getIsSupplierProduction());

        return dto;
    }
}
