package com.example.childrentracking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProcessMonitor {
    private final Map<String, Long> appUsageData=new HashMap<>();
    private final Map<String, Long> appStartTimes=new HashMap<>();
    private ScheduledExecutorService scheduler;
    private String lastActiveApp="";

    public void startMonitoring() {
        scheduler= Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::checkActiveApplications, 0, 1, TimeUnit.SECONDS);
    }

    public void stopMonitoring() {
        if (scheduler != null) {
            scheduler.shutdown();
            if (!lastActiveApp.isEmpty()) {
                updateAppUsage(lastActiveApp);
            }
        }
    }

    private void checkActiveApplications() {
        try {
            String activeApp=getActiveWindowTitle();
            if (!activeApp.equals(lastActiveApp)) {
                if (!lastActiveApp.isEmpty()) {
                    updateAppUsage(lastActiveApp);
                }
                appStartTimes.put(activeApp, System.currentTimeMillis());
                lastActiveApp = activeApp;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getActiveWindowTitle() throws Exception{
        String result="";
        String os=System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Process process = Runtime.getRuntime().exec("powershell -command \"Add-Type -AssemblyName System.Windows.Forms; [System.Windows.Forms.Screen]::GetWorkingArea($null)\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process = Runtime.getRuntime().exec("powershell -command \"Get-Process | Where-Object {$_.MainWindowTitle -ne ''} | Select-Object -First 1 MainWindowTitle\"");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.contains("MainWindowTitle")) {
                    result = line.trim();
                    break;
                }
            }
        }
        else if (os.contains("mac")) {
            Process process = Runtime.getRuntime().exec(new String[]{"osascript", "-e", "tell application \"System Events\"" + " to get name of first application process whose frontmost is true"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            result = reader.readLine();
        } else if (os.contains("nix") || os.contains("nux")) {
            Process process = Runtime.getRuntime().exec("xdotool getwindowfocus getwindowname");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            result = reader.readLine();
        }
        return result != null ? result : "Unknown";
    }

    private void updateAppUsage(String appName) {
        long startTime = appStartTimes.getOrDefault(appName, System.currentTimeMillis());
        long duration = System.currentTimeMillis() - startTime;
        appUsageData.put(appName, appUsageData.getOrDefault(appName, 0L) + duration);
    }

    public Map<String, Long> getAppUsageData() {
        if (!lastActiveApp.isEmpty()) {
            updateAppUsage(lastActiveApp);
        }
        return appUsageData;
    }
}
