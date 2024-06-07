package controller;

import dao.OrderDao;
import dao.OrderDaoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent;
import model.Order;
import model.User;
import utils.SceneChanger;
import utils.UserSession;

import java.sql.SQLException;
import java.util.List;

public class HomeController {

    @FXML
    private Label label_welcome;

    @FXML
    private Button button_logout;

    @FXML
    private Button button_neworder;

    @FXML
    private Button button_vieworders;

    @FXML
    private Button button_viewprofile;

    @FXML
    private ListView<String> list_activeOrders;

    private final OrderDao orderDao = new OrderDaoImpl();

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
            loadActiveOrders(loggedInUser.getUserId());
        }
    }

    public void setUserInformation(String firstname, String lastname) {
        label_welcome.setText("Welcome " + firstname + " " + lastname + "!");
    }

    // loading orders by user_id
    
    private void loadActiveOrders(int userId) {
        try {
            List<Order> activeOrders = orderDao.getActiveOrdersByUserId(userId);
            displayActiveOrders(activeOrders);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Order Loading Error", "Failed to load active orders.");
        }
    }

    // showing active orders
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

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
