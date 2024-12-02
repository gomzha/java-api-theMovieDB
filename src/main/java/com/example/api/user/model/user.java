package com.example.api.user.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String name;
    private String token;
    private String privilege;

    public user() {
    }

    public user(int id, String name, String privilege, String token) {
        this.id = id;
        this.name = name;
        this.privilege = privilege;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
