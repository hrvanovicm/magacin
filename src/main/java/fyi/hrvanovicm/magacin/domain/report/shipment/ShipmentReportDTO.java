package fyi.hrvanovicm.magacin.domain.report.shipment;

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
