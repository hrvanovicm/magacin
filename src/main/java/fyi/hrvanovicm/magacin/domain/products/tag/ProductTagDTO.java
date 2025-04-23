package fyi.hrvanovicm.magacin.domain.products.tag;

import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import lombok.Data;

@Data
public class ProductTagDTO {
    Long id;
    String name;
    AuditDTO audit;

    @SuppressWarnings("DuplicatedCode")
    public static ProductTagDTO fromEntity(ProductTagEntity entity) {
        var dto = new ProductTagDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAudit(AuditDTO.fromEntity(entity.getAudit()));

        return dto;
    }
}
