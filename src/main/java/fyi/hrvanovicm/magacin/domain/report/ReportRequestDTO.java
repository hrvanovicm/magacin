package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.products.ProductValidationRulesUtils;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public abstract sealed class ReportRequestDTO permits ReportCreateRequestDTO, ReportUpdateRequestDTO {
    @NotBlank
    @Size(max = ProductValidationRulesUtils.NAME_MAX_CHARACTERS)
    protected String name;

    @NotBlank
    @Size(max = ProductValidationRulesUtils.CODE_MAX_CHARACTERS)
    String code;

    @Size(max = ProductValidationRulesUtils.DESCRIPTION_MAX_CHARACTERS)
    String description;

    @NotNull
    @PositiveOrZero
    Float inStockAmount;

    @NotNull
    @Positive
    Long jmId;

    protected ProductEntity toEntity(UnitMeasure unitMeasure) {
        var entity = new ProductEntity();

        entity.setName(name);
        entity.setCode(code);
        entity.setDescriptionHtml(description);
        entity.setInStockAmount(inStockAmount);
        entity.setUnitMeasure(unitMeasure);

        return entity;
    }
}
