package fyi.hrvanovicm.magacin.application.report.requests;

import fyi.hrvanovicm.magacin.application.report.dto.ReportProductReceptionDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ReportProductRequest {
    @Positive
    Long id;

    @NotNull
    @Positive
    Long productId;

    @NotNull
    @PositiveOrZero
    Float amount;

    List<ReportProductReceptionDTO> receptions = new ArrayList<>();
}
