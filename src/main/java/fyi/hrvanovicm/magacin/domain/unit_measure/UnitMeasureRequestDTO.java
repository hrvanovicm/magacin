package fyi.hrvanovicm.magacin.domain.unit_measure;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public abstract sealed class UnitMeasureRequestDTO permits UnitMeasureCreateRequestDTO, UnitMeasureUpdateRequestDTO {
    @NotBlank
    @Size(max = UnitMeasureValidationUtils.NAME_MAX_CHARACTERS)
    String name;

    @NotBlank
    @Size(max = UnitMeasureValidationUtils.SHORTNAME_MAX_CHARACTERS)
    String shortName;

    @NotNull
    Boolean isInteger;

    protected UnitMeasure toEntity() {
        var entity = new UnitMeasure();

        entity.setName(name);
        entity.setShortName(shortName);
        entity.setIsInteger(isInteger);

        return entity;
    }
}
