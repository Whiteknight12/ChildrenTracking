package com.example.childrentracking.Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ApplicationDbContext {
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connect= DriverManager.getConnection("jdbc:mysql://localhost:3306/ultrack", "root", "");
            return connect;
        }
        catch (Exception e) {
            System.out.println("DATABASE CONNECTION ERROR: "+e);
        }
        return null;
    }
}
