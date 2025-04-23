package fyi.hrvanovicm.magacin.domain.products;

import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionBasicResponse;
import fyi.hrvanovicm.magacin.domain.products.reports.ProductReportDTO;
import fyi.hrvanovicm.magacin.domain.products.tag.ProductTagEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ProductDetailsDTO extends ProductDTO {
    public static ProductDetailsDTO fromEntity(ProductEntity productEntity) {
        ProductDetailsDTO dto = new ProductDetailsDTO();

        dto.setId(productEntity.getId());
        dto.setCategory(productEntity.getCategory());
        dto.setName(productEntity.getName());
        dto.setCode(productEntity.getCode());
        dto.setLowInStockAmount(
                productEntity.getInStockAmount() != null && productEntity.getInStockAmount() <= productEntity.getInStockAmount()
        );
        dto.setInStockAmount(productEntity.getInStockAmount());
        dto.setUnitMeasure(UnitMeasureResponse.fromEntity(productEntity.getUnitMeasure()));
        dto.setLowInStockAmount(
                productEntity.getInStockAmount() != null && productEntity.getInStockAmount() <= productEntity.getInStockAmount()
        );
        dto.setReports(productEntity.getReports().stream()
                .map(ProductReportDTO::fromEntity)
                .collect(Collectors.toList()));
        dto.setReceptions(productEntity.getReceptions().stream()
                .map(ProductReceptionBasicResponse::fromEntity)
                .collect(Collectors.toList()));
        dto.setTags(productEntity.getTags().stream()
                .map(ProductTagEntity::getName)
                .collect(Collectors.toList()));

        return dto;
    }
}
