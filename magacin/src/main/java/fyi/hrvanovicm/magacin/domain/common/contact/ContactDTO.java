package fyi.hrvanovicm.magacin.domain.common.contact;

import fyi.hrvanovicm.magacin.domain.common.address.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContactDTO {
    Long id;
    String title;
    String mobileNumber;
    String phoneNumber;
    String email;

    public static ContactDTO fromEntity(Contact entity) {
        var dto = new ContactDTO();
        dto.setTitle(entity.getTitle());
        dto.setEmail(entity.getEmail());
        dto.setMobileNumber(entity.getMobileNumber());
        dto.setPhoneNumber(entity.getPhoneNumber());

        return dto;
    }
}
