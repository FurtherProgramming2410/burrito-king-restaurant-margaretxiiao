package model;

import java.sql.Timestamp;

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
    private boolean readyForCollection;
	private boolean orderStatus;


    public Order(int userId, int burritoQty, int friesQty, int sodaQty, int mealQty, double totalPrice, int preparationTime) {
        this.setUserId(userId);
        this.setBurritoQty(burritoQty);
        this.setFriesQty(friesQty);
        this.setSodaQty(sodaQty);
        this.setMealQty(mealQty);
        this.setTotalPrice(totalPrice);
        this.setPreparationTime(preparationTime);
        this.setOrderTime(new Timestamp(System.currentTimeMillis()));
        this.setOrderStatus(false);
    }


	public boolean isReadyForCollection() {
		return readyForCollection;
	}


	public void setReadyForCollection(boolean readyForCollection) {
		this.readyForCollection = readyForCollection;
	}


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


	public boolean isOrderStatus() {
		return orderStatus;
	}


	public void setOrderStatus(boolean orderStatus) {
		this.orderStatus = orderStatus;
	}


}
