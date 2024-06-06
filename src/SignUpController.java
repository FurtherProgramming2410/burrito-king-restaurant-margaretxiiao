import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {

	// buttons
    @FXML
    private Button button_signup;

    @FXML
    private Button button_signin;

    // fields 
    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

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

        if (!usernameText.trim().isEmpty() && !firstNameText.trim().isEmpty() && !lastNameText.trim().isEmpty() && !passwordText.trim().isEmpty()) {
            boolean signUpSuccess = DatabaseUtils.signUpUser(event, usernameText, firstNameText, lastNameText, passwordText);
            
            if (signUpSuccess) {
                // alert if successful
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Sign Up Successful");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Sign up successful!");
                successAlert.show();
            } else {
                // error alert
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Sign Up Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to sign up. Please try again.");
                errorAlert.show();
            }
        } else {
            // validation alert
            Alert validationAlert = new Alert(Alert.AlertType.ERROR);
            validationAlert.setContentText("Please fill in all information");
            validationAlert.show();
        }
    }


    // redirect to signin page on click of the signin button
    private void handleSignIn(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
            Scene scene = new Scene(root, 700, 500);
            Stage stage = (Stage) button_signin.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
