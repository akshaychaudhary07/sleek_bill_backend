package com.example.rolebasedauth.model;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "roles")
@Component
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int id;

    @Column(name = "role", unique = true)
    private String role;

    public Role() {

    }

    public Role(int id, String role) {
        this.id = id;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}