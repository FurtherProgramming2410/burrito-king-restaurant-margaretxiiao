package dao;

import model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderDaoImpl implements OrderDao {
    @Override
    public int storeOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (user_id, burrito_qty, fries_qty, soda_qty, meal_qty, total_price, preparation_time, order_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getUserId());
            stmt.setInt(2, order.getBurritoQty());
            stmt.setInt(3, order.getFriesQty());
            stmt.setInt(4, order.getSodaQty());
            stmt.setInt(5, order.getMealQty());
            stmt.setDouble(6, order.getTotalPrice());
            stmt.setInt(7, order.getPreparationTime());
            stmt.setBoolean(8, order.isOrderStatus());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // return order_id
                    }
                }
            }
            return -1; // for failure
        }
    }
}
