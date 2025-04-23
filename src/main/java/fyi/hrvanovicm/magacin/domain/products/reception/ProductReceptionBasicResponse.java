package fyi.hrvanovicm.magacin.domain.products.reception;

import fyi.hrvanovicm.magacin.domain.products.ProductDTO;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductUsedReceptionsEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductReceptionBasicResponse {
    private Long id;


    private ProductDTO rawMaterialProduct;
    private Float amount;

    @SuppressWarnings("DuplicatedCode")
    public static ProductReceptionBasicResponse fromEntity(ProductReceptionEntity receptionEntity) {
        ProductReceptionBasicResponse dto = new ProductReceptionBasicResponse();

        dto.setId(receptionEntity.getId());
       // dto.setProduct(ProductDTO.fromEntity(receptionEntity.getProduct()));
        dto.setRawMaterialProduct(ProductDTO.fromEntity(receptionEntity.getRawMaterialProduct()));
        dto.setAmount(receptionEntity.getAmount());

        return dto;
    }

    public static ProductReceptionBasicResponse fromEntity(ReportProductUsedReceptionsEntity receptionEntity) {
        ProductReceptionBasicResponse dto = new ProductReceptionBasicResponse();

        dto.setId(receptionEntity.getId());
        // dto.setProduct(ProductDTO.fromEntity(receptionEntity.getProduct()));
        dto.setRawMaterialProduct(ProductDTO.fromEntity(receptionEntity.getRawMaterialProduct()));
        dto.setAmount(receptionEntity.getAmount());

        return dto;
    }
}
