package com.example.childrentracking.Models;

public class User {
    @PrimaryKey
    public int UserId;
    public String Email;
    public String UserName;
    public String Password;
    public String Role;
    public String FullName;
    public Integer Age;
    public String Gender;
    public String Phone;

    public User() {}

    public User(String username, String password, String role, String email, String fullName, Integer age, String gender, String phone) {
        UserName = username;
        Password = password;
        Role = role;
        Email=email;
        FullName=fullName;
        Age=age;
        Gender=gender;
        Phone=phone;
    }
}
