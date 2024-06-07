package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Order {
    private int id;
    private int userId;
    private int burritoQty;
    private int friesQty;
    private int sodaQty;
    private int mealQty;
    private double totalPrice;
    private int preparationTime;
    private Timestamp orderTime;
    private String orderStatus;

    private double actualPaidAmount;
    private int creditsUsed;

    // order constructor
    
    public Order(int id, int userId, int burritoQty, int friesQty, int sodaQty, int mealQty, double totalPrice, int preparationTime, String orderStatus, Timestamp orderTime) {
        this.id = id;
        this.userId = userId;
        this.burritoQty = burritoQty;
        this.friesQty = friesQty;
        this.sodaQty = sodaQty;
        this.mealQty = mealQty;
        this.totalPrice = totalPrice;
        this.preparationTime = preparationTime;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
    }

    // order constructor for NewOrderController
    public Order(int userId, int burritoQty, int friesQty, int sodaQty, int mealQty, double totalPrice, int preparationTime) {
        this.userId = userId;
        this.burritoQty = burritoQty;
        this.friesQty = friesQty;
        this.sodaQty = sodaQty;
        this.mealQty = mealQty;
        this.totalPrice = totalPrice;
        this.preparationTime = preparationTime;
        this.orderTime = new Timestamp(System.currentTimeMillis()); // set current time
        this.orderStatus = "placed"; // Default status
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBurritoQty() {
        return burritoQty;
    }

    public void setBurritoQty(int burritoQty) {
        this.burritoQty = burritoQty;
    }

    public int getFriesQty() {
        return friesQty;
    }

    public void setFriesQty(int friesQty) {
        this.friesQty = friesQty;
    }

    public int getSodaQty() {
        return sodaQty;
    }

    public void setSodaQty(int sodaQty) {
        this.sodaQty = sodaQty;
    }

    public int getMealQty() {
        return mealQty;
    }

    public void setMealQty(int mealQty) {
        this.mealQty = mealQty;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderedItems() {
        StringBuilder items = new StringBuilder();
        if (burritoQty > 0) items.append(burritoQty).append(" Burrito(s), ");
        if (friesQty > 0) items.append(friesQty).append(" Fries, ");
        if (sodaQty > 0) items.append(sodaQty).append(" Soda(s), ");
        if (mealQty > 0) items.append(mealQty).append(" Meal(s)");
        
        // remove comma and space
        if (items.length() > 0 && items.charAt(items.length() - 2) == ',') {
            items.setLength(items.length() - 2);
        }
        
        return items.toString();
    }
    
    // format order time
    public String getFormattedOrderTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        return sdf.format(orderTime);
    }
    
    // format order price
    public String getFormattedTotalPrice() {
        return String.format("$%.2f", totalPrice);
    }
    
    public double getActualPaidAmount() {
        return actualPaidAmount;
    }

    public void setActualPaidAmount(double actualPaidAmount) {
        this.actualPaidAmount = actualPaidAmount;
    }

    public int getCreditsUsed() {
        return creditsUsed;
    }

    public void setCreditsUsed(int creditsUsed) {
        this.creditsUsed = creditsUsed;
    }
}
