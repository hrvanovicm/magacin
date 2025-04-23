package fyi.hrvanovicm.magacin.domain.products.reports;

import fyi.hrvanovicm.magacin.domain.products.ProductDTO;
import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionBasicResponse;
import fyi.hrvanovicm.magacin.domain.report.Report;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProductReportDTO {
    private Long id;
    private Report report;
    private List<ProductReceptionBasicResponse> receptions;
    private Float amount;

    public static ProductReportDTO fromEntity(ReportProductEntity entity) {
        var dto = new ProductReportDTO();

        dto.setId(entity.getId());
        dto.setReport(Report.fromEntity(entity.getReport()));
        dto.setReceptions(entity.getReceptions().stream().map(ProductReceptionBasicResponse::fromEntity).collect(Collectors.toList()));
        dto.setAmount(entity.getAmount());

        return dto;
    }
}
