package com.example.childrentracking.DataTableClass;

import com.example.childrentracking.Database.DatabaseTable;
import com.example.childrentracking.Models.AppUsage;

import java.sql.Connection;

public class AppUsageTable extends DatabaseTable<AppUsage> {
    public AppUsageTable(Connection connection) {
        super("APPUSAGETABLE", AppUsage.class, connection);
    }
}
