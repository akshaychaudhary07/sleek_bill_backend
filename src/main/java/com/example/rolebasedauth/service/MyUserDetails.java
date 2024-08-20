package com.example.rolebasedauth.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.example.rolebasedauth.model.Role;
import com.example.rolebasedauth.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MyUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String fullname;
    private String email; // Changed from username to email
    private Collection<? extends GrantedAuthority> authorities;

    @JsonIgnore
    private String password;

    public MyUserDetails(Long id, String fullname, String email, String password, Collection<? extends GrantedAuthority> authorities) { // Changed from username to email
        this.id = id;
        this.fullname = fullname;
        this.email = email; // Changed from username to email
        this.password = password;
        this.authorities = authorities;
    }

    public static MyUserDetails build(User user) {
        List<GrantedAuthority> roles = new ArrayList<>();

        for (Role role : user.getRoles()) {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        }

        return new MyUserDetails(user.getId(), user.getFullname(), user.getEmail(), user.getPassword(), roles); // Changed from username to email
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return email; // Changed from username to email
    }

    public void setEmail(String email) { // Added setter for email
        this.email = email;
    }

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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MyUserDetails user = (MyUserDetails) o;
        return Objects.equals(id, user.id);
    }
}
