package fyi.hrvanovicm.magacin.domain.report.product;

import fyi.hrvanovicm.magacin.domain.products.ProductBasicResponse;
import fyi.hrvanovicm.magacin.domain.report.ReportResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportProductResponse {
    private Long id;
    private ReportResponse report;
    private ProductBasicResponse product;
    private Float amount;

    public static ReportProductResponse fromEntity(ReportProductEntity entity) {
        var dto = new ReportProductResponse();

        dto.setId(entity.getId());
        dto.setReport(ReportResponse.fromEntity(entity.getReport()));
        dto.setProduct(ProductBasicResponse.fromEntity(entity.getProduct()));
        dto.setAmount(entity.getAmount());

        return dto;
    }
}
