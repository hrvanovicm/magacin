package fyi.hrvanovicm.magacin.application.product.requests;

import fyi.hrvanovicm.magacin.application.product.dto.ProductReceptionDTO;
import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.products.ProductReceptionEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class ReceptionEditRequestDTO {
    @NotNull
    @Positive
    Long id;

    @NotNull
    @Positive
    Long rawMaterialId;

    @NotNull
    @PositiveOrZero
    Float amount;

    public void fill(
            ProductReceptionEntity reception,
            ProductEntity product,
            ProductEntity rawMaterialProduct
    ) {
        reception.setId(id);
        reception.setProduct(product);
        reception.setRawMaterialProduct(rawMaterialProduct);
        reception.setAmount(amount);
    }

    public static ReceptionEditRequestDTO fromDTO(ProductReceptionDTO dto) {
        var request = new ReceptionEditRequestDTO();
        request.setId(dto.getId());
        request.setRawMaterialId(dto.getRawMaterialProduct().getId());
        request.setAmount(dto.getAmount());
        return request;
    }
}
