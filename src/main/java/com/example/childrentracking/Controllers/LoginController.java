package com.example.childrentracking.Controllers;

import com.example.childrentracking.DataTableClass.UserTable;
import com.example.childrentracking.Database.ApplicationDbContext;
import com.example.childrentracking.Models.User;
import com.example.childrentracking.UserSession;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.sql.Connection;

public class LoginController {
    @FXML
    private Hyperlink createnewaccount;

    @FXML
    private Hyperlink forgetpassword;

    @FXML
    private Button loginbutton;

    @FXML
    private Button loginbuttonwithgoogle;

    @FXML
    private PasswordField passwordtext;

    @FXML
    private CheckBox rememberme;

    @FXML
    private TextField usernametext;

    @FXML
    public void initialize() {
        createnewaccount.setOnAction(event -> goToCreateAccount(event));
    }

    private Connection connection= ApplicationDbContext.getConnection();
    private UserTable userTable=new UserTable(connection);

    private void goToCreateAccount(ActionEvent event) {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/example/childrentracking/Layout/AccountCreate.fxml"));
        try {
            Stage stage=(Stage)createnewaccount.getScene().getWindow();
            double width=stage.getScene().getWindow().getWidth();
            double height=stage.getScene().getWindow().getHeight();
            Parent root = loader.load();
            Scene scene=new Scene(root, width, height);
            stage.setScene(scene);

            root.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Login(ActionEvent event) {
        if (passwordtext.getText().isEmpty() || usernametext.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Không được bỏ trống các trường!");
            alert.showAndWait();
        }
        else {
            User user=userTable.findByTwoSpecialProperties("UserName", usernametext.getText(), "Password", passwordtext.getText());
            if (user == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Tên đăng nhập/ Mật khẩu không chính xác");
                alert.showAndWait();
            }
            else {
                UserSession.setUsername(usernametext.getText());
                UserSession.setUserid(user.UserId);
                Stage stage=(Stage)loginbutton.getScene().getWindow();

            }
        }
    }
}
