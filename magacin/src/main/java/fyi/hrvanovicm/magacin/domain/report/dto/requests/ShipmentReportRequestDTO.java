package fyi.hrvanovicm.magacin.domain.report.dto.requests;

import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.article.utils.ArticleValidationRulesUtils;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public abstract sealed class ShipmentReportRequestDTO permits ShipmentCreateRequestDTO, ShipmentUpdateRequestDTO {
    @NotBlank
    @Size(max = ArticleValidationRulesUtils.NAME_MAX_CHARACTERS)
    protected String name;

    @NotBlank
    @Size(max = ArticleValidationRulesUtils.CODE_MAX_CHARACTERS)
    String code;

    @Size(max = ArticleValidationRulesUtils.DESCRIPTION_MAX_CHARACTERS)
    String description;

    @NotNull
    @PositiveOrZero
    Float inStockAmount;

    @NotNull
    @Positive
    Long jmId;

    protected Article toEntity(UnitMeasure unitMeasure) {
        var entity = new Article();

        entity.setName(name);
        entity.setCode(code);
        entity.setDescription(description);
        entity.setInStockAmount(inStockAmount);
        entity.setUnitMeasure(unitMeasure);

        return entity;
    }
}
