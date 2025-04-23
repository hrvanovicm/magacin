package fyi.hrvanovicm.magacin.domain.report;

import jakarta.persistence.criteria.JoinType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class ReportSpecification {
    public static Specification<ReportEntity> search(@NotBlank String keywords) {
        return (root, query, builder) -> {
            return builder.or(
                    builder.like(root.get("code"), String.format("%s%%", keywords)),
                    builder.like(root.get("placeOfPublish"), String.format("%%%s%%", keywords))
            );
        };
    }

    public static Specification<ReportEntity> hasTypes(@NotNull List<ReportType> types) {
        return (root, query, builder) -> {
            return root.get("type").in(types);
        };
    }

    public static Specification<ReportEntity> isShipment() {
        return (root, query, builder) -> {
            return builder.equal(root.get("type"), ReportType.RECEIPT);
        };
    }

    public static Specification<ReportEntity> betweenDates(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            if (from != null && to != null) {
                return builder.between(root.get("date"), from.toString(), to.toString());
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(root.get("date"), from.toString());
            } else {
                return builder.lessThanOrEqualTo(root.get("date"), to.toString());
            }
        };
    }

    public static Specification<ReportEntity> hasCompanyName(@NotEmpty String companyName) {
        return (root, query, builder) -> {
            return builder.or(
                    builder.equal(root.join("receiptReport", JoinType.LEFT).get("supplierCompanyName"), companyName),
                    builder.equal(root.join("shipmentReport", JoinType.LEFT).get("receiptCompanyName"), companyName)
            );
        };
    }

    public static Specification<ReportEntity> hasProduct(@NotEmpty Long productId) {
        return (root, query, builder) -> {
            return builder.equal(root.join("products").join("product").get("id"), productId);
        };
    }
}
