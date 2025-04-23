package fyi.hrvanovicm.magacin.application.report.requests;

import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReportEditRequest {
    @NotBlank
    String code;

    @NotBlank
    LocalDate date;

    String descriptionHtml;
    String placeOfPublish;
    String signedByName;

    List<ReportProductRequest> products;

    public void fill(ReportEntity report) {
        report.setCode(this.getCode());
        report.setDate(this.getDate().toString());
        report.setDescriptionHtml(this.getDescriptionHtml());
        report.setPlaceOfPublish(this.getPlaceOfPublish());
        report.setSignedByName(this.getSignedByName());
    }
}
