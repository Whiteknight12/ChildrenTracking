package com.example.childrentracking.Models;

public class BrowserVisit {
    private final String browser;
    private final String url;
    private final String title;
    private final long startTime;
    private final long endTime;

    public BrowserVisit(String browser, String url, String title, long startTime, long endTime) {
        this.browser = browser;
        this.url = url;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getBrowser() {
        return browser;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getDuration() {
        return endTime - startTime;
    }
}
