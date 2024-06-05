import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoggedInController {

    private String loggedInUsername; 

    @FXML
    private Button button_logout;

    @FXML
    private Label label_welcome;

    @FXML
    private Button button_viewprofile;

    @FXML
    public void initialize() {
        button_logout.setOnAction(this::handleLogout);
        button_viewprofile.setOnAction(this::handleViewProfileButton);
    }

    private void handleLogout(ActionEvent event) {
        double newWidth = 700;
        double newHeight = 500;
        DatabaseUtils.changeScene(event, "/view/Main.fxml", "Log in!", loggedInUsername, null, null, newWidth, newHeight);
    }

    public void setUserInformation(String firstname, String lastname) {
        label_welcome.setText("Welcome " + firstname + " " + lastname + "!");
    }

    // Setter for loggedInUsername
    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }
    


    @FXML
    private void handleViewProfileButton(ActionEvent event) {
        try {
            // Load the profile.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/profile.fxml"));
           
            
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
