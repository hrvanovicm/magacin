package fyi.hrvanovicm.magacin.domain.unit_measure;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UnitMeasureRequest {
    @Positive
    Long id;

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
}
