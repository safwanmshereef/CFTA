package com.singularis.carbon.Beans;


public class User {
    private String name;
    private String email;
    private int rank;

    public User(String name, String email, int rank) {
        this.name = name;
        this.email = email;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getRank() {
        return rank;
    }
}