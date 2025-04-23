package fyi.hrvanovicm.magacin.domain.products;

import jakarta.persistence.criteria.JoinType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public class ProductSpecification {
    public static Specification<ProductEntity> search(@NotBlank String keywords) {
        return (root, query, builder) -> {
            return builder.or(
                    builder.like(root.get("name"), String.format("%%%s%%", keywords)),
                    builder.like(root.get("code"), String.format("%s%%", keywords))
            );
        };
    }

    public static Specification<ProductEntity> isRawMaterial() {
        return (root, query, builder) -> {
            return builder.equal(root.get("category"), ProductCategory.RAW_MATERIAL);
        };
    }

    public static Specification<ProductEntity> hasCategory(List<ProductCategory> categories) {
        return (root, query, builder) -> {
            return root.get("category").in(categories);
        };
    }

    public static Specification<ProductEntity> hasTag(String tagName) {
        return (root, query, builder) -> {
            return builder.equal(root.join("tags").get("name"), tagName);
        };
    }

    public static Specification<ProductEntity> hasLowInStockAmount() {
        return (root, query, builder) -> {
            return builder.lessThanOrEqualTo(root.get("inStockAmount"), root.get("inStockWarningAmount"));
        };
    }

    public static Specification<ProductEntity> inStockAmountBetween(Float fromAmount, Float toAmount) {
        return (root, query, builder) -> {
            if (fromAmount != null && toAmount != null) {
                return builder.between(root.get("inStockAmount"), fromAmount, toAmount);
            } else if (fromAmount != null) {
                return builder.greaterThanOrEqualTo(root.get("inStockAmount"), fromAmount);
            } else {
                return builder.lessThanOrEqualTo(root.get("inStockAmount"), toAmount);
            }
        };
    }

    public static Specification<ProductEntity> withReceptions() {
        return (root, query, builder) -> {
        //  root.fetch("receptions", JoinType.LEFT);
          return builder.conjunction();
        };
    }

    public static Specification<ProductEntity> withReports() {
        return (root, query, builder) -> {
           // root.fetch("reports", JoinType.LEFT);
            return builder.conjunction();
        };
    }
}
