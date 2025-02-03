package fyi.hrvanovicm.magacin.domain.company.dto.requests;

import fyi.hrvanovicm.magacin.domain.company.Company;
import fyi.hrvanovicm.magacin.domain.company.utils.CompanyValidationRulesUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public abstract sealed class CompanyRequestDTO permits CompanyCreateRequestDTO, CompanyUpdateRequest {
    @NotBlank
    @Size(max = CompanyValidationRulesUtil.NAME_MAX_CHARACTERS)
    String name;

    @Size(max = CompanyValidationRulesUtil.DESCRIPTION_MAX_CHARACTERS)
    String description;

    Boolean isSupplier;

    Boolean isRecipient;

    protected Company toEntity() {
        final var entity = new Company();

        entity.setName(getName());
        entity.setDescription(getDescription());
        entity.setIsSupplier(getIsSupplier());
        entity.setIsRecipient(getIsRecipient());

        return entity;
    }
}
