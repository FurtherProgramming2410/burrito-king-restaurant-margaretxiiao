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

    public Order(int userId, int burritoQty, int friesQty, int sodaQty, int mealQty, double totalPrice, int preparationTime) {
        this.userId = userId;
        this.burritoQty = burritoQty;
        this.friesQty = friesQty;
        this.sodaQty = sodaQty;
        this.mealQty = mealQty;
        this.totalPrice = totalPrice;
        this.preparationTime = preparationTime;
        this.orderTime = new Timestamp(System.currentTimeMillis());
        this.orderStatus = "placed";  // default
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
    
    public String getFormattedOrderTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        return sdf.format(orderTime);
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
