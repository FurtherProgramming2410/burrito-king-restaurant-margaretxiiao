package controller;

import dao.OrderDao;
import dao.OrderDaoImpl;
import dao.UserDao;
import dao.UserDaoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import model.Order;
import model.User;
import utils.SceneChanger;
import utils.UserSession;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeController {

	// home labels 
	
    @FXML
    private Label label_welcome;

    @FXML
    private Label label_vipstatus;

    // nav and function buttons
    
    @FXML
    private Button button_logout;

    @FXML
    private Button button_neworder;

    @FXML
    private Button button_vieworders;

    @FXML
    private Button button_viewprofile;

    @FXML
    private Button button_unlockvip;

    @FXML
    private ListView<String> list_activeOrders;

    private final OrderDao orderDao = new OrderDaoImpl();
    private final UserDao userDao = new UserDaoImpl();

    @FXML
    public void initialize() {
        button_logout.setOnAction(this::handleLogout);
        button_viewprofile.setOnAction(this::handleViewProfile);
        button_neworder.setOnAction(this::handleNewOrder);
        button_vieworders.setOnAction(this::handleViewOrders);
        button_unlockvip.setOnAction(this::handleUnlockVIP);

        User loggedInUser = UserSession.getLoggedInUser();
        if (loggedInUser != null) {
            setUserInformation(loggedInUser.getFirstname(), loggedInUser.getLastname());
            loadActiveOrders(loggedInUser.getUserId());

            if (loggedInUser.isVip()) {
                updateVIPStatus(true);
            } else {
                updateVIPStatus(false);
            }
        }
    }

    public void setUserInformation(String firstname, String lastname) {
        label_welcome.setText("Welcome " + firstname + " " + lastname + "!");
    }

    private void loadActiveOrders(int userId) {
        try {
            List<Order> activeOrders = orderDao.getActiveOrdersByUserId(userId);
            displayActiveOrders(activeOrders);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Order Loading Error", "Failed to load active orders.");
        }
    }

    private void displayActiveOrders(List<Order> activeOrders) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Order order : activeOrders) {
            String orderSummary = "Order #" + order.getId() +
                                  "\nItems: " + order.getOrderedItems() +
                                  "\nTotal: $" + order.getTotalPrice() +
                                  "\nStatus: " + order.getOrderStatus();
            items.add(orderSummary);
        }
        list_activeOrders.setItems(items);
    }

    // handle unlock vip button event
    @FXML
    private void handleUnlockVIP(ActionEvent event) {
        User loggedInUser = UserSession.getLoggedInUser();
        if (loggedInUser != null) {
            showVIPDialog(loggedInUser);
        }
    }

    private void showVIPDialog(User user) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Upgrade to VIP");
        dialog.setHeaderText("Would you like to receive promotion information via email to become a VIP?");

        // cancel and upgrade buttons
        ButtonType upgradeButtonType = new ButtonType("Upgrade", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(upgradeButtonType, ButtonType.CANCEL);

        // email input field
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email address");

        // layout of gridpane pop up
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == upgradeButtonType) {
                String email = emailField.getText();
                if (isValidEmail(email)) {
                    try {
                        userDao.upgradeToVIP(user.getUserId());
                        updateVIPStatus(true);
                        showAlert(Alert.AlertType.INFORMATION, "VIP Upgrade Successful", "You are now a VIP user! Please log out and log in again to access VIP functionalities.");
                        return null;
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Upgrade Error", "Failed to upgrade to VIP. Please try again.");
                        e.printStackTrace();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // method to update isVIP status in database
    private void updateVIPStatus(boolean isVip) {
        if (isVip) {
            button_unlockvip.setVisible(false);
            label_vipstatus.setText("Thank you for being a VIP member!");
        } else {
            button_unlockvip.setVisible(true);
            label_vipstatus.setText("Become VIP");
        }
    }

    // validate email 
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
    
    // nav methods
    
    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.clearSession();
        SceneChanger.changeScene(event, "/view/SignIn.fxml", "Log in!", 700, 500);
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
