package fyi.hrvanovicm.magacin.application.report.dto;

import fyi.hrvanovicm.magacin.domain.report.ShipmentReportEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShipmentReportDTO {
    private Long id;
    private String receiptCompanyName;

    public static ShipmentReportDTO fromEntity(ShipmentReportEntity entity) {
        var dto = new ShipmentReportDTO();

        dto.setId(entity.getId());
        dto.setReceiptCompanyName(entity.getReceiptCompanyName());

        return dto;
    }
}
