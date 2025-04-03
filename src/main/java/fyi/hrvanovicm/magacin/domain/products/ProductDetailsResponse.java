package fyi.hrvanovicm.magacin.domain.products;

import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionBasicResponse;
import fyi.hrvanovicm.magacin.domain.products.tag.ProductTagEntity;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductResponse;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureResponse;
import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ProductDetailsResponse {
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
    private List<ReportProductResponse> reports;
    private List<ProductReceptionBasicResponse> receptions;
    private AuditDTO audit;

    @SuppressWarnings("DuplicatedCode")
    public static ProductDetailsResponse fromEntity(ProductEntity productEntity) {
        ProductDetailsResponse dto = new ProductDetailsResponse();

        dto.setId(productEntity.getId());
        dto.setDescriptionHtml(productEntity.getDescriptionHtml());
        dto.setCategory(productEntity.getCategory());
        dto.setName(productEntity.getName());
        dto.setInStockWarningAmount(productEntity.getInStockWarningAmount());
        dto.setCode(productEntity.getCode());
        dto.setReceptions(
                productEntity.getReceptions()
                        .stream()
                        .map(ProductReceptionBasicResponse::fromEntity)
                        .toList()
        );
        dto.setReports(productEntity.getReports().stream().map(ReportProductResponse::fromEntity).collect(Collectors.toList()));
        dto.setInStockAmount(productEntity.getInStockAmount());
        dto.setUnitMeasure(UnitMeasureResponse.fromEntity(productEntity.getUnitMeasure()));
        dto.setLowInStockAmount(
                productEntity.getInStockAmount() != null && productEntity.getInStockAmount() <= productEntity.getInStockAmount()
        );
        dto.setTags(productEntity.getTags().stream().map(ProductTagEntity::getName).collect(Collectors.toList()));

        return dto;
    }

    public ProductBasicResponse toBasicResponse() {
        var productBasicResponse = new ProductBasicResponse();
        productBasicResponse.setId(this.getId());
        productBasicResponse.setName(this.getName());
        productBasicResponse.setTags(this.getTags());
        productBasicResponse.setCategory(this.getCategory());
        productBasicResponse.setCode(this.getCode());
        productBasicResponse.setAudit(this.getAudit());

        return productBasicResponse;
    }
}
