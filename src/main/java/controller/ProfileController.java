package controller;

import dao.UserDao;
import dao.UserDaoImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import model.User;
import utils.SceneChanger;
import utils.UserSession;

import java.sql.SQLException;

public class ProfileController {

	// texts to display user info in profile
    @FXML
    private Text profile_firstname;

    @FXML
    private Text profile_lastname;

    @FXML
    private Text profile_username;

    @FXML
    private Text profile_password;
    
    // update fields change
    @FXML
    private TextField field_firstname;

    @FXML
    private TextField field_lastname;

    @FXML
    private PasswordField field_password; 
    
    // editing updating buttons
    
    @FXML
    private Button button_editprofile;

    @FXML
    private Button button_saveprofile;

    
    // nav buttons
    
    @FXML
    private Button button_logout;

    @FXML
    private Button button_home;
    
    @FXML
    private Button button_vieworders;

    private final UserDao userDao = new UserDaoImpl();

    @FXML
    public void initialize() {
        loadUserProfile();
    }

    private void loadUserProfile() {
        User user = UserSession.getLoggedInUser();
        if (user != null) {
            profile_firstname.setText(user.getFirstname());
            profile_lastname.setText(user.getLastname());
            profile_username.setText(user.getUsername());
            profile_password.setText("********");
            
            // hide the editable fields initially
            field_firstname.setVisible(false);
            field_lastname.setVisible(false);
            field_password.setVisible(false);
            button_saveprofile.setVisible(false);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "User not found.");
        }
    }

    @FXML
    private void handleEditProfile() {
        field_firstname.setVisible(true);
        field_lastname.setVisible(true);
        field_password.setVisible(true);

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
        String updatedFirstname = field_firstname.getText();
        String updatedLastname = field_lastname.getText();
        String updatedPassword = field_password.getText();

        if (validateInput(updatedFirstname, updatedLastname, updatedPassword)) {
            try {
                boolean updateSuccess = userDao.updateUser(UserSession.getLoggedInUser().getUsername(), updatedFirstname, updatedLastname, updatedPassword);
                if (updateSuccess) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");

                    // update session user info
                    UserSession.getLoggedInUser().setFirstname(updatedFirstname);
                    UserSession.getLoggedInUser().setLastname(updatedLastname);
                    UserSession.getLoggedInUser().setPassword(updatedPassword);

                    // reload updated profile information
                    loadUserProfile();

                    field_firstname.setVisible(false);
                    field_lastname.setVisible(false);
                    field_password.setVisible(false);
                    button_saveprofile.setVisible(false);
                    button_editprofile.setVisible(true);

                    profile_firstname.setVisible(true);
                    profile_lastname.setVisible(true);
                    profile_password.setVisible(true);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Update Error", "Failed to update profile. Please try again.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Update Error", "An error occurred while updating profile.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Update Error", "Please fill in all fields.");
        }
    }

    private boolean validateInput(String firstname, String lastname, String password) {
        return !firstname.trim().isEmpty() && !lastname.trim().isEmpty() && !password.trim().isEmpty();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // nav methods

    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.clearSession();
        SceneChanger.changeScene(event, "/view/SignIn.fxml", "Log in!", 700, 500);
    }

    @FXML
    private void handleHome(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/Home.fxml", "Home", 1200, 800);
    }

    @FXML
    private void handleNewOrderButton(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/NewOrder.fxml", "New Order", 1200, 800);
    }
    
    @FXML
    private void handleViewOrders(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/OrderHistory.fxml", "Order History", 1200, 800);
    }
}
