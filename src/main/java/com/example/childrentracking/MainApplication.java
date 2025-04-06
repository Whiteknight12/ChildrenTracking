package com.example.childrentracking;

import com.example.childrentracking.DataTableClass.AppUsageTable;
import com.example.childrentracking.DataTableClass.BrowserHistoryTable;
import com.example.childrentracking.DataTableClass.RecordingSessionTable;
import com.example.childrentracking.DataTableClass.UserTable;
import com.example.childrentracking.Database.ApplicationDbContext;
import com.example.childrentracking.Models.AppUsage;
import com.example.childrentracking.Models.BrowserHistory;
import com.example.childrentracking.Models.RecordingSession;
import com.example.childrentracking.Models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.Connection;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setupDatabase();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Layout/LoginView.fxml"));
        Scene scene=new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("UlTrack");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Images/appicon.png")));
        primaryStage.show();
    }

    private void setupDatabase() {
        Connection connection=ApplicationDbContext.getConnection();
        UserTable userTable=new UserTable(connection);
        AppUsageTable appUsageTable=new AppUsageTable(connection);
        BrowserHistoryTable browserHistoryTable=new BrowserHistoryTable(connection);
        RecordingSessionTable recordingSessionTable=new RecordingSessionTable(connection);
        appUsageTable.createTable(new AppUsage());
        browserHistoryTable.createTable(new BrowserHistory());
        recordingSessionTable.createTable(new RecordingSession());
        userTable.createTable(new User());
    }
}
