package com.example.rolebasedauth.payload;

import java.util.List;

public class JwtResponse {

    private String type;
    private String token;
    private String refreshToken;
    private Long userid;
    private String fullname;
    private String email;  // Changed from username to email
    private List<String> roles;

    public JwtResponse(String type, String token, String refreshToken, Long userid, String fullname, String email, List<String> roles) {  // Changed parameter name
        this.type = type;
        this.token = token;
        this.refreshToken = refreshToken;
        this.userid = userid;
        this.fullname = fullname;
        this.email = email;  // Changed from username to email
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {  // Changed from getUsername to getEmail
        return email;
    }

    public void setEmail(String email) {  // Changed from setUsername to setEmail
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}