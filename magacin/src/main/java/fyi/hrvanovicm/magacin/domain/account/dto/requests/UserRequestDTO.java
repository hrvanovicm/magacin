package fyi.hrvanovicm.magacin.domain.account.dto.requests;

import fyi.hrvanovicm.magacin.domain.account.User;
import fyi.hrvanovicm.magacin.domain.account.UserRole;
import fyi.hrvanovicm.magacin.domain.account.utils.UserValidationUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public abstract sealed class UserRequestDTO permits CreateUserRequestDTO, UpdateUserRequestDTO {
    @NotBlank()
    @Size(max = UserValidationUtils.USERNAME_MAX_CHARACTERS)
    String username;

    @Email
    String email;

    Set<Long> roleIds;

    protected User toEntity(Set<UserRole> roles) {
        var entity = new User();
        entity.setUsername(username);
        entity.setEmail(email);
        entity.setRoles(roles);

        return entity;
    }
}
