package dao;

import java.sql.SQLException;
import model.User;

public interface UserDao {
    void setup() throws SQLException;
    User getUser(String username, String password) throws SQLException;
    boolean createUser(String username, String firstname, String lastname, String password) throws SQLException;
    boolean updateUser(String username, String updatedFirstname, String updatedLastname, String updatedPassword) throws SQLException;
    User getUserByUsername(String username) throws SQLException;
}
