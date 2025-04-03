package fyi.hrvanovicm.magacin.domain.report.shipment;

import fyi.hrvanovicm.magacin.domain.products.ProductBasicResponse;
import fyi.hrvanovicm.magacin.domain.report.ReportProductEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShipmentReportResponse {
    private Long id;
    private String receiptCompanyName;

    public static ShipmentReportResponse fromEntity(ShipmentReport entity) {
        var dto = new ShipmentReportResponse();

        dto.setId(entity.getId());
        dto.setReceiptCompanyName(entity.getReceiptCompanyName());

        return dto;
    }
}
