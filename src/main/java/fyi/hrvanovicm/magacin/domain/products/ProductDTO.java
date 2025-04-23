package fyi.hrvanovicm.magacin.domain.products;

import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionBasicResponse;
import fyi.hrvanovicm.magacin.domain.products.reports.ProductReportDTO;
import fyi.hrvanovicm.magacin.domain.products.tag.ProductTagEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureResponse;
import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private ProductCategory category;
    private String descriptionHtml;
    private String code;
    private Float inStockAmount;
    private Float inStockWarningAmount;
    private Boolean lowInStockAmount;
    private UnitMeasureResponse unitMeasure;
    private List<String> tags;
    private List<ProductReportDTO> reports;
    private List<ProductReceptionBasicResponse> receptions;
    private AuditDTO audit;

    @SuppressWarnings("DuplicatedCode")
    public static ProductDTO fromEntity(ProductEntity productEntity) {
        ProductDTO dto = new ProductDTO();

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

    @Override
    public String toString() {
        return this.name + " ( " + this.code + " )";
    }
}
