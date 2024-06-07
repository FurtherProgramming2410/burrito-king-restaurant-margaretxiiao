package controller;

import dao.UserDao;
import dao.UserDaoImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import model.User;
import utils.SceneChanger;
import utils.UserSession;

import java.sql.SQLException;

public class LogInController {

    private final UserDao userDao = new UserDaoImpl();

    // buttons
    
    @FXML
    private Button button_signup;
    
    @FXML
    private Button button_signin;

    // input fields for login
    @FXML
    private TextField password;

    @FXML
    private TextField username;



    @FXML
    public void initialize() {
        button_signin.setOnAction(this::handleSignIn);
        
        // change to signup view
        button_signup.setOnAction(event -> {
            SceneChanger.changeScene(event, "/view/SignUp.fxml", "Sign Up", 700, 500);
        });
    }

    @FXML
    private void handleSignIn(ActionEvent event) {
        String usernameText = username.getText();
        String passwordText = password.getText();

        if (!usernameText.trim().isEmpty() && !passwordText.trim().isEmpty()) {
            try {
                User user = userDao.getUser(usernameText, passwordText);
                if (user != null) {
                    // set the user session with the logged-in user
                    UserSession.setLoggedInUser(user);
                    // display success alert and open the logged-in window
                    showSuccessAlert(event, user.getFirstname(), user.getLastname());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Credentials", "Invalid username or password!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred while trying to log in.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Missing Credentials", "Please enter username and password!");
        }
    }

    // display success alert
    private void showSuccessAlert(ActionEvent event, String firstname, String lastname) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Successful");
        alert.setHeaderText(null);
        alert.setContentText("Welcome, " + firstname + " " + lastname + "!");

     // once the alert is closed, open the dashboard
        alert.setOnHidden(e -> {
            // close the current login window
            Stage loginStage = (Stage) button_signin.getScene().getWindow();
            loginStage.close();
            // open the logged-in window
            openLoggedInWindow(event);
        });

        alert.show();
    }

    private void openLoggedInWindow(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/Home.fxml", "Welcome", 1200, 800, controller -> {
            if (controller instanceof HomeController) {
                HomeController loggedInController = (HomeController) controller;
                User loggedInUser = UserSession.getLoggedInUser();
                if (loggedInUser != null) {
                    loggedInController.setUserInformation(loggedInUser.getFirstname(), loggedInUser.getLastname());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
