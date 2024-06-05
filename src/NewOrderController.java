import java.time.LocalDate;

import javafilesforlater.Burrito;
import javafilesforlater.Fries;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class NewOrderController {

    // buttons
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

    // order summary texts
    @FXML
    private Text burrito_qty;

    @FXML
    private Text fries_qty;

    @FXML
    private Text soda_qty;

    @FXML
    private Text meal_qty;

    @FXML
    private Text text_totalprice;

    // payment section key inputs
    @FXML
    private TextField input_cardnumber;

    @FXML
    private TextField input_cardcvv;

    @FXML
    private DatePicker input_cardexpiry;
    
    // item specific subtotals before adding to the total price
    @FXML
    private Text text_burritocheckoutprice;
    
    @FXML
    private Text text_friescheckoutprice;
    
    @FXML
    private Text text_sodacheckoutprice;
    
    @FXML
    private Text text_checkoutmealprice;
    
    // preparation time text
    
    @FXML 
    private Text text_totalpreparationtime;

    // Constants for food prices
    private final double burritoPrice = 7.0;
    private final double friesPrice = 4.0;
    private final double sodaPrice = 2.5;
    private final double mealPrice = 10.50;

    @FXML
    public void initialize() {
        button_confirm_qtys.setOnAction(event -> handleConfirmQuantities());
        button_placeorder.setOnAction(event -> handlePlaceOrder());
    }

    @FXML
    private void handleConfirmQuantities() {
        // handle confirmation of quantities
        try {
            // Retrieve quantities from input fields
            int burritoQty = Integer.parseInt(input_burrito_qty.getText());
            int friesQty = Integer.parseInt(input_fries_qty.getText());
            int sodaQty = Integer.parseInt(input_soda_qty.getText());
            int mealQty = Integer.parseInt(input_meal_qty.getText());

            // Update displayed quantities
            burrito_qty.setText(String.valueOf(burritoQty));
            fries_qty.setText(String.valueOf(friesQty));
            soda_qty.setText(String.valueOf(sodaQty));
            meal_qty.setText(String.valueOf(mealQty));

            // Calculate total price for each item
            double burritoTotal = burritoQty * burritoPrice;
            double friesTotal = friesQty * friesPrice;
            double sodaTotal = sodaQty * sodaPrice;
            double mealTotal = mealQty * mealPrice;

            // Update displayed prices
            text_burritocheckoutprice.setText(String.format("$%.2f", burritoTotal));
            text_friescheckoutprice.setText(String.format("$%.2f", friesTotal));
            text_sodacheckoutprice.setText(String.format("$%.2f", sodaTotal));
            text_checkoutmealprice.setText(String.format("$%.2f", mealTotal));

            // Calculate and display total price
            double total = burritoTotal + friesTotal + sodaTotal + mealTotal;
            text_totalprice.setText(String.format("$%.2f", total));

            // Calculate and display total preparation time
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

    @FXML
    private void handlePlaceOrder() {
        // handle placing order
        String cardNumber = input_cardnumber.getText();
        String cardCvv = input_cardcvv.getText();
        String cardExpiry = (input_cardexpiry.getValue() != null) ? input_cardexpiry.getValue().toString() : "";

        // credit card validations
        String validationError = validateCreditCard(cardNumber, cardCvv, cardExpiry);
        if (validationError != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Payment Error");
            alert.setHeaderText(null);
            alert.setContentText(validationError);
            alert.showAndWait();
            return;
        }

        // confirmation message if validation is successful
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Placed");
        alert.setHeaderText(null);
        alert.setContentText("Your order has been placed successfully!");
        alert.showAndWait();
    }

    // Validate credit card information
    private String validateCreditCard(String cardNumber, String cvv, String expiry) {
        // Check if card number is 16 digits
        if (cardNumber.length() != 16) {
            return "Invalid card number. Please enter a 16-digit card number.";
        }
        
        // Check if expiry date is in the future
        LocalDate expiryDate = input_cardexpiry.getValue();
        if (expiryDate == null || expiryDate.isBefore(LocalDate.now())) {
            return "Card is expired. Please use a valid card.";
        }

        // Check if CVV is 3 digits long
        if (cvv.length() != 3) {
            return "Invalid CVV. Please enter a 3-digit CVV.";
        }

        return null;

    }
    
}

