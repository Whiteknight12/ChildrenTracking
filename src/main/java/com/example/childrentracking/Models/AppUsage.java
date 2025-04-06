package com.example.childrentracking.Models;

public class AppUsage {
    @PrimaryKey
    public int Id;
    public int SessionId;
    public RecordingSession Session;
    public String ApplicationName;
    public long UsageDuration;

    public AppUsage(int sessionid, String applicationname, long usageduration) {
        SessionId=sessionid;
        ApplicationName=applicationname;
        UsageDuration=usageduration;
    }

    public AppUsage() {}
}
