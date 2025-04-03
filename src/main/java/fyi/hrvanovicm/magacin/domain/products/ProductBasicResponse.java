package fyi.hrvanovicm.magacin.domain.products;

import fyi.hrvanovicm.magacin.domain.products.tag.ProductTagEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureResponse;
import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProductBasicResponse {
    private Long id;
    private String name;
    private ProductCategory category;
    private String descriptionHtml;
    private String code;
    private Float inStockAmount;
    private Boolean lowInStockAmount;
    private UnitMeasureResponse unitMeasure;
    private List<String> tags;
    private List<?> reports;
    private AuditDTO audit;

    @SuppressWarnings("DuplicatedCode")
    public static ProductBasicResponse fromEntity(ProductEntity productEntity) {
        ProductBasicResponse dto = new ProductBasicResponse();

        dto.setId(productEntity.getId());
        dto.setReports(productEntity.getReports());
        dto.setCategory(productEntity.getCategory());
        dto.setName(productEntity.getName());
        dto.setCode(productEntity.getCode());
        dto.setInStockAmount(productEntity.getInStockAmount());
        dto.setUnitMeasure(UnitMeasureResponse.fromEntity(productEntity.getUnitMeasure()));
        dto.setLowInStockAmount(
                productEntity.getInStockAmount() != null && productEntity.getInStockAmount() <= productEntity.getInStockAmount()
        );
        dto.setTags(productEntity.getTags().stream().map(ProductTagEntity::getName).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public String toString() {
        return getName();
    }
}
