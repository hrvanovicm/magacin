package fyi.hrvanovicm.magacin.application.report.dto;

import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportType;
import fyi.hrvanovicm.magacin.shared.dto.AuditDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ReportDetailsDTO {
    Long id;
    ReportType type;
    String code;
    String descriptionHtml;
    String date;
    String placeOfPublish;
    String signedByName;
    AuditDTO audit;
    ShipmentReportDTO shipment;
    ReceiptReportDTO receipt;
    List<ReportProductDTO> products;

    @SuppressWarnings("DuplicatedCode")
    public static ReportDetailsDTO fromEntity(ReportEntity entity) {
        var dto = new ReportDetailsDTO();
 
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setCode(entity.getCode());
        dto.setDescriptionHtml(entity.getDescriptionHtml());
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
        dto.setProducts(
                entity.getProducts().stream().map(ReportProductDTO::fromEntity).collect(Collectors.toList())
        );

        return dto;
    }
}
