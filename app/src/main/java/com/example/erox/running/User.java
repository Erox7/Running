package com.example.erox.running;

public class User {
    private String name;
    private String email;
    private String password;
    private String token;
    private String UID = null;

    public User(String name, String email, String password, String token, String UID) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.token = token;
        this.UID = UID;
    }

    public User(String name, String email, String password, String token) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String token) {
        this.UID = UID;
    }
}
