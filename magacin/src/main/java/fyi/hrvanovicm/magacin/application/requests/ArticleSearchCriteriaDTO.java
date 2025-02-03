package fyi.hrvanovicm.magacin.application.requests;

import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.article.specifications.ArticleSpecification;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleSearchCriteriaDTO {
    @Size(max = 255)
    String q;

    Set<Integer> tagIds = new HashSet<>();

    @PositiveOrZero
    Float inStockAmountFrom;

    @PositiveOrZero
    Float inStockAmountTo;

    public Specification<Article> toSpecification() {
        return (root, query, builder) -> {
            var predicates = builder.conjunction();

            if (q != null && !q.isEmpty()) {
                predicates = builder.and(
                        predicates,
                        ArticleSpecification
                                .search(q)
                                .toPredicate(root, query, builder)
                );
            }

            if (tagIds != null && !tagIds.isEmpty()) {
                predicates = builder.and(
                        predicates,
                        ArticleSpecification
                                .hasTags(tagIds)
                                .toPredicate(root, query, builder)
                );
            }

            if (inStockAmountFrom != null || inStockAmountTo != null) {
                predicates = builder.and(
                        predicates,
                        ArticleSpecification
                                .inStockAmountBetween(inStockAmountFrom, inStockAmountTo)
                                .toPredicate(root, query, builder)
                );
            }

            return predicates;
        };
    }
}