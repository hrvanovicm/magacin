package fyi.hrvanovicm.magacin.application.unit_measure.requests;

import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureValidationUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UnitMeasureEditRequest {
    @NotBlank
    @NotNull
    @Size(max = UnitMeasureValidationUtils.NAME_MAX_CHARACTERS)
    String name;

    @NotBlank
    @NotNull
    @Size(max = UnitMeasureValidationUtils.SHORTNAME_MAX_CHARACTERS)
    String shortName;

    @NotNull
    Boolean isInteger;

    public void fill(UnitMeasureEntity entity) {
        entity.setName(this.name);
        entity.setShortName(this.shortName);
        entity.setIsInteger(this.isInteger);
    }
}
