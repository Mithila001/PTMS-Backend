package com.tritonptms.ptms.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

public final class AuthenticatedUser implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String username;
    private final String password;
    private final long authVersion;
    private final List<GrantedAuthority> authorities;

    public AuthenticatedUser(Long id, String username, String password, long authVersion, Collection<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authVersion = authVersion;
        this.authorities = roles.stream()
                .sorted()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    public Long id() {
        return id;
    }

    public long authVersion() {
        return authVersion;
    }

    public List<String> roles() {
        return authorities.stream().map(GrantedAuthority::getAuthority).toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
