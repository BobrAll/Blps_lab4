package bobr.mainMicroservice.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static bobr.mainMicroservice.user.Permission.*;

@RequiredArgsConstructor
public enum Role {
    USER(Set.of(
            FLAT_ADD
    )),
    SUPERUSER(Set.of(
            FLAT_ADD,
            FLAT_BOOST
    )),
    ADMIN(Set.of(
            FLAT_ADD,
            FLAT_BOOST,
            FLAT_DELETE,

            USER_BlOCK,
            USER_UNBLOCK,
            USER_DELETE,
            USER_ADD_BALANCE
    ));

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
    }
}
