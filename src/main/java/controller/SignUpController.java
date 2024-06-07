package controller;

import dao.UserDao;
import dao.UserDaoImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utils.SceneChanger;

import java.sql.SQLException;

public class SignUpController {

    // buttons
    @FXML
    private Button button_signup;

    @FXML
    private Button button_signin;

    // input fields 
    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    private final UserDao userDao = new UserDaoImpl();

    @FXML
    public void initialize() {
        button_signup.setOnAction(this::handleSignUp);
        button_signin.setOnAction(this::handleSignIn);
    }

    private void handleSignUp(ActionEvent event) {
        String firstNameText = firstName.getText();
        String lastNameText = lastName.getText();
        String usernameText = username.getText();
        String passwordText = password.getText();

        if (validateInput(firstNameText, lastNameText, usernameText, passwordText)) {
            try {
                if (userDao.getUserByUsername(usernameText) != null) {
                    showAlert(Alert.AlertType.ERROR, "Sign Up Error", "Username already exists. Please choose another username.");
                } else {
                    boolean signUpSuccess = userDao.createUser(usernameText, firstNameText, lastNameText, passwordText);
                    if (signUpSuccess) {
                        showAlert(Alert.AlertType.INFORMATION, "Sign Up Successful", "Sign up successful! You can now log in.");
                        SceneChanger.changeScene(event, "/view/SignIn.fxml", "Log in!", 700, 500);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Sign Up Error", "Failed to sign up. Please try again.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Sign Up Error", "Couldn't complete sign up. Please try again.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Sign Up Error", "Please fill in all information.");
        }
    }
    
    // check if fields are not empty
    private boolean validateInput(String firstName, String lastName, String username, String password) {
        return !firstName.trim().isEmpty() && !lastName.trim().isEmpty() && !username.trim().isEmpty() && !password.trim().isEmpty();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    // redirect to sign in page on click of the sign in button
    private void handleSignIn(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/SignIn.fxml", "Log in!", 700, 500);
    }
}
