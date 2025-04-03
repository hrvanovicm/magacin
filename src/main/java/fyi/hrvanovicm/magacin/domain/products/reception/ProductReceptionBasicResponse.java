package fyi.hrvanovicm.magacin.domain.products.reception;

import fyi.hrvanovicm.magacin.domain.products.ProductBasicResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductReceptionBasicResponse {
    private Long id;
    private ProductBasicResponse product;
    private ProductBasicResponse rawMaterialProduct;
    private Float amount;

    @SuppressWarnings("DuplicatedCode")
    public static ProductReceptionBasicResponse fromEntity(ProductReceptionEntity receptionEntity) {
        ProductReceptionBasicResponse dto = new ProductReceptionBasicResponse();

        dto.setId(receptionEntity.getId());
        dto.setProduct(ProductBasicResponse.fromEntity(receptionEntity.getProduct()));
        dto.setRawMaterialProduct(ProductBasicResponse.fromEntity(receptionEntity.getRawMaterialProduct()));
        dto.setAmount(receptionEntity.getAmount());

        return dto;
    }
}
