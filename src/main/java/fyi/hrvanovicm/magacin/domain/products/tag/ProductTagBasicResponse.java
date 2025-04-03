package fyi.hrvanovicm.magacin.domain.products.tag;

import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import lombok.Data;

@Data
public class ProductTagBasicResponse {
    Long id;
    String name;
    AuditDTO audit;

    @SuppressWarnings("DuplicatedCode")
    public static ProductTagBasicResponse fromEntity(ProductTagEntity entity) {
        var dto = new ProductTagBasicResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAudit(AuditDTO.fromEntity(entity.getAudit()));

        return dto;
    }
}
