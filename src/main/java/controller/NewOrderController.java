package controller;

import dao.OrderDao;
import dao.OrderDaoImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Order;
import model.User;
import restaurant.Burrito;
import restaurant.Fries;
import utils.SceneChanger;
import utils.UserSession;

import java.sql.SQLException;
import java.time.LocalDate;

public class NewOrderController {

	// buttons
	
    @FXML
    private Button button_logout;
    @FXML
    private Button button_home;
    @FXML
    private Button button_vieworders;
    @FXML
    private Button button_placeorder;
    @FXML
    private Button button_confirm_qtys;

    // quantity fields
    @FXML
    private TextField input_burrito_qty;
    @FXML
    private TextField input_fries_qty;
    @FXML
    private TextField input_soda_qty;
    @FXML
    private TextField input_meal_qty;

    // labels
    
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

    
    // card number inputs
    @FXML
    private TextField input_cardnumber;
    @FXML
    private TextField input_cardcvv;
    @FXML
    private DatePicker input_cardexpiry;
    
    // order details text display 
    @FXML
    private Text text_burritocheckoutprice;
    @FXML
    private Text text_friescheckoutprice;
    @FXML
    private Text text_sodacheckoutprice;
    @FXML
    private Text text_checkoutmealprice;
    @FXML
    private Text text_totalpreparationtime;
    
    // labels for vip meals view
    @FXML
    private Button label_meal;
    @FXML
    private Label label_meal_qty;

    private final double burritoPrice = 7.0;
    private final double friesPrice = 4.0;
    private final double sodaPrice = 2.5;
    private final double mealPrice = 10.50;

    private boolean isVipUser;
    private final OrderDao orderDao = new OrderDaoImpl();

    @FXML
    public void initialize() {
        button_confirm_qtys.setOnAction(event -> handleConfirmQuantities());
        button_placeorder.setOnAction(event -> {
            try {
                handlePlaceOrder(event);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        User loggedInUser = UserSession.getLoggedInUser();
        if (loggedInUser != null) {
            isVipUser = loggedInUser.isVip();
        }

        if (!isVipUser) {
            input_meal_qty.setVisible(false);
            meal_qty.setVisible(false);
            label_meal.setVisible(false);
            label_meal_qty.setVisible(false);
            text_checkoutmealprice.setVisible(false);
        }
    }

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

            int preparationTime = calculatePreparationTime(burritoQty, friesQty, sodaQty);
            text_totalpreparationtime.setText(String.format("%d minutes", preparationTime));
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Quantities", "Please enter valid numbers for all items.");
        }
    }

    @FXML
    private void handlePlaceOrder(ActionEvent event) throws SQLException {
        String cardNumber = input_cardnumber.getText();
        String cardCvv = input_cardcvv.getText();
        LocalDate cardExpiry = input_cardexpiry.getValue();

        String validationError = validateCreditCard(cardNumber, cardCvv, cardExpiry);
        if (validationError != null) {
            showErrorAlert("Payment Error", validationError);
            return;
        }

        int burritoQty = Integer.parseInt(input_burrito_qty.getText());
        int friesQty = Integer.parseInt(input_fries_qty.getText());
        int sodaQty = Integer.parseInt(input_soda_qty.getText());
        int mealQty = isVipUser ? Integer.parseInt(input_meal_qty.getText()) : 0;

        double totalPrice = calculateTotalPrice(burritoQty, friesQty, sodaQty, mealQty);
        int preparationTime = calculatePreparationTime(burritoQty, friesQty, sodaQty);

        User loggedInUser = UserSession.getLoggedInUser();
        int userId = loggedInUser != null ? loggedInUser.getUserId() : -1;

        if (userId == -1) {
            showErrorAlert("Order Error", "User information is missing. Please log in again.");
            return;
        }

        Order order = new Order(userId, burritoQty, friesQty, sodaQty, mealQty, totalPrice, preparationTime);

        // Store order and get the order_id
        int orderId = orderDao.storeOrder(order);
        if (orderId > 0) { 
            showInfoAlert("Order Placed", "Your order has been placed successfully!\nOrder Number: " + orderId);
            // Redirect to LoggedIn.fxml
            SceneChanger.changeScene(event, "/view/LoggedIn.fxml", "Welcome", 1200, 800, controller -> {
                if (controller instanceof HomeController) {
                    HomeController loggedInController = (HomeController) controller;
                    User currentUser = UserSession.getLoggedInUser();
                    if (currentUser != null) {
                        loggedInController.setUserInformation(currentUser.getFirstname(), currentUser.getLastname());
                    }
                }
            });
        } else {
            showErrorAlert("Order Error", "Failed to place the order. Please try again later.");
        }
    }

    private double calculateTotalPrice(int burritoQty, int friesQty, int sodaQty, int mealQty) {
        return (burritoQty * burritoPrice) + (friesQty * friesPrice) + (sodaQty * sodaPrice) + (mealQty * mealPrice);
    }

    private int calculatePreparationTime(int burritoQty, int friesQty, int sodaQty) {
        int burritoPrepTime = Burrito.getPreparationTime(burritoQty);
        int friesPrepTime = Fries.getPreparationTime(friesQty);


        return burritoPrepTime + friesPrepTime;
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
    private void handleViewProfile(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/Profile.fxml", "Profile", 1200, 800);
    }
    
    @FXML
    private void handleViewOrders(ActionEvent event) {
        SceneChanger.changeScene(event, "/view/OrderHistory.fxml", "Order History", 1200, 800);
    }
    
}
