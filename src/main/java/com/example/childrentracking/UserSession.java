package com.example.childrentracking;

public class UserSession {
    private static String username;
    private static Integer userid;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserSession.username = username;
    }

    public static Integer getUserid() {
        return userid;
    }

    public static void setUserid(Integer userid) {
        UserSession.userid = userid;
    }
}
