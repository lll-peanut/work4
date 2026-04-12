package com.peanut.POJO.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private String id;
    private String username;
    private String password;
    private String role;
    private boolean enabled = true;
    private Collection<? extends GrantedAuthority> authorities;

    // 构造方法（用于创建实例）
    public CustomUserDetails(String id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    // getter：用于后续提取 id
    public String getId() {
        return id;
    }

    // 以下是 UserDetails 接口的默认实现（直接返回对应字段）
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 以下方法默认返回 true（表示账户正常，无需锁定/过期）
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
}