package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Order;

public class OrderDaoImpl implements OrderDao {

    @Override
    public List<Order> getOrdersByUserId(int userId) throws SQLException {
        List<Order> orderHistory = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_time DESC";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("order_id"), // Assuming order_id is the primary key
                        rs.getInt("user_id"),
                        rs.getInt("burrito_qty"),
                        rs.getInt("fries_qty"),
                        rs.getInt("soda_qty"),
                        rs.getInt("meal_qty"),
                        rs.getDouble("total_price"),
                        rs.getInt("preparation_time"),
                        rs.getString("order_status"),
                        rs.getTimestamp("order_time")
                    );
                    orderHistory.add(order);
                }
            }
        }
        return orderHistory;
    }

    @Override
    public int storeOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (user_id, burrito_qty, fries_qty, soda_qty, meal_qty, total_price, preparation_time, order_time, order_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getUserId());
            stmt.setInt(2, order.getBurritoQty());
            stmt.setInt(3, order.getFriesQty());
            stmt.setInt(4, order.getSodaQty());
            stmt.setInt(5, order.getMealQty());
            stmt.setDouble(6, order.getTotalPrice());
            stmt.setInt(7, order.getPreparationTime());
            stmt.setTimestamp(8, order.getOrderTime());
            stmt.setString(9, order.getOrderStatus());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating order failed.");
                }
            }
        }
    }

    @Override
    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Order> getActiveOrdersByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE user_id = ? AND order_status = 'placed'";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (resultSet.next()) {
                Order order = new Order(
                        resultSet.getInt("order_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("burrito_qty"),
                        resultSet.getInt("fries_qty"),
                        resultSet.getInt("soda_qty"),
                        resultSet.getInt("meal_qty"),
                        resultSet.getDouble("total_price"),
                        resultSet.getInt("preparation_time"),
                        resultSet.getString("order_status"),
                        resultSet.getTimestamp("order_time")
                );
                orders.add(order);
            }
            return orders;
        }
    }
}
