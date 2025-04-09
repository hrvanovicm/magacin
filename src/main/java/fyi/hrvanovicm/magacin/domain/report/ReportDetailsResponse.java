package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductResponse;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportResponse;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ReportDetailsResponse {
    Long id;
    ReportType type;
    String code;
    String descriptionHtml;
    String date;
    String placeOfPublish;
    String signedByName;
    AuditDTO audit;
    ShipmentReportResponse shipment;
    ReceiptReportResponse receipt;
    List<ReportProductResponse> products;

    @SuppressWarnings("DuplicatedCode")
    public static ReportDetailsResponse fromEntity(ReportEntity entity) {
        var dto = new ReportDetailsResponse();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setCode(entity.getCode());
        dto.setDescriptionHtml(entity.getDescriptionHtml());
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
        dto.setProducts(
                entity.getProducts().stream().map(ReportProductResponse::fromEntity).collect(Collectors.toList())
        );

        return dto;
    }
}
