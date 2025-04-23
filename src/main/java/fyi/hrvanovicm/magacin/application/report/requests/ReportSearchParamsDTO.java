package fyi.hrvanovicm.magacin.application.report.requests;

import fyi.hrvanovicm.magacin.domain.report.ReportEntity;
import fyi.hrvanovicm.magacin.domain.report.ReportSpecification;
import fyi.hrvanovicm.magacin.domain.report.ReportType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class ReportSearchParamsDTO {
    String search;
    List<String> types;
    String signedDateFrom;
    String signedDateTo;
    String company;
    Long hasProductId;

    public Specification<ReportEntity> toSpecification() {
        Specification<ReportEntity> spec = Specification.where(null);

        if (search != null && !search.isEmpty()) {
            spec = spec.and(ReportSpecification.search(search));
        }

        if (types != null && !types.isEmpty()) {
            List<ReportType> reportTypes = types.stream()
                    .map(ReportType::valueOf)  // Assuming the types are valid enum names
                    .collect(Collectors.toList());
            spec = spec.and(ReportSpecification.hasTypes(reportTypes));
        }

        if (signedDateFrom != null && !signedDateFrom.isEmpty()) {
            LocalDate fromDate = LocalDate.parse(signedDateFrom);
            spec = spec.and(ReportSpecification.betweenDatesFrom(fromDate));
        }

        if (signedDateTo != null && !signedDateTo.isEmpty()) {
            LocalDate toDate = LocalDate.parse(signedDateTo);
            spec = spec.and(ReportSpecification.betweenDatesTo(toDate));
        }

        if (company != null && !company.isEmpty()) {
            spec.and(ReportSpecification.hasCompanyName(company));
        }

        if (hasProductId != null) {
            spec.and(ReportSpecification.hasProduct(hasProductId));
        }

        return spec;
    }
}
