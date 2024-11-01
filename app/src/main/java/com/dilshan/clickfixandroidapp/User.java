package com.dilshan.clickfixandroidapp;

public class User {
    private String name;
    private String email;
    private String password;
    private String mobile;
    private String city;
    private String address;
    private String imageUrl;

    // Default constructor required for Firebase
    public User() {
    }

    // Constructor for full user details
    public User(String name, String email, String password, String mobile, String city, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.city = city;
        this.address = address;
    }

    // Constructor for minimal user details
    public User(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // Getters and setters for all fields
    // These are necessary for Firebase to properly map data
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
