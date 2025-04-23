package fyi.hrvanovicm.magacin.domain.report.product;

import fyi.hrvanovicm.magacin.domain.products.ProductDTO;
import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionDTO;
import fyi.hrvanovicm.magacin.domain.report.Report;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ReportProductDTO {
    private Long id;
    private Report report;
    private ProductDTO product;
    private List<ProductReceptionDTO> receptions;
    private Float amount;

    public static ReportProductDTO fromEntity(ReportProductEntity entity) {
        var dto = new ReportProductDTO();

        dto.setId(entity.getId());
        dto.setReport(Report.fromEntity(entity.getReport()));
        dto.setProduct(ProductDTO.fromEntity(entity.getProduct()));
        dto.setReceptions(entity.getReceptions().stream().map(ProductReceptionDTO::fromEntity).collect(Collectors.toList()));
        dto.setAmount(entity.getAmount());

        return dto;
    }
}
