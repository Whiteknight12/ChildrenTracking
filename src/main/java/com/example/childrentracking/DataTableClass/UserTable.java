package com.example.childrentracking.DataTableClass;

import com.example.childrentracking.Database.DatabaseTable;
import com.example.childrentracking.Models.User;

import java.sql.Connection;

public class UserTable extends DatabaseTable<User> {
    public UserTable(Connection connection) {
        super("USERACCOUNT", User.class, connection);
    }
}
