package com.example.childrentracking.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainViewController {
    @FXML
    private Button buttonaccount;

    @FXML
    private Button buttonhome;

    @FXML
    private Button buttonlogout;

    @FXML
    private Button buttonreport;

    @FXML
    private Button buttonschedule;

    @FXML
    private Button buttonsettings;

    @FXML
    private StackPane contentArea;


    @FXML
    private void handleHome(ActionEvent event) {
        setContent("/com/example/childrentracking/Layout/MainPage.fxml");
    }

    @FXML
    private void handleAccount(ActionEvent event) {

    }

    @FXML
    private void handleReport(ActionEvent event) {
        setContent("/com/example/childrentracking/Layout/DetailPage.fxml");
    }

    @FXML
    private void handleSettings(ActionEvent event) {

    }

    @FXML
    private void handleLogout(ActionEvent event) {

    }

    @FXML
    private void handleSchedule(ActionEvent event) {

    }

    private void setContent(String fxmlPath) {
        try {
            Parent pane = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        setContent("/com/example/childrentracking/Layout/MainPage.fxml");
    }
}
