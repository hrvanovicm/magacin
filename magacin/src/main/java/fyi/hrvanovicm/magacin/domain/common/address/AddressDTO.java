package fyi.hrvanovicm.magacin.domain.common.address;

import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import fyi.hrvanovicm.magacin.domain.company.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressDTO {
    Long id;
    String country;
    String city;
    String address;

    public static AddressDTO fromEntity(Address address) {
        var dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setCountry(address.getCountry());
        dto.setCity(address.getCity());
        dto.setAddress(address.getAddress());

        return dto;
    }
}
