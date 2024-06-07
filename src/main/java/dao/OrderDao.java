package dao;

import model.Order;
import java.sql.SQLException;
import java.util.List;

public interface OrderDao {
    List<Order> getOrdersByUserId(int userId) throws SQLException;
    int storeOrder(Order order) throws SQLException;
    boolean updateOrderStatus(int orderId, String status) throws SQLException;
	List<Order> getActiveOrdersByUserId(int userId) throws SQLException;
    void upgradeToVIP(int userId, String email) throws SQLException;
	void updateUserCredits(int userId, int userCredits) throws SQLException;
}