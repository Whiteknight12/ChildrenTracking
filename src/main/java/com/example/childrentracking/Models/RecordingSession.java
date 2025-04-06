package com.example.childrentracking.Models;

import java.sql.Timestamp;

public class RecordingSession {
    @PrimaryKey
    public Integer SessionId;
    public Integer UserId;
    public User User;
    public Timestamp StartTime;
    public Timestamp EndTime;

    public RecordingSession(int userid, Timestamp starttime, Timestamp endtime) {
        UserId = userid;
        StartTime = starttime;
        EndTime = endtime;
    }

    public RecordingSession() {}
}
