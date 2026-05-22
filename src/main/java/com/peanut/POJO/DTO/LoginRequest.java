package com.peanut.POJO.DTO;

/**
 * @author: peanut
 * @date: 2026/5/17
 * @version:1.0
 */
public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}