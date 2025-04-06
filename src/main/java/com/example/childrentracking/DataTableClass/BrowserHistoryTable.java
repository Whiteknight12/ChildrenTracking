package com.example.childrentracking.DataTableClass;

import com.example.childrentracking.Database.DatabaseTable;
import com.example.childrentracking.Models.BrowserHistory;

import java.sql.Connection;

public class BrowserHistoryTable extends DatabaseTable<BrowserHistory> {
    public BrowserHistoryTable(Connection connection) {
        super("BROWSERHISTORYTABLE", BrowserHistory.class, connection);
    }
}
