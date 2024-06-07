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
import javafx.stage.FileChooser;
import model.Order;
import model.User;
import utils.SceneChanger;
import utils.UserSession;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryController {

    // nav and function buttons
    @FXML
    private Button button_logout;

    @FXML
    private Button button_home;

    @FXML
    private Button button_neworder;

    @FXML
    private Button button_viewprofile;
    
    @FXML
    private Button button_exportorders;

    // order table view and columns
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
        button_exportorders.setOnAction(this::handleExportOrders);

        column_orderNumber.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_orderTime.setCellValueFactory(new PropertyValueFactory<>("formattedOrderTime"));
        column_totalPrice.setCellValueFactory(new PropertyValueFactory<>("formattedTotalPrice"));
        column_status.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

        loadOrderHistory();
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
            showAlert(Alert.AlertType.ERROR, "Invalid Selection", "You can only collect orders that are placed and not already collected or cancelled.");
        }
    }

    private void showCollectOrderDialog() {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Collect Order");
        dialog.setHeaderText("When did you collect this order?");

        // Button types
        ButtonType collectButtonType = new ButtonType("Collect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(collectButtonType, ButtonType.CANCEL);

        // Date and time pickers for order collection time
        DatePicker datePicker = new DatePicker();
        ComboBox<LocalTime> comboBox_time = new ComboBox<>();

        ObservableList<LocalTime> times = FXCollections.observableArrayList();
        for (int hour = 9; hour <= 17; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                times.add(LocalTime.of(hour, minute));
            }
        }
        comboBox_time.setItems(times);

        // gridpane to display date and time pickers for collection.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        grid.add(comboBox_time, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // to convert result to a LocalDateTime when collect button is clicked
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

        dialog.showAndWait().ifPresent(this::handleCollectOrderConfirmation);
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

            // Confirm cancellation of order pop up
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
            showAlert(Alert.AlertType.ERROR, "Invalid Selection", "You can only cancel orders that are not already collected.");
        }
    }

    @FXML
    private void handleExportOrders(ActionEvent event) {
        // dialogue for selecting export options
        Dialog<List<Order>> dialog = new Dialog<>();
        dialog.setTitle("Export Orders");
        dialog.setHeaderText("Select orders and details to export");

        // buttons for dialog
        ButtonType exportButtonType = new ButtonType("Export", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(exportButtonType, ButtonType.CANCEL);

        // tableView for selecting orders
        TableView<Order> orderSelectionTable = new TableView<>();
        orderSelectionTable.setItems(table_orders.getItems()); // same items as in the main table
        orderSelectionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<Order, Integer> orderNumberColumn = new TableColumn<>("Order Number");
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Order, String> orderTimeColumn = new TableColumn<>("Order Time");
        orderTimeColumn.setCellValueFactory(new PropertyValueFactory<>("formattedOrderTime"));
        TableColumn<Order, Double> totalPriceColumn = new TableColumn<>("Total Price");
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        TableColumn<Order, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

        // to show all the table columns for selection to export
        orderSelectionTable.getColumns().addAll(orderNumberColumn, orderTimeColumn, totalPriceColumn, statusColumn);

        // width of selection table set
        orderSelectionTable.setPrefWidth(600); // Adjust the width value as needed

        // checkboxes for selecting details to export
        CheckBox cbOrderNumber = new CheckBox("Order Number");
        CheckBox cbOrderTime = new CheckBox("Order Time");
        CheckBox cbTotalPrice = new CheckBox("Total Price");
        CheckBox cbStatus = new CheckBox("Status");

        cbOrderNumber.setSelected(true);
        cbOrderTime.setSelected(true);
        cbTotalPrice.setSelected(true);
        cbStatus.setSelected(true);

        // layout for dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.setPrefWidth(700); //gridpane width

        grid.add(new Label("Select Orders:"), 0, 0);
        grid.add(orderSelectionTable, 0, 1, 2, 1);
        grid.add(new Label("Select Details to Export:"), 0, 2);
        grid.add(cbOrderNumber, 0, 3);
        grid.add(cbOrderTime, 0, 4);
        grid.add(cbTotalPrice, 0, 5);
        grid.add(cbStatus, 0, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == exportButtonType) {
                return new ArrayList<>(orderSelectionTable.getSelectionModel().getSelectedItems());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ordersToExport -> {
            if (!ordersToExport.isEmpty()) {
                exportOrdersToCSV(ordersToExport, cbOrderNumber.isSelected(), cbOrderTime.isSelected(), cbTotalPrice.isSelected(), cbStatus.isSelected());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No Orders Selected", "Please select at least one order to export.");
            }
        });
    }

    private void exportOrdersToCSV(List<Order> orders, boolean includeOrderNumber, boolean includeOrderTime, boolean includeTotalPrice, boolean includeStatus) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Orders");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(button_exportorders.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                List<String> headers = new ArrayList<>();
                if (includeOrderNumber) headers.add("Order Number");
                if (includeOrderTime) headers.add("Order Time");
                if (includeTotalPrice) headers.add("Total Price");
                if (includeStatus) headers.add("Status");

                writer.write(String.join(",", headers));
                writer.newLine();

                for (Order order : orders) {
                    List<String> values = new ArrayList<>();
                    if (includeOrderNumber) values.add(String.valueOf(order.getId()));
                    if (includeOrderTime) values.add(order.getFormattedOrderTime());
                    if (includeTotalPrice) values.add(order.getFormattedTotalPrice());
                    if (includeStatus) values.add(order.getOrderStatus());

                    writer.write(String.join(",", values));
                    writer.newLine();
                }

                writer.flush();

                // successful export alert
                showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Orders exported successfully!");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Export Failed", "Failed to export orders to CSV file.");
                e.printStackTrace();
            }
        }
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
    private void handleHome(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/Home.fxml", "Home", 1200, 800, controller -> {
            if (controller instanceof HomeController) {
                HomeController loggedInController = (HomeController) controller;
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