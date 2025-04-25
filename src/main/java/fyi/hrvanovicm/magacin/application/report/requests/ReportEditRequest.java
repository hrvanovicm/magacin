package fyi.hrvanovicm.magacin.application.report.requests;

import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportValidationRulesUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public sealed class ReportEditRequest permits ReceiptReportEditRequest, ShipmentReportEditRequest {
    @NotBlank(message = "Polje šifra ne može biti prazno!")
    @Size(
            max = ReportValidationRulesUtils.CODE_MAX_CHARACTERS,
            message = "Polje šifra sadrži previše karaktera!"
    )
    String code;

    @NotNull(message = "Polje datum izdavanja ne može biti prazno!")
    LocalDate date;

    @Size(
            max = ReportValidationRulesUtils.DESCRIPTION_MAX_CHARACTERS,
            message = "Opis sadrži previše karaktera!"
    )
    String descriptionHtml;

    @Size(
            max = ReportValidationRulesUtils.PLACE_OF_PUBLISH_MAX_CHARACTERS,
            message = "Polje mjesto izdavanja sadrži previše karaktera!"
    )
    String placeOfPublish;

    @Size(
            max = ReportValidationRulesUtils.SIGNED_BY_USER_NAME_MAX_CHARACTERS,
            message = "Polje odgovorni radnik sadrži previše karaktera!"
    )
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
