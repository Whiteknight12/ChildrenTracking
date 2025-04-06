package com.example.childrentracking.Controllers;

import com.example.childrentracking.BrowserHistoryCollector;
import com.example.childrentracking.DataTableClass.AppUsageTable;
import com.example.childrentracking.DataTableClass.BrowserHistoryTable;
import com.example.childrentracking.DataTableClass.RecordingSessionTable;
import com.example.childrentracking.DataTableClass.UserTable;
import com.example.childrentracking.Database.ApplicationDbContext;
import com.example.childrentracking.Models.*;
import com.example.childrentracking.ProcessMonitor;
import com.example.childrentracking.SystemTrayManager;
import com.example.childrentracking.UserSession;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class MainPageController {
    private final Connection connection= ApplicationDbContext.getConnection();
    private final UserTable userTable=new UserTable(connection);
    private final AppUsageTable appUsageTable=new AppUsageTable(connection);
    private final BrowserHistoryTable browserHistoryTable=new BrowserHistoryTable(connection);
    private final RecordingSessionTable recordingSessionTable=new RecordingSessionTable(connection);
    private boolean isRecording = false;
    private long startRecordingTime;
    private Timeline recordingTimeline;
    private ProcessMonitor processMonitor;
    private BrowserHistoryCollector browserHistoryCollector;
    private SystemTrayManager trayManager;

    @FXML
    private Label clockLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Button recordButton;

    @FXML
    private Label recordingTimeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private void initialize() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        User user=userTable.findBySpecialProperty("UserName", UserSession.getUsername());
        if (user.FullName==null || user.FullName.isEmpty()) {
            usernameLabel.setText("Người dùng");
        }
        else usernameLabel.setText(user.FullName);
        recordButton.setOnAction(event -> toggleRecording());
        statusLabel.setText("Chưa ghi");
        Platform.runLater(() -> {
            Stage stage=(Stage) recordButton.getScene().getWindow();
            trayManager = new SystemTrayManager(stage);
            trayManager.setUp();
            stage.setOnCloseRequest(event -> {
                if (isRecording) {
                    event.consume();
                    trayManager.minimizeToTray();
                }
            });
        });
    }

    private void toggleRecording() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        isRecording = true;
        startRecordingTime = System.currentTimeMillis();
        recordButton.setText("Dừng ghi");
        statusLabel.setText("Đang ghi...");
        processMonitor = new ProcessMonitor();
        browserHistoryCollector = new BrowserHistoryCollector();
        processMonitor.startMonitoring();
        browserHistoryCollector.startCollecting();
        recordingTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            long elapsedTime = System.currentTimeMillis() - startRecordingTime;
            recordingTimeLabel.setText(formatDuration(elapsedTime));
        }));
        recordingTimeline.setCycleCount(Animation.INDEFINITE);
        recordingTimeline.play();
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }

    private void stopRecording() {
        isRecording = false;
        recordButton.setText("Bắt đầu ghi");
        statusLabel.setText("Đã dừng ghi");
        processMonitor.stopMonitoring();
        browserHistoryCollector.stopCollecting();
        recordingTimeline.stop();
        saveCollectedData();
    }

    private void saveCollectedData() {
        Map<String, Long> appUsageData = processMonitor.getAppUsageData();
        List<BrowserVisit> browserHistory = browserHistoryCollector.getBrowserHistory();
        try {
            RecordingSession recordingSession=new RecordingSession(UserSession.getUserid(), new Timestamp(startRecordingTime), new Timestamp(System.currentTimeMillis()));
            int sessionId=recordingSessionTable.insert(recordingSession);
            for (Map.Entry<String, Long> entry : appUsageData.entrySet()) {
                AppUsage appUsage=new AppUsage(sessionId, entry.getKey(), entry.getValue());
                appUsageTable.insert(appUsage);
            }
            for (BrowserVisit browserVisit : browserHistory) {
                BrowserHistory bh=new BrowserHistory(sessionId, browserVisit.getUrl(), browserVisit.getTitle(), new Timestamp(browserVisit.getStartTime()), new Timestamp(browserVisit.getEndTime()));
                browserHistoryTable.insert(bh);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void restoreFromTray() {
        Platform.runLater(() -> {
            Stage stage = (Stage) recordButton.getScene().getWindow();
            stage.show();
            stage.setIconified(false);
            stage.toFront();
        });
    }
}
