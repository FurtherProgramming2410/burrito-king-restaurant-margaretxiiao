package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import utils.SceneChanger;
import utils.UserSession;
import model.User;

public class LoggedInController {
	
	// welcome message
	
    @FXML
    private Label label_welcome;

    // nav buttons
    
    @FXML
    private Button button_logout;

    @FXML
    private Button button_neworder;
    
    @FXML
    private Button button_vieworders;

    @FXML
    private Button button_viewprofile;

    @FXML
    public void initialize() {
        button_logout.setOnAction(this::handleLogout);
        button_viewprofile.setOnAction(this::handleViewProfile);
        button_neworder.setOnAction(this::handleNewOrder);
        button_vieworders.setOnAction(this::handleViewOrders);

        // Set welcome message
        User loggedInUser = UserSession.getLoggedInUser();
        if (loggedInUser != null) {
            setUserInformation(loggedInUser.getFirstname(), loggedInUser.getLastname());
        }
    }

    // set the user's first and last name
    public void setUserInformation(String firstname, String lastname) {
        label_welcome.setText("Welcome " + firstname + " " + lastname + "!");
    }

    
    // nav methods
    
    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.clearSession();
        SceneChanger.changeScene(event, "/view/Main.fxml", "Log in!", 700, 500);
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/Profile.fxml", "Profile", 1200, 800);
    }

    @FXML
    private void handleNewOrder(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/NewOrder.fxml", "New Order", 1200, 800);
    }

    @FXML
    private void handleViewOrders(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/OrderHistory.fxml", "Order History", 1200, 800);
    }
}
