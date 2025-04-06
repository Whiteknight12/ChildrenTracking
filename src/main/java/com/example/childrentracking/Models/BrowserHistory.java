package com.example.childrentracking.Models;

import java.sql.Timestamp;

public class BrowserHistory {
    @PrimaryKey
    public int Id;
    public int SessionId;
    public RecordingSession Session;
    public String Url;
    public String Title;
    public Timestamp VisitStart;
    public Timestamp VisitEnd;
}
