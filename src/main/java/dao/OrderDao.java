package dao;

import java.sql.SQLException;
import model.Order;

public interface OrderDao {
    int storeOrder(Order order) throws SQLException;
}