package fyi.hrvanovicm.magacin.application.report.requests;

import fyi.hrvanovicm.magacin.application.report.dto.ReportProductDTO;
import fyi.hrvanovicm.magacin.application.report.dto.ReportProductReceptionDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ReportProductRequest {
    @Positive
    Long id;

    @NotNull
    @Positive
    Long productId;

    @NotNull
    @PositiveOrZero
    Float amount;

    List<ReportProductReceptionDTO> receptions = new ArrayList<>();

    public static ReportProductRequest from(ReportProductDTO item) {
        var req = new ReportProductRequest();
        req.setId(item.getId());
        req.setReceptions(item.getReceptions().stream().map(
                rec -> {
                    var recReq = new ReportProductReceptionDTO();
                    recReq.setId(rec.getId());
                    recReq.setRawMaterialId(rec.getRawMaterialProduct().getId());
                    recReq.setAmount(rec.getAmount());
                    return recReq;
                }
        ).collect(Collectors.toList()));
        req.setProductId(item.getProduct().getId());
        req.setAmount(item.getAmount());
        return req;
    }
}
