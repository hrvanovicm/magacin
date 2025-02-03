package fyi.hrvanovicm.magacin.domain.account.dto.requests;

import fyi.hrvanovicm.magacin.domain.account.User;
import fyi.hrvanovicm.magacin.domain.account.UserRole;

import java.util.Set;

public final class UpdateUserRequestDTO extends UserRequestDTO {
    public User toEntity(Long id, Set<UserRole> roles) {
        var entity = super.toEntity(roles);
        entity.setId(id);

        return entity;
    }
}
