package controller;

import dao.OrderDao;
import dao.OrderDaoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import model.Order;
import model.User;
import utils.SceneChanger;
import utils.UserSession;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class OrderHistoryController {

	// buttons
    @FXML
    private Button button_logout;

    @FXML
    private Button button_home;

    @FXML
    private Button button_neworder;

    @FXML
    private Button button_viewprofile;

    // table view and columns
    @FXML
    private TableView<Order> table_orders;

    @FXML
    private TableColumn<Order, Integer> column_orderNumber;

    @FXML
    private TableColumn<Order, String> column_orderTime;

    @FXML
    private TableColumn<Order, Double> column_totalPrice;

    @FXML
    private TableColumn<Order, String> column_status;

    private final OrderDao orderDao = new OrderDaoImpl();

    private Order selectedOrderForCollection;

    @FXML
    public void initialize() {
        button_logout.setOnAction(this::handleLogout);
        button_home.setOnAction(this::handleHome);
        button_neworder.setOnAction(this::handleNewOrder);
        button_viewprofile.setOnAction(this::handleViewProfile);

        column_orderNumber.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_orderTime.setCellValueFactory(new PropertyValueFactory<>("formattedOrderTime"));
        column_totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        column_status.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

        loadOrderHistory();
    }

    // nav methods
    
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
    
    

    private void loadOrderHistory() {
        User loggedInUser = UserSession.getLoggedInUser();
        if (loggedInUser != null) {
            try {
                List<Order> orderHistory = orderDao.getOrdersByUserId(loggedInUser.getUserId());
                displayOrderHistory(orderHistory);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Order History Error", "Failed to load order history.");
            }
        }
    }

    private void displayOrderHistory(List<Order> orderHistory) {
        ObservableList<Order> observableOrderHistory = FXCollections.observableArrayList(orderHistory);
        table_orders.setItems(observableOrderHistory);
    }

    @FXML
    private void handleCollectOrder(ActionEvent event) {
        selectedOrderForCollection = table_orders.getSelectionModel().getSelectedItem();
        if (selectedOrderForCollection != null && "placed".equals(selectedOrderForCollection.getOrderStatus())) {
            showCollectOrderDialog();
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid Order", "You can only collect orders that are placed and not already collected or cancelled.");
        }
    }

    private void showCollectOrderDialog() {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Collect Order");
        dialog.setHeaderText("When did you collect this order?");

        // button types
        ButtonType collectButtonType = new ButtonType("Collect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(collectButtonType, ButtonType.CANCEL);

        // date and time pickers for order collection time
        DatePicker datePicker = new DatePicker();
        ComboBox<LocalTime> comboBox_time = new ComboBox<>();

        ObservableList<LocalTime> times = FXCollections.observableArrayList();
        for (int hour = 9; hour <= 17; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                times.add(LocalTime.of(hour, minute));
            }
        }
        comboBox_time.setItems(times);

        // Create the grid pane and add the pickers.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        grid.add(comboBox_time, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // convert the result to a LocalDateTime when collect button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == collectButtonType) {
                LocalDate selectedDate = datePicker.getValue();
                LocalTime selectedTime = comboBox_time.getValue();
                if (selectedDate != null && selectedTime != null) {
                    return LocalDateTime.of(selectedDate, selectedTime);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedDateTime -> handleCollectOrderConfirmation(selectedDateTime));
    }

    @FXML
    private void handleCollectOrderConfirmation(LocalDateTime selectedDateTime) {
        if (selectedOrderForCollection != null) {
            Timestamp collectionTimestamp = Timestamp.valueOf(selectedDateTime);
            // minimum collection time order time + prep time
            Timestamp minimumCollectionTime = new Timestamp(selectedOrderForCollection.getOrderTime().getTime() + (selectedOrderForCollection.getPreparationTime() * 60 * 1000));
            if (collectionTimestamp.after(minimumCollectionTime)) {
                try {
                    orderDao.updateOrderStatus(selectedOrderForCollection.getId(), "collected");
                    loadOrderHistory();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Collection Error", "Failed to update order status. Please try again.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Time", "Order is not ready yet.");
            }
        }
    }

    @FXML
    private void handleCancelOrder(ActionEvent event) {
        Order selectedOrder = table_orders.getSelectionModel().getSelectedItem();
        if (selectedOrder != null && !"collected".equals(selectedOrder.getOrderStatus())) {
        	
        	// confirm cancellation of order pop up
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Cancel Order");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Are you sure you want to cancel this order?");
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        orderDao.updateOrderStatus(selectedOrder.getId(), "cancelled");
                        loadOrderHistory();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Cancellation Error", "Failed to cancel the order. Please try again.");
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid Order", "You can only cancel orders that are not already collected.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
