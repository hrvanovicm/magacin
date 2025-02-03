package fyi.hrvanovicm.magacin.domain.common.address.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public abstract sealed class AddressRequestDTO permits AddressCreateRequestDTO, AddressUpdateRequestDTO {
    @NotBlank()
    @Size(min = 2, max = 50)
    protected String name;

    @Size(max = 50)
    protected String country;

    @Size(max = 50)
    protected String city;

    @Size(max = 100)
    protected String address;

    @Size(max = 20)
    protected String phoneNumber;

    @Size(max = 20)
    protected String mobileNumber;

    @Email
    protected String email;
}
