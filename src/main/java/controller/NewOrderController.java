package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import javafilesforlater.Burrito;
import javafilesforlater.Fries;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class NewOrderController implements DatabaseUtils.OrderStoredCallback {

    // buttons
    @FXML
    private Button button_logout;
    @FXML
    private Button button_home;
    @FXML
    private Button button_placeorder;
    @FXML
    private Button button_confirm_qtys;

    // quantities
    @FXML
    private TextField input_burrito_qty;
    @FXML
    private TextField input_fries_qty;
    @FXML
    private TextField input_soda_qty;
    @FXML
    private TextField input_meal_qty;

    // Order summary texts
    @FXML
    private Text burrito_qty;
    @FXML
    private Text fries_qty;
    @FXML
    private Text soda_qty;
    
    // these are for only vip users to view later
    @FXML
    private Button label_meal;
    @FXML
    private Label label_meal_qty;
    
    @FXML
    private Text meal_qty;
    @FXML
    private Text text_totalprice;

    // payment inputs
    @FXML
    private TextField input_cardnumber;
    @FXML
    private TextField input_cardcvv;
    @FXML
    private DatePicker input_cardexpiry;

    // Item specific subtotals before adding to the total price
    @FXML
    private Text text_burritocheckoutprice;
    @FXML
    private Text text_friescheckoutprice;
    @FXML
    private Text text_sodacheckoutprice;
    @FXML
    private Text text_checkoutmealprice;

    // Preparation time text
    @FXML
    private Text text_totalpreparationtime;

    // Constants for food prices
    private final double burritoPrice = 7.0;
    private final double friesPrice = 4.0;
    private final double sodaPrice = 2.5;
    private final double mealPrice = 10.50;

    // VIP status
    private boolean isVipUser;

    @FXML
    public void initialize() {
        button_confirm_qtys.setOnAction(event -> handleConfirmQuantities());
        button_placeorder.setOnAction(event -> {
            try {
                handlePlaceOrder();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        isVipUser = checkIfUserIsVip();

        if (!isVipUser) {
            input_meal_qty.setVisible(false);
            meal_qty.setVisible(false);
            label_meal.setVisible(false);
            label_meal_qty.setVisible(false);
            text_checkoutmealprice.setVisible(false);
        }
    }
    
    // confirm quantities before placing order
    @FXML
    private void handleConfirmQuantities() {
        try {
            int burritoQty = Integer.parseInt(input_burrito_qty.getText());
            int friesQty = Integer.parseInt(input_fries_qty.getText());
            int sodaQty = Integer.parseInt(input_soda_qty.getText());
            int mealQty = isVipUser ? Integer.parseInt(input_meal_qty.getText()) : 0;

            burrito_qty.setText(String.valueOf(burritoQty));
            fries_qty.setText(String.valueOf(friesQty));
            soda_qty.setText(String.valueOf(sodaQty));
            meal_qty.setText(String.valueOf(mealQty));

            double burritoTotal = burritoQty * burritoPrice;
            double friesTotal = friesQty * friesPrice;
            double sodaTotal = sodaQty * sodaPrice;
            double mealTotal = mealQty * mealPrice;

            text_burritocheckoutprice.setText(String.format("$%.2f", burritoTotal));
            text_friescheckoutprice.setText(String.format("$%.2f", friesTotal));
            text_sodacheckoutprice.setText(String.format("$%.2f", sodaTotal));
            text_checkoutmealprice.setText(String.format("$%.2f", mealTotal));

            double total = burritoTotal + friesTotal + sodaTotal + (isVipUser ? mealTotal : 0);
            text_totalprice.setText(String.format("$%.2f", total));

            int preparationTime = Burrito.getPreparationTime(burritoQty) + Fries.getPreparationTime(friesQty);
            text_totalpreparationtime.setText(String.format("%d minutes", preparationTime));
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid numbers for all items.");
            alert.showAndWait();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handlePlaceOrder() throws SQLException {
        String cardNumber = input_cardnumber.getText();
        String cardCvv = input_cardcvv.getText();
        LocalDate cardExpiry = input_cardexpiry.getValue();

        String validationError = validateCreditCard(cardNumber, cardCvv, cardExpiry);
        if (validationError != null) {
            showErrorAlert(validationError, validationError);
            return;
        }

        int burritoQty = Integer.parseInt(input_burrito_qty.getText());
        int friesQty = Integer.parseInt(input_fries_qty.getText());
        int sodaQty = Integer.parseInt(input_soda_qty.getText());
        int mealQty = isVipUser ? Integer.parseInt(input_meal_qty.getText()) : 0;

        double totalPrice = calculateTotalPrice(burritoQty, friesQty, sodaQty, mealQty);
        int preparationTime = calculatePreparationTime(burritoQty, friesQty);

        int userId = getCurrentUserId();

        boolean stored = DatabaseUtils.storeOrder(userId, burritoQty, friesQty, sodaQty, mealQty, totalPrice, preparationTime, this);
        if (stored) {
            showInfoAlert("Order Placed", "Your order has been placed successfully!");
        } else {
            showInfoAlert("Order Error", "Failed to place the order. Please try again later.");
        }
    }

    public void onOrderStored(int orderId) {
        showInfoAlert("Order Created", "Your order number is: " + orderId);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoggedIn.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) button_placeorder.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double calculateTotalPrice(int burritoQty, int friesQty, int sodaQty, int mealQty) {
        return (burritoQty * burritoPrice) + (friesQty * friesPrice) + (sodaQty * sodaPrice) + (mealQty * mealPrice);
    }

    private int calculatePreparationTime(int burritoQty, int friesQty) {
        return Burrito.getPreparationTime(burritoQty) + Fries.getPreparationTime(friesQty);
    }

    private int getCurrentUserId() {
        return 1; 
    }

    private String validateCreditCard(String cardNumber, String cvv, LocalDate cardExpiry) {
        if (cardNumber.length() != 16) {
            return "Invalid card number. Please enter a 16-digit card number.";
        }
        if (cardExpiry == null || cardExpiry.isBefore(LocalDate.now())) {
            return "Card is expired. Please use a valid card.";
        }
        if (cvv.length() != 3) {
            return "Invalid CVV. Please enter a 3-digit CVV.";
        }
        return null;
    }

    // vip for later
    private boolean checkIfUserIsVip() {
        return false;

    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Stage stage = (Stage) button_logout.getScene().getWindow();
        stage.close();
    }

    // view home
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

    // view profile
    @FXML
    private void handleViewProfile(ActionEvent event) {
        try {
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