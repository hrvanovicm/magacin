package fyi.hrvanovicm.magacin.application.requests;

import fyi.hrvanovicm.magacin.domain.company.Company;
import fyi.hrvanovicm.magacin.domain.company.specifications.CompanySpecification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanySearchCriteriaDTO {
    String q;
    public Specification<Company> toSpecification() {
        return (root, query, builder) -> {
            var predicates = builder.conjunction();

            if (q != null && !q.isEmpty()) {
                predicates = builder.and(
                        predicates,
                        CompanySpecification
                                .search(q)
                                .toPredicate(root, query, builder)
                );
            }

            return predicates;
        };
    }
}