package fyi.hrvanovicm.magacin.domain.company.specifications;

import fyi.hrvanovicm.magacin.domain.common.address.Address;
import fyi.hrvanovicm.magacin.domain.company.Company;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.criteria.Join;

public class CompanySpecification {
    public static Specification<Company> search(@NotBlank String keywords) {
        return (root, query, builder) -> {
            Join<Company, Address> addressJoinRoot = root.join("addresses", JoinType.LEFT);
            return builder.or(
                    builder.like(root.get("name"), String.format("%%%s%%", keywords)),
                    builder.like(root.get("location"), String.format("%s%%", keywords)),
                    builder.like(root.get("description"), String.format("%s%%", keywords)),
                    builder.like(addressJoinRoot.get("email"), String.format("%%%s%%", keywords)),
                    builder.like(addressJoinRoot.get("phone_number"), String.format("%%%s%%", keywords))
            );
        };
    }

    public static Specification<Company> isSupplier() {
        return (root, query, builder) -> builder.isTrue(root.get("isSupplier"));
    }

    public static Specification<Company> isRecipient() {
        return (root, query, builder) -> builder.isTrue(root.get("isRecipient"));
    }
}
