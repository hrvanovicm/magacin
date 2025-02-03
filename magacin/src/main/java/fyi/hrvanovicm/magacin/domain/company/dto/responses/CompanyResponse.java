package fyi.hrvanovicm.magacin.domain.company.dto.responses;

import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import fyi.hrvanovicm.magacin.domain.company.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanyResponse {
    Long id;
    String name;
    String description;
    Boolean isSupplier;
    Boolean isRecipient;
    AuditDTO audit;

    public static CompanyResponse fromEntity(Company company) {
        var dto = new CompanyResponse();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setIsSupplier(company.getIsSupplier());
        dto.setIsRecipient(company.getIsRecipient());
        dto.setAudit(AuditDTO.fromEntity(company.getAudit()));

        return dto;
    }

    @SuppressWarnings("DuplicatedCode")
    public Company toEntity() {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        company.setDescription(description);
        company.setIsSupplier(isSupplier);
        company.setIsRecipient(isRecipient);
        company.setAudit(audit.toEntity());

        return company;
    }
}
