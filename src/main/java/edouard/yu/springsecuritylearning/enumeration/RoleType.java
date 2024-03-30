package edouard.yu.springsecuritylearning.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum RoleType {
    USER(Set.of(
            PermissionType.USER_CREATE_POST,
            PermissionType.USER_READ,
            PermissionType.USER_UPDATE_POST
    )),
    MANAGER(Set.of(
            PermissionType.MANAGER_CREATE,
            PermissionType.MANAGER_READ,
            PermissionType.MANAGER_UPDATE,
            PermissionType.MANAGER_DELETE_POST
    )),
    ADMINISTRATOR(Set.of(
            PermissionType.ADMINISTRATOR_CREATE,
            PermissionType.ADMINISTRATOR_READ,
            PermissionType.ADMINISTRATOR_UPDATE,
            PermissionType.ADMINISTRATOR_DELETE,

            PermissionType.MANAGER_CREATE,
            PermissionType.MANAGER_READ,
            PermissionType.MANAGER_UPDATE,
            PermissionType.MANAGER_DELETE_POST
    ));

    final Set<PermissionType> permissions;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<SimpleGrantedAuthority> grantedAuthorities = new java.util.ArrayList<>(this.getPermissions().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .toList());

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return grantedAuthorities;
    }
}
