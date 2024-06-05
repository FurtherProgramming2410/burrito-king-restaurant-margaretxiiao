import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ProfileController {

    @FXML
    private Text profile_firstname;

    @FXML
    private Text profile_lastname;

    @FXML
    private Text profile_username;
    
    @FXML
    private Text profile_password;

    @FXML
    private TextField field_firstname;

    @FXML
    private TextField field_lastname;

    @FXML
    private PasswordField field_password;
    
    @FXML
    private Button button_editprofile;
    
    @FXML
    private Button button_saveprofile;

    @FXML
    private Button button_logout;

    @FXML
    private Button button_home;

    // initialize the profile information
    public void initialize() {

        String username = "xmx"; 
        String[] userInfo = DatabaseUtils.getUserInfo(username);
        if (userInfo != null) {
            // Display profile info
            profile_firstname.setText(userInfo[0]);
            profile_lastname.setText(userInfo[1]);
            profile_username.setText(username);
        } else {
            System.out.println("User not found in the database!");
        }
        
        // Hide editable fields initially
        field_firstname.setVisible(false);
        field_lastname.setVisible(false);
        field_password.setVisible(false);
        
        button_saveprofile.setVisible(false);
    }

    // editing the profile
    @FXML
    private void handleEditProfile() {
        // Enable text fields for editing
        field_firstname.setVisible(true);
        field_firstname.setEditable(true);
        
        field_lastname.setVisible(true);
        field_lastname.setEditable(true);
        
        field_password.setVisible(true);
        field_password.setEditable(true);

        // Setting the text of text fields to the current profile info
        field_firstname.setText(profile_firstname.getText());
        field_lastname.setText(profile_lastname.getText());

        field_password.setText("");
        
        profile_firstname.setVisible(false);
        profile_lastname.setVisible(false);
        profile_password.setVisible(false);
        
        button_editprofile.setVisible(false);
        
        button_saveprofile.setVisible(true);
    }

    @FXML
    private void handleSaveProfile() {
        // Get updated profile information from text fields
        String updatedFirstname = field_firstname.getText();
        String updatedLastname = field_lastname.getText();
        String updatedPassword = field_password.getText();

        // Update profile information in the database
        String username = "xmx";
        DatabaseUtils.updateProfile(username, updatedFirstname, updatedLastname, updatedPassword);

        // Display updated profile information
        profile_firstname.setText(updatedFirstname);
        profile_lastname.setText(updatedLastname);
        profile_password.setText(updatedPassword);

        // Disable text fields after saving
        field_firstname.setVisible(false);
        field_lastname.setVisible(false);
        field_password.setVisible(false);
        
        // Hide save button
        button_saveprofile.setVisible(false);
        
        // Show edit button
        button_editprofile.setVisible(true);
        
        // successful alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Profile updated successfully!");
        alert.showAndWait();
        
        // Reload the profile window to show new info
        reloadProfileWindow();
    }

    // refresh the profile window after update
    private void reloadProfileWindow() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Profile.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) button_logout.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // handle logout
    @FXML
    private void handleLogout(ActionEvent event) {
        Stage stage = (Stage) button_logout.getScene().getWindow();
        stage.close();
    }

    // go back to home
    @FXML
    private void handleHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoggedIn.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) button_home.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

