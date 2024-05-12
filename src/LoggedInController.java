import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class LoggedInController {

    @FXML
    private Button button_logout; // Make sure this field matches the fx:id in your FXML file
    
    @FXML
    private void handleLogoutButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) button_logout.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    } 

}