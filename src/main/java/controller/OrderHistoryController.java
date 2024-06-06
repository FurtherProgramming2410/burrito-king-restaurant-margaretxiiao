package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.Order;
import model.User;
import utils.SceneChanger;
import utils.UserSession;

public class OrderHistoryController {

    @FXML
    private Button button_logout;

    @FXML
    private Button button_home;
    
    @FXML
    private Button button_neworder;

    @FXML
    private Button button_viewprofile;

    @FXML
    public void initialize() {
        button_logout.setOnAction(this::handleLogout);
        button_home.setOnAction(this::handleHome);
        button_neworder.setOnAction(this::handleNewOrder);
        button_viewprofile.setOnAction(this::handleViewProfile);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.clearSession();
        SceneChanger.changeScene(event, "/view/Main.fxml", "Log in!", 700, 500);
    }

    @FXML
    private void handleHome(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/LoggedIn.fxml", "Home", 1200, 800, controller -> {
            if (controller instanceof LoggedInController) {
                LoggedInController loggedInController = (LoggedInController) controller;
                User loggedInUser = UserSession.getLoggedInUser();
                if (loggedInUser != null) {
                    loggedInController.setUserInformation(loggedInUser.getFirstname(), loggedInUser.getLastname());
                }
            }
        });
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/Profile.fxml", "Profile", 1200, 800);
    }

    @FXML
    private void handleNewOrder(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/NewOrder.fxml", "New Order", 1200, 800);
    }

}
