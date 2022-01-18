package fr.kmcl.unisignBACK.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static fr.kmcl.unisignBACK.security.AppUserPermission.*;

public enum AppUserRole {
    USER(Sets.newHashSet(USER_READ, SIGNATURE_READ)),
	MANAGER(Sets.newHashSet(USER_READ, SIGNATURE_READ, SIGNATURE_WRITE)),
	ADMIN(Sets.newHashSet(USER_READ, USER_WRITE, SIGNATURE_READ, SIGNATURE_WRITE)),
	SUPER_ADMIN(Sets.newHashSet(USER_READ, USER_WRITE, SIGNATURE_READ, SIGNATURE_WRITE));

	private final Set<AppUserPermission> permissions;

	AppUserRole(Set<AppUserPermission> permissions) {
		this.permissions = permissions;
	}

	public Set<AppUserPermission> getPermissions() {
		return permissions;
	}

	public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
		Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
				.collect(Collectors.toSet());
		permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
		return permissions;
	}
}
