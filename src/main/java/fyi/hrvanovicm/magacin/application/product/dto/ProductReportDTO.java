package fyi.hrvanovicm.magacin.application.product.dto;

import fyi.hrvanovicm.magacin.application.report.dto.ReportDTO;
import fyi.hrvanovicm.magacin.domain.report.ReportProductEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProductReportDTO {
    private Long id;
    private ReportDTO report;
    private List<ProductReceptionDTO> receptions;
    private Float amount;

    public static ProductReportDTO fromEntity(ReportProductEntity entity) {
        var dto = new ProductReportDTO();

        dto.setId(entity.getId());
        dto.setReport(ReportDTO.fromEntity(entity.getReport()));
        dto.setReceptions(entity.getReceptions().stream().map(ProductReceptionDTO::fromEntity).collect(Collectors.toList()));
        dto.setAmount(entity.getAmount());

        return dto;
    }
}
