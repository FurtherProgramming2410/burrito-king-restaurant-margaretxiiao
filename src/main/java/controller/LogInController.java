package controller;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInController {

    @FXML
    private Button button_signin;
    
    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField password;

    @FXML
    private TextField username;

    @FXML
    private Button button_signup;

    @FXML
    public void initialize() {
        button_signin.setOnAction(this::handleSignIn);


        button_signup.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignUp.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 700, 500);
                Stage stage = (Stage) button_signin.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleSignIn(ActionEvent event) {
        String usernameText = username.getText();
        String passwordText = password.getText();

        if (!usernameText.trim().isEmpty() && !passwordText.trim().isEmpty()) {
            if (DatabaseUtils.authenticateUser(usernameText, passwordText)) {
                // Get user's first and last name from the database
                String[] userInfo = DatabaseUtils.getUserInfo(usernameText);
                if (userInfo != null && userInfo.length == 2) {
                    String firstname = userInfo[0];
                    String lastname = userInfo[1];
                    openLoggedInWindow(event, firstname, lastname);
                } else {
                    System.out.println("Failed to retrieve user information");
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Invalid username or password!");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter username and password!");
            alert.show();
        }
    }


    private void openLoggedInWindow(ActionEvent event, String firstname, String lastname) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoggedIn.fxml"));
            Parent root = loader.load();
            LoggedInController loggedInController = loader.getController();
            loggedInController.setUserInformation(firstname, lastname);
            Scene scene = new Scene(root, 1200, 800);
            Stage stage = new Stage();
            stage.setScene(scene);

            // Display success alert
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Login Successful");
            alert.setHeaderText(null);
            alert.setContentText("Welcome, " + firstname + " " + lastname + "!");
            alert.setOnHidden(e -> {
                // Once the alert is closed, open the dashboard
                stage.show(); // Show the dashboard
                // Close the login window
                Stage loginStage = (Stage) button_signin.getScene().getWindow();
                loginStage.close();
            });
            alert.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}