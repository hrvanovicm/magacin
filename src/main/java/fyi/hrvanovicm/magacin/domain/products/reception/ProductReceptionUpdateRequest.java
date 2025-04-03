package fyi.hrvanovicm.magacin.domain.products.reception;

import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class ProductReceptionUpdateRequest {
    @NotNull
    @Positive
    Long receptionId;

    @NotNull
    @Positive
    Long productId;

    @NotNull
    @Positive
    Long rawMaterialId;

    @NotNull
    @PositiveOrZero
    Float amount;

    public ProductReceptionEntity toEntity(
            ProductEntity product,
            ProductEntity rawMaterialProduct
    ) {
        var entity = new ProductReceptionEntity();

        entity.setId(product.getId());
        entity.setProduct(product);
        entity.setRawMaterialProduct(rawMaterialProduct);
        entity.setAmount(amount);

        return entity;
    }
}
