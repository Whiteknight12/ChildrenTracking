package com.example.childrentracking.Controllers;

import com.example.childrentracking.DataTableClass.UserTable;
import com.example.childrentracking.Database.ApplicationDbContext;
import com.example.childrentracking.Models.User;
import com.example.childrentracking.Viewmodels.AccountCreateViewModel;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;

public class AccountCreateController {
    private final AccountCreateViewModel viewModel=new AccountCreateViewModel();

    @FXML
    private Button ButtonReset;

    @FXML
    private Button ButtonSignUp;

    @FXML
    private TextField email;

    @FXML
    private TextField password;

    @FXML
    private TextField phonenumber;

    @FXML
    private TextField username;

    @FXML
    private Hyperlink LoginLink;

    @FXML
    public void initialize() {
        email.textProperty().bindBidirectional(viewModel.emailProperty());
        password.textProperty().bindBidirectional(viewModel.passwordProperty());
        username.textProperty().bindBidirectional(viewModel.usernameProperty());
        phonenumber.textProperty().bindBidirectional(viewModel.phoneProperty());

        ButtonReset.setOnAction((ActionEvent event) -> {
            viewModel.setEmail("");
            viewModel.setPassword("");
            viewModel.setUsername("");
            viewModel.setPhone("");
        });
        ButtonSignUp.setOnAction((ActionEvent event) -> signUp(event));
        LoginLink.setOnAction((ActionEvent event) -> goBackToLogin(event));
    }

    private void goBackToLogin(ActionEvent event) {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/example/childrentracking/Layout/LoginView.fxml"));
        Stage stage=(Stage)LoginLink.getScene().getWindow();
        double width=stage.getScene().getWindow().getWidth();
        double height=stage.getScene().getWindow().getHeight();
        try {
            Parent root=loader.load();
            Scene scene=new Scene(root, width, height);
            stage.setScene(scene);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void signUp(ActionEvent event) {
        if (email.getText().isEmpty() || password.getText().isEmpty() || phonenumber.getText().isEmpty() || username.getText().isEmpty()) {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Không được bỏ trống các trường!");
            alert.showAndWait();
        }
        else {
            Connection connection = ApplicationDbContext.getConnection();
            UserTable userTable=new UserTable(connection);
            User tmp1=userTable.findBySpecialProperty("UserName", username.getText());
            User tmp2=userTable.findBySpecialProperty("Email", email.getText());
            User tmp3=userTable.findBySpecialProperty("Phone", phonenumber.getText());
            if (tmp1!=null) {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Tên đăng nhập đã tồn tại");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            if (tmp2!=null) {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Email đã tồn tại!");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            if (tmp3!=null) {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Số điện thoại đã được sử dụng");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            User user=new User(username.getText(), password.getText(), "Customer", email.getText(), null, 0, null, phonenumber.getText());
            userTable.insert(user);
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/example/childrentracking/Layout/LoginView.fxml"));
            Stage stage=(Stage)LoginLink.getScene().getWindow();
            try {
                double width=stage.getScene().getWindow().getWidth();
                double height=stage.getScene().getWindow().getHeight();
                Parent root=loader.load();
                Scene scene=new Scene(root, width, height);
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
