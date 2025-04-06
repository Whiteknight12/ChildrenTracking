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

    public BrowserHistory(int sessionid, String url, String title, Timestamp visitStart, Timestamp visitEnd) {
        SessionId=sessionid;
        Url=url;
        Title=title;
        VisitStart=visitStart;
        VisitEnd=visitEnd;
    }
}
