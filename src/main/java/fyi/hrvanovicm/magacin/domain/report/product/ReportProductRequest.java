package fyi.hrvanovicm.magacin.domain.report.product;

import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
