import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class LogInController {

    @FXML
    private Button button_signin; // Make sure this field matches the fx:id in your FXML file
    
    @FXML
    private void handleSignInButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoggedIn.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) button_signin.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    } 

    @FXML
    private Button button_signup; // Make sure this field matches the fx:id in your FXML file

    @FXML
    private void handleSignUpButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) button_signup.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}