package com.example.childrentracking.Models;

import java.sql.Timestamp;

public class RecordingSession {
    @PrimaryKey
    public int SessionId;
    public String userID;
    public int UserId;
    public User User;
    public Timestamp StartTime;
    public Timestamp EndTime;
}
