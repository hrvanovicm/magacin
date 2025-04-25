package fyi.hrvanovicm.magacin.application.unit_measure.requests;

import fyi.hrvanovicm.magacin.application.unit_measure.dto.UnitMeasureDTO;
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
    @NotBlank(message = "Polje naziv ne može biti prazno!")
    @Size(
            max = UnitMeasureValidationUtils.NAME_MAX_CHARACTERS,
            message = "Polje naziv sadrži previše karaktera!"
    )
    String name;

    @NotBlank(message = "Polje kratki naziv ne može biti prazno!")
    @Size(
            max = UnitMeasureValidationUtils.SHORTNAME_MAX_CHARACTERS,
            message = "Polje kratki naziv sadrži previše karaktera!"
    )
    String shortName;

    @NotNull(message = "Polje da li je cjelokupna vrijednost ne može biti prazno!")
    Boolean isInteger;

    public void fill(UnitMeasureEntity entity) {
        entity.setName(this.name);
        entity.setShortName(this.shortName);
        entity.setIsInteger(this.isInteger);
    }

    public static UnitMeasureEditRequest from(UnitMeasureDTO unitMeasureDTO) {
        var request = new UnitMeasureEditRequest();
        request.name = unitMeasureDTO.getName();
        request.shortName = unitMeasureDTO.getShortName();
        request.isInteger = unitMeasureDTO.getIsInteger();
        return request;
    }
}
