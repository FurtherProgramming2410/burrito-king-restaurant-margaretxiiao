import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {

    @FXML
    private Button button_signup;

    @FXML
    private Button button_signin;

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
            DatabaseUtils.signUpUser(event, usernameText, firstNameText, lastNameText, passwordText);
        } else {
            System.out.println("Please fill in all information");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please fill in all information");
            alert.show();
        }
    }

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
