package fyi.hrvanovicm.magacin.application.report.dto;

import fyi.hrvanovicm.magacin.domain.report.ReceiptReportEntity;
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
        dto.setSupplierCompanyName(entity.getSupplierCompanyName());
        if(entity.getIsSupplierProduction()) {
            dto.setSupplierCompanyName("Proizvodnja");
        }
        dto.setIsSupplierProduction(entity.getIsSupplierProduction());

        return dto;
    }
}
