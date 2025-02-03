package fyi.hrvanovicm.magacin.domain.company.dto.responses;

import fyi.hrvanovicm.magacin.domain.common.address.AddressDTO;
import fyi.hrvanovicm.magacin.domain.common.contact.ContactDTO;
import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import fyi.hrvanovicm.magacin.domain.company.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CompanyDetailsResponse {
    Long id;
    String name;
    String description;
    Boolean isSupplier;
    Boolean isRecipient;
    Set<AddressDTO> addresses;
    Set<ContactDTO> contacts;
    AuditDTO audit;

    public static CompanyDetailsResponse fromEntity(Company entity) {
        CompanyDetailsResponse dto = new CompanyDetailsResponse();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setIsSupplier(entity.getIsSupplier());
        dto.setDescription(entity.getDescription());
        dto.setIsRecipient(entity.getIsRecipient());
        dto.setAudit(AuditDTO.fromEntity(entity.getAudit()));
        dto.setAddresses(
                entity.getAddresses()
                        .stream()
                        .map(AddressDTO::fromEntity)
                        .collect(Collectors.toSet())
        );

        return dto;
    }
}
