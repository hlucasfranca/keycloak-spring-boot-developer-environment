package com.github.thomasdarimont.keycloak.userstorage.flyweight;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AcmeUser {

    private boolean enabled;
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;

    private long createdTimestamp;

    private Map<String, List<String>> attributes;

    public AcmeUser(String id, String username, String password, String firstName, String lastName, Map<String, List<String>> attributes, boolean enabled) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.attributes = attributes;
        this.username = username;
        this.password = password;
        this.email = this.username + "@example.com";
        this.enabled = enabled;
        // TODO pull from database
//        this.createdTimestamp = System.currentTimeMillis();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public List<String> getAttribute(String name) {
        return attributes.getOrDefault(name, Collections.emptyList());
    }
}
