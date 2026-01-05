package com.peanut.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * @author: peanut
 * @date: 2026/3/9
 * @version:1.0
 */
public class LoginUser implements UserDetails {

    private final String userId;
    private final String username;      // 登录名
    private final String passwordHash;  // 数据库密码密文（BCrypt）
    private final Collection<? extends GrantedAuthority> authorities;

    public LoginUser(String userId, String username, String passwordHash,
                     Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.authorities = (authorities == null) ? Collections.emptyList() : authorities;
    }


    public String getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // 下面 4 个一定别写反；写错会被当成不可用账号，从而认证失败
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "LoginUser{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
