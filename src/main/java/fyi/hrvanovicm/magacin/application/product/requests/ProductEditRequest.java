package fyi.hrvanovicm.magacin.application.product.requests;

import fyi.hrvanovicm.magacin.domain.products.ProductCategory;
import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.products.ProductValidationRulesUtils;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureEntity;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductEditRequest {
    @NotBlank
    @Size(max = ProductValidationRulesUtils.NAME_MAX_CHARACTERS)
    String name;

    @NotBlank
    ProductCategory category;

    @NotBlank
    @Size(max = ProductValidationRulesUtils.CODE_MAX_CHARACTERS)
    String code;

    @Size(max = ProductValidationRulesUtils.DESCRIPTION_MAX_CHARACTERS)
    String descriptionHtml;

    @NotNull
    @PositiveOrZero
    Float inStockAmount;

    @NotNull
    @PositiveOrZero
    Float inStockWarningAmount;

    @NotNull
    @Positive
    Long jmId;

    List<ReceptionEditRequestDTO> receptions;

    public void fill(ProductEntity entity, UnitMeasureEntity unitMeasureEntity) {
        entity.setCategory(category);
        entity.setName(name);
        entity.setCode(code);
        entity.setDescriptionHtml(descriptionHtml);
        entity.setInStockAmount(inStockAmount);
        entity.setInStockWarningAmount(inStockWarningAmount);
        entity.setUnitMeasure(unitMeasureEntity);
    }
}
