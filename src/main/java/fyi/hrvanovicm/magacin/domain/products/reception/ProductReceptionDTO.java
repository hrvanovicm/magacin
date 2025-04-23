package fyi.hrvanovicm.magacin.domain.products.reception;

import fyi.hrvanovicm.magacin.domain.products.ProductDTO;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductUsedReceptionsEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductReceptionDTO {
    private Long id;


    private ProductDTO rawMaterialProduct;
    private Float amount;

    @SuppressWarnings("DuplicatedCode")
    public static ProductReceptionDTO fromEntity(ProductReceptionEntity receptionEntity) {
        ProductReceptionDTO dto = new ProductReceptionDTO();

        dto.setId(receptionEntity.getId());
       // dto.setProduct(ProductDTO.fromEntity(receptionEntity.getProduct()));
        dto.setRawMaterialProduct(ProductDTO.fromEntity(receptionEntity.getRawMaterialProduct()));
        dto.setAmount(receptionEntity.getAmount());

        return dto;
    }

    public static ProductReceptionDTO fromEntity(ReportProductUsedReceptionsEntity receptionEntity) {
        ProductReceptionDTO dto = new ProductReceptionDTO();

        dto.setId(receptionEntity.getId());
        // dto.setProduct(ProductDTO.fromEntity(receptionEntity.getProduct()));
        dto.setRawMaterialProduct(ProductDTO.fromEntity(receptionEntity.getRawMaterialProduct()));
        dto.setAmount(receptionEntity.getAmount());

        return dto;
    }
}
