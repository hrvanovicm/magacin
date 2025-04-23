package fyi.hrvanovicm.magacin.application.report.dto;

import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportType;
import fyi.hrvanovicm.magacin.shared.dto.AuditDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportDTO {
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
    public static ReportDTO fromEntity(ReportEntity entity) {
        var dto = new ReportDTO();

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
