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
import javafx.scene.image.ImageView;
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
    @FXML
    private Button button_applycredits;

    // qty inputs
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
    private Text text_totalprice;
    @FXML
    private Text text_vipdiscountamount;
    @FXML
    private Text text_totalpreparationtime;
    @FXML
    private Text text_burritocheckoutprice;
    @FXML
    private Text text_friescheckoutprice;
    @FXML
    private Text text_sodacheckoutprice;

    // payment inputs
    @FXML
    private TextField input_cardnumber;
    @FXML
    private TextField input_cardcvv;
    @FXML
    private DatePicker input_cardexpiry;

    // vip-specific elements
    @FXML
    private Text text_mealdiscount;
    @FXML
    private Label label_meal_qty;
    @FXML
    private Button label_meal;
    @FXML
    private Text label_redeemcredits;
    @FXML
    private Text label_creditsavailable;
    @FXML
    private Text text_creditsavailable;
    @FXML
    private Text text_appliedcredits;
    @FXML
    private Text label_creditsapplied;
    @FXML
    private TextField input_redeemcredits;
    @FXML
    private ImageView image_meal;

    // constants for food prices
    private final double burritoPrice = 7.0;
    private final double friesPrice = 4.0;
    private final double sodaPrice = 2.5;
    private final double mealDiscount = 3.0;

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

        button_applycredits.setOnAction(event -> handleApplyCredits());

        User loggedInUser = UserSession.getLoggedInUser();
        if (loggedInUser != null) {
            isVipUser = loggedInUser.isVip();
            setVipElementsVisibility(isVipUser);
        }
    }
    
    // for VIP only view

    private void setVipElementsVisibility(boolean isVisible) {
        label_meal.setVisible(isVisible);
        input_meal_qty.setVisible(isVisible);
        label_meal_qty.setVisible(isVisible);
        text_vipdiscountamount.setVisible(isVisible);
        label_redeemcredits.setVisible(isVisible);
        label_creditsavailable.setVisible(isVisible);
        text_creditsavailable.setVisible(isVisible);
        text_appliedcredits.setVisible(isVisible);
        label_creditsapplied.setVisible(isVisible);
        input_redeemcredits.setVisible(isVisible);
        button_applycredits.setVisible(isVisible);
        text_mealdiscount.setVisible(isVisible);
        image_meal.setVisible(isVisible);

        if (isVisible) {
            User loggedInUser = UserSession.getLoggedInUser();
            text_creditsavailable.setText(String.valueOf(loggedInUser.getCredits()));
        }
    }

    @FXML
    private void handleConfirmQuantities() {
        try {
            int burritoQty = Integer.parseInt(input_burrito_qty.getText());
            int friesQty = Integer.parseInt(input_fries_qty.getText());
            int sodaQty = Integer.parseInt(input_soda_qty.getText());
            int mealQty = isVipUser ? Integer.parseInt(input_meal_qty.getText()) : 0;

            // Update quantities
            burritoQty += mealQty;
            friesQty += mealQty;
            sodaQty += mealQty;

            burrito_qty.setText(String.valueOf(burritoQty));
            fries_qty.setText(String.valueOf(friesQty));
            soda_qty.setText(String.valueOf(sodaQty));

            double burritoTotal = burritoQty * burritoPrice;
            double friesTotal = friesQty * friesPrice;
            double sodaTotal = sodaQty * sodaPrice;
            double totalDiscount = mealQty * mealDiscount;

            text_burritocheckoutprice.setText(String.format("$%.2f", burritoTotal));
            text_friescheckoutprice.setText(String.format("$%.2f", friesTotal));
            text_sodacheckoutprice.setText(String.format("$%.2f", sodaTotal));
            text_vipdiscountamount.setText(String.format("-$%.2f", totalDiscount));

            double total = burritoTotal + friesTotal + sodaTotal - totalDiscount;
            text_totalprice.setText(String.format("$%.2f", total));

            int preparationTime = calculatePreparationTime(burritoQty, friesQty, sodaQty);
            text_totalpreparationtime.setText(String.format("%d minutes", preparationTime));
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Quantities", "Please enter valid numbers for all items.");
        }
    }

    @FXML
    private void handleApplyCredits() {
        try {
            int creditsAvailable = Integer.parseInt(text_creditsavailable.getText());
            int creditsToRedeem = Integer.parseInt(input_redeemcredits.getText());

            if (creditsToRedeem <= 0 || creditsToRedeem > creditsAvailable) {
                showErrorAlert("Invalid Credits", "You don't have enough credits or the input is invalid.");
                return;
            }

            // Ensure creditsToRedeem is a multiple of 100
            if (creditsToRedeem % 100 != 0) {
                showErrorAlert("Invalid Credits", "100 credits = $1. Please redeem in whole dollars :)");
                return;
            }

            // Calculate the redeemable credit value in dollars
            double creditValue = creditsToRedeem / 100.0;
            text_appliedcredits.setText(String.format("$%.2f", creditValue));
            int newCreditBalance = creditsAvailable - creditsToRedeem;
            text_creditsavailable.setText(String.valueOf(newCreditBalance));

            double currentTotalPrice = Double.parseDouble(text_totalprice.getText().replace("$", ""));
            currentTotalPrice -= creditValue;
            text_totalprice.setText(String.format("$%.2f", currentTotalPrice));
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Input", "Please enter a valid number of credits.");
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

        int burritoQty = getIntFromTextField(input_burrito_qty, "burrito quantity");
        int friesQty = getIntFromTextField(input_fries_qty, "fries quantity");
        int sodaQty = getIntFromTextField(input_soda_qty, "soda quantity");
        int mealQty = isVipUser ? getIntFromTextField(input_meal_qty, "meal quantity") : 0;

        // Adjust quantities for meals
        burritoQty += mealQty;
        friesQty += mealQty;
        sodaQty += mealQty;

        double totalPrice = calculateTotalPrice(burritoQty, friesQty, sodaQty, mealQty);

        User loggedInUser = UserSession.getLoggedInUser();
        if (loggedInUser != null) {
            int credits = loggedInUser.getCredits();
            int creditsToRedeem = getIntFromTextField(input_redeemcredits, "credits to redeem");
            double discount = creditsToRedeem / 100.0;
            totalPrice -= discount;
            loggedInUser.setCredits(credits - creditsToRedeem);
            orderDao.updateUserCredits(loggedInUser.getUserId(), loggedInUser.getCredits());
        }

        int preparationTime = calculatePreparationTime(burritoQty, friesQty, sodaQty);

        int userId = loggedInUser != null ? loggedInUser.getUserId() : -1;

        if (userId == -1) {
            showErrorAlert("Order Error", "User information is missing. Please log in again.");
            return;
        }

        Order order = new Order(userId, burritoQty, friesQty, sodaQty, 0, totalPrice, preparationTime);

        // store order and get the order_id
        int orderId = orderDao.storeOrder(order);
        if (orderId > 0) {
            // Add credits to user's account only if VIP
            if (isVipUser) {
                int creditsEarned = addCreditsToUser(totalPrice);
                showInfoAlert("You've Earned Credits!", "Earned credits from Order Number " + orderId + ": " + creditsEarned);
            }

            // show order placed info
            showInfoAlert("Order Placed", "Your order has been placed successfully!\nOrder Number: " + orderId);

            // redirect to Home.fxml
            SceneChanger.changeScene(event, "/view/Home.fxml", "Welcome", 1200, 800, controller -> {
                if (controller instanceof HomeController) {
                    HomeController homeController = (HomeController) controller;
                    User currentUser = UserSession.getLoggedInUser();
                    if (currentUser != null) {
                        homeController.setUserInformation(currentUser.getFirstname(), currentUser.getLastname());
                    }
                }
            });
        } else {
            showErrorAlert("Order Error", "Failed to place the order. Please try again later.");
        }
    }

    private int getIntFromTextField(TextField textField, String fieldName) {
        try {
            String text = textField.getText().trim();
            if (text.isEmpty()) {
                return 0;  // Default to 0 if empty
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Input", "Please enter a valid number for " + fieldName + ".");
            throw e;
        }
    }

    private double calculateTotalPrice(int burritoQty, int friesQty, int sodaQty, int mealQty) {
        double burritoTotal = burritoQty * burritoPrice;
        double friesTotal = friesQty * friesPrice;
        double sodaTotal = sodaQty * sodaPrice;
        double totalDiscount = mealQty * mealDiscount;
        return burritoTotal + friesTotal + sodaTotal - totalDiscount;
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

    private int addCreditsToUser(double amountPaid) {
        int creditsEarned = (int) amountPaid;
        User loggedInUser = UserSession.getLoggedInUser();
        if (loggedInUser != null) {
            int userCredits = loggedInUser.getCredits() + creditsEarned;
            loggedInUser.setCredits(userCredits);
            try {
                orderDao.updateUserCredits(loggedInUser.getUserId(), userCredits);
            } catch (SQLException e) {
                showErrorAlert("Database Error", "Failed to update user credits.");
                e.printStackTrace();
            }
        }
        return creditsEarned;
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
