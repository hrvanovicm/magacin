package fyi.hrvanovicm.magacin.domain.products;

import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class ProductUpdateRequest {
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

    public ProductEntity toEntity(Long id, UnitMeasure unitMeasure) {
        var entity = new ProductEntity();

        entity.setId(id);
        entity.setCategory(category);
        entity.setName(name);
        entity.setCode(code);
        entity.setDescriptionHtml(descriptionHtml);
        entity.setInStockAmount(inStockAmount);
        entity.setInStockWarningAmount(inStockWarningAmount);
        entity.setUnitMeasure(unitMeasure);

        return entity;
    }
}
