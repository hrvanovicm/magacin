package fyi.hrvanovicm.magacin.domain.account.dto.requests;

import fyi.hrvanovicm.magacin.domain.account.User;
import fyi.hrvanovicm.magacin.domain.account.UserRole;

import java.util.Set;

public final class CreateUserRequestDTO extends UserRequestDTO {
    @Override
    public User toEntity(Set<UserRole> roles) {
        return super.toEntity(roles);
    }
}
