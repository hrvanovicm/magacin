package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductDTO;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportDTO;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ReportDetails {
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
    public static ReportDetails fromEntity(ReportEntity entity) {
        var dto = new ReportDetails();
 
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
