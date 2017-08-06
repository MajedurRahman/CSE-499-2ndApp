package com.design.capstone.cse_499_2ndapp.Model;

import java.io.Serializable;

/**
 * Created by Majedur Rahman on 8/1/2017.
 */

public class User implements Serializable{

    private String fullName;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;


    public User(){


    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
