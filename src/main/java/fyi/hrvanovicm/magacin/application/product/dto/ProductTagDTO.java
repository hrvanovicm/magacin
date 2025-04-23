package fyi.hrvanovicm.magacin.application.product.dto;

import fyi.hrvanovicm.magacin.shared.dto.AuditDTO;
import fyi.hrvanovicm.magacin.domain.products.ProductTagEntity;
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
