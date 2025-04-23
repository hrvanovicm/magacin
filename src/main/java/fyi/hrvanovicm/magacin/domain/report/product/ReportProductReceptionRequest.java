package fyi.hrvanovicm.magacin.domain.report.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReportProductReceptionRequest {
    @Positive
    Long id;

    @NotNull
    @Positive
    Long rawMaterialId;

    @NotNull
    @PositiveOrZero
    Float amount;
}
