package fyi.hrvanovicm.magacin.application.report.dto;

import fyi.hrvanovicm.magacin.application.product.dto.ProductDTO;
import fyi.hrvanovicm.magacin.application.product.dto.ProductReceptionDTO;
import fyi.hrvanovicm.magacin.domain.report.ReportProductEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ReportProductDTO {
    private Long id;
    private ProductDTO product;
    private List<ProductReceptionDTO> receptions;
    private Float amount;

    public static ReportProductDTO fromEntity(ReportProductEntity entity) {
        var dto = new ReportProductDTO();

        dto.setId(entity.getId());
        dto.setProduct(ProductDTO.fromEntity(entity.getProduct()));
        dto.setReceptions(entity.getReceptions().stream().map(ProductReceptionDTO::fromEntity).collect(Collectors.toList()));
        dto.setAmount(entity.getAmount());

        return dto;
    }
}
