package fyi.hrvanovicm.magacin.domain.account.dto.responses;

import fyi.hrvanovicm.magacin.domain.account.User;
import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    Long id;
    String username;
    String email;
    String password;
    UserProfileResponse profile;
    AuditDTO audit;

    public static UserResponse fromEntity(User user) {
        UserProfileResponse userProfileDTO = new UserProfileResponse();

        if(user.getProfile() != null) {
            userProfileDTO.setFirstName(user.getProfile().getFirstName());
            userProfileDTO.setLastName(user.getProfile().getLastName());
        }

        var dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAudit(AuditDTO.fromEntity(user.getAudit()));
        dto.setProfile(userProfileDTO);
        return dto;
    }

    @SuppressWarnings("DuplicatedCode")
    public User toEntity() {
        var user = new User();

        user.setId(this.getId());
        user.setUsername(this.getUsername());
        user.setEmail(this.getEmail());
        user.setAudit(this.getAudit().toEntity());

        return user;
    }
}
