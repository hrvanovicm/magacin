package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportDTO;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Report {
    Long id;
    ReportType type;
    String code;
    String date;
    String placeOfPublish;
    String signedByName;
    AuditDTO audit;
    ShipmentReportDTO shipment;
    ReceiptReportDTO receipt;

    @SuppressWarnings("DuplicatedCode")
    public static Report fromEntity(ReportEntity entity) {
        var dto = new Report();

        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setCode(entity.getCode());
        dto.setDate(entity.getDate());
        dto.setPlaceOfPublish(entity.getPlaceOfPublish());
        dto.setSignedByName(entity.getSignedByName());
        dto.setAudit(AuditDTO.fromEntity(entity.getAudit()));

        if(entity.getShipmentReport() != null) {
            dto.setShipment(ShipmentReportDTO.fromEntity(entity.getShipmentReport()));
        }
        if(entity.getReceiptReport() != null) {
            dto.setReceipt(ReceiptReportDTO.fromEntity(entity.getReceiptReport()));
        }

        return dto;
    }
}
