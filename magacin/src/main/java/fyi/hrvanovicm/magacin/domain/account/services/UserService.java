package fyi.hrvanovicm.magacin.domain.account.services;

import fyi.hrvanovicm.magacin.domain.account.User;
import fyi.hrvanovicm.magacin.domain.account.UserRole;
import fyi.hrvanovicm.magacin.domain.account.dto.requests.CreateUserRequestDTO;
import fyi.hrvanovicm.magacin.domain.account.dto.requests.UpdateUserRequestDTO;
import fyi.hrvanovicm.magacin.domain.account.dto.responses.UserResponse;
import fyi.hrvanovicm.magacin.domain.account.repositories.UserRepository;
import fyi.hrvanovicm.magacin.domain.account.repositories.UserRoleRepository;
import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.article.dto.request.ArticleCreateRequestDTO;
import fyi.hrvanovicm.magacin.domain.article.dto.request.ArticleUpdateRequestDTO;
import fyi.hrvanovicm.magacin.domain.article.dto.response.ArticleResponse;
import fyi.hrvanovicm.magacin.domain.article.repositories.ArticleRepository;
import fyi.hrvanovicm.magacin.domain.unit_measure.services.UnitMeasureService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserService(
            final UserRepository userRepository,
            final UserRoleRepository userRoleRepository
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public List<UserResponse> getAll(Specification<User> specs) {
        return userRepository
                .findAll(specs)
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public Page<UserResponse> getAll(
            Specification<User> specs,
            Pageable pageable
    ) {
        return userRepository
                .findAll(specs, pageable)
                .map(UserResponse::fromEntity);
    }

    public Optional<UserResponse> getById(Long id) {
        return userRepository.findById(id).map(UserResponse::fromEntity);
    }

    @Transactional
    public UserResponse create(@Valid CreateUserRequestDTO request) {
        var userRoles = new HashSet<>(this.userRoleRepository.findAllById(request.getRoleIds()));
        var createdUser = userRepository.save(request.toEntity(userRoles));

        return UserResponse.fromEntity(createdUser);
    }

    @Transactional
    public UserResponse update(
            @NotNull Long userId,
            @Valid UpdateUserRequestDTO request
    ) {
        var userRoles = new HashSet<>(this.userRoleRepository.findAllById(request.getRoleIds()));
        var updatedUser = userRepository.save(request.toEntity(userId, userRoles));

        userRepository.save(updatedUser);

        return UserResponse.fromEntity(updatedUser);
    }

    @Transactional
    public void delete(@NotNull Long userId) {
        var user = userRepository.findById(userId).orElseThrow();
        userRepository.delete(user);
    }

    @Transactional
    public void forceDelete(@NotNull Long userId) {
        var user = userRepository.findById(userId).orElseThrow();
        userRepository.forceDeleteById(user.getId());
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public String getPassword() {
                return "test";
            }

            @Override
            public String getUsername() {
                return "mirza";
            }
        };
    }
}
