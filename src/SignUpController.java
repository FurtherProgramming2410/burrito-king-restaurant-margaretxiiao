import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class SignUpController {

    @FXML
    private Button button_signin;
    
    @FXML
    private void handleSignUpButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) button_signin.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    } 
    
    //
    @FXML
    private Button button_signup; 

    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) button_signup.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}