package com.example.shadowbez.chato;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String id;
    private String email;
    private Map<String, String> userChats;

    public User(String id, String email) {
        this.id = id;
        this.email = email;
        userChats = new HashMap<>();
    }

    public User() {

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserChats(Map<String, String> userChats) {
        this.userChats = userChats;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getUserChats() {
        return userChats;
    }
}
