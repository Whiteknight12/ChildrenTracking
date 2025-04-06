package com.example.childrentracking;

import com.example.childrentracking.Models.BrowserVisit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BrowserHistoryCollector {
    private final List<BrowserVisit> browserHistory = new ArrayList<>();
    private boolean isCollecting = false;
    private Thread collectorThread;

    public void startCollecting() {
        isCollecting = true;
        collectorThread = new Thread(() -> {
           while (isCollecting) {
               try {
                   collectChromeHistory();
                   collectEdgeHistory();
                   Thread.sleep(60000);
               }
               catch (Exception e) {
                   e.printStackTrace();
               }
           }
        });
        collectorThread.start();
    }

    public void stopCollecting() {
        isCollecting = false;
        if (collectorThread != null) {
            collectorThread.interrupt();
        }
    }

    private void collectChromeHistory() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        Path historyPath = null;
        try {
            if (os.contains("win")) {
                historyPath = Paths.get(userHome, "AppData", "Local", "Google", "Chrome", "User Data", "Default", "History");
            } else if (os.contains("mac")) {
                historyPath = Paths.get(userHome, "Library", "Application Support", "Google", "Chrome", "Default", "History");
            } else if (os.contains("nix") || os.contains("nux")) {
                historyPath = Paths.get(userHome, ".config", "google-chrome", "Default", "History");
            }
            if (historyPath != null && Files.exists(historyPath)) {
                Path tempHistoryPath = Files.createTempFile("chrome_history", ".db");
                Files.copy(historyPath, tempHistoryPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                String url = "jdbc:sqlite:" + tempHistoryPath.toString();
                try (Connection conn = DriverManager.getConnection(url);
                     Statement stmt = conn.createStatement()) {
                    long cutoffTime = (System.currentTimeMillis() / 1000) - 3600;
                    long chromeEpochOffset = 11644473600L * 1000000L;
                    ResultSet rs = stmt.executeQuery(
                            "SELECT url, title, last_visit_time, visit_duration FROM urls " +
                                    "JOIN visits ON urls.id = visits.url " +
                                    "WHERE last_visit_time/1000000 - " + chromeEpochOffset/1000000 + " > " + cutoffTime +
                                    " ORDER BY last_visit_time DESC");
                    while (rs.next()) {
                        String urls = rs.getString("url");
                        String title = rs.getString("title");
                        long visitTime = rs.getLong("last_visit_time") / 1000000 - chromeEpochOffset/1000000;
                        long visitDuration = rs.getLong("visit_duration") / 1000000;
                        browserHistory.add(new BrowserVisit("Chrome", urls, title, visitTime * 1000, (visitTime + visitDuration) * 1000));
                    }
                }
                Files.deleteIfExists(tempHistoryPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void collectEdgeHistory() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        Path historyPath = null;
        try {
            if (os.contains("win")) {
                historyPath = Paths.get(userHome, "AppData", "Local", "Microsoft", "Edge", "User Data", "Default", "History");
            } else if (os.contains("mac")) {
                historyPath = Paths.get(userHome, "Library", "Application Support", "Microsoft", "Edge", "Default", "History");
            } else if (os.contains("nix") || os.contains("nux")) {
                historyPath = Paths.get(userHome, ".config", "microsoft-edge", "Default", "History");
            }
            if (historyPath != null && Files.exists(historyPath)) {
                Path tempHistoryPath = Files.createTempFile("edge_history", ".db");
                Files.copy(historyPath, tempHistoryPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                String url = "jdbc:sqlite:" + tempHistoryPath.toString();
                try (Connection conn = DriverManager.getConnection(url);
                     Statement stmt = conn.createStatement()) {
                    long cutoffTime = (System.currentTimeMillis() / 1000) - 3600;
                    long edgeEpochOffset = 11644473600L * 1000000L;
                    ResultSet rs = stmt.executeQuery(
                            "SELECT url, title, last_visit_time, visit_duration FROM urls " +
                                    "JOIN visits ON urls.id = visits.url " +
                                    "WHERE last_visit_time/1000000 - " + edgeEpochOffset/1000000 + " > " + cutoffTime +
                                    " ORDER BY last_visit_time DESC");
                    while (rs.next()) {
                        String urls = rs.getString("url");
                        String title = rs.getString("title");
                        long visitTime = rs.getLong("last_visit_time") / 1000000 - edgeEpochOffset/1000000;
                        long visitDuration = rs.getLong("visit_duration") / 1000000;
                        browserHistory.add(new BrowserVisit("Edge", urls, title, visitTime * 1000, (visitTime + visitDuration) * 1000));
                    }
                }
                Files.deleteIfExists(tempHistoryPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<BrowserVisit> getBrowserHistory() {
        return browserHistory;
    }
}
