package fyi.hrvanovicm.magacin.domain.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecification {
    public static Specification<ProductEntity> search(@NotBlank String keywords) {
        return (root, query, builder) -> {
            return builder.or(
                    builder.like(root.get("name"), String.format("%%%s%%", keywords)),
                    builder.like(root.get("code"), String.format("%s%%", keywords)),
                    builder.like(root.get("description"), String.format("%%%s%%", keywords))
            );
        };
    }

    public static Specification<ProductEntity> isRawMaterial() {
        return (root, query, builder) -> {
            return builder.equal(root.get("category"), ProductCategory.RAW_MATERIAL);
        };
    }

    public static Specification<ProductEntity> hasCategories(@NotEmpty List<ProductCategory> categories) {
        return (root, query, builder) -> {
            return root.get("category").in(categories);
        };
    }

    public static Specification<ProductEntity> hasTag(@NotEmpty String tagName) {
        return (root, query, builder) -> {
            return builder.equal(root.join("tags").get("name"), tagName);
        };
    }

    public static Specification<ProductEntity> hasLowInStockAmount() {
        return (root, query, builder) -> {
            return builder.lessThanOrEqualTo(root.get("inStockAmount"), root.get("inStockWarningAmount"));
        };
    }

    public static Specification<ProductEntity> inStockAmountBetween(
        @NotNull @PositiveOrZero Float fromAmount,
        @NotNull @PositiveOrZero Float toAmount
    ) {
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
}
