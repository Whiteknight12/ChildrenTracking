package com.example.childrentracking.DataTableClass;

import com.example.childrentracking.Database.DatabaseTable;
import com.example.childrentracking.Models.RecordingSession;

import java.sql.Connection;

public class RecordingSessionTable extends DatabaseTable<RecordingSession> {
    public RecordingSessionTable(Connection connection) {
        super("RECORDINGSESSIONTABLE", RecordingSession.class, connection);
    }
}
