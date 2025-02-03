package fyi.hrvanovicm.magacin.domain.account.dto.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileResponse {
    String firstName;
    String lastName;
}
