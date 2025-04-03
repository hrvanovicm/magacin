package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportResponse;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportResponse {
    Long id;
    ReportType type;
    String code;
    String date;
    String placeOfPublish;
    String signedByName;
    AuditDTO audit;
    ShipmentReportResponse shipment;
    ReceiptReportResponse receipt;

    @SuppressWarnings("DuplicatedCode")
    public static ReportResponse fromEntity(ReportEntity entity) {
        var dto = new ReportResponse();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setCode(entity.getCode());
        dto.setDate(entity.getDate());
        dto.setPlaceOfPublish(entity.getPlaceOfPublish());
        dto.setSignedByName(entity.getSignedByName());
        dto.setAudit(AuditDTO.fromEntity(entity.getAudit()));
        if(entity.getShipmentReport() != null) {
            dto.setShipment(ShipmentReportResponse.fromEntity(entity.getShipmentReport()));
        }
        if(entity.getReceiptReport() != null) {
            dto.setReceipt(ReceiptReportResponse.fromEntity(entity.getReceiptReport()));
        }

        return dto;
    }
}
