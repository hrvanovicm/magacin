package fyi.hrvanovicm.magacin.domain.article.specifications;

import fyi.hrvanovicm.magacin.domain.article.Article;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public class ArticleSpecification {
    public static Specification<Article> search(@NotBlank String keywords) {
        return (root, query, builder) -> {
            return builder.or(
                    builder.like(root.get("name"), String.format("%%%s%%", keywords)),
                    builder.like(root.get("code"), String.format("%s%%", keywords)),
                    builder.like(root.get("description"), String.format("%%%s%%", keywords))
            );
        };
    }

    public static Specification<Article> hasCategories(@NotEmpty Set<Integer> categoryIds) {
        return (root, query, builder) -> {
            return root.join("category").get("id").in(categoryIds);
        };
    }

    public static Specification<Article> hasTags(@NotEmpty Set<Integer> tagIds) {
        return (root, query, builder) -> {
            return root.join("tags").get("id").in(tagIds);
        };
    }

    public static Specification<Article> inStockAmountBetween(
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
