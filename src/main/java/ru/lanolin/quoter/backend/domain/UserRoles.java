package ru.lanolin.quoter.backend.domain;

import org.springframework.security.core.GrantedAuthority;

public enum UserRoles implements GrantedAuthority {
	ADMIN, EDITOR, GUEST, ANON;

	@Override
	public String getAuthority() {
		return this.name();
	}
}
