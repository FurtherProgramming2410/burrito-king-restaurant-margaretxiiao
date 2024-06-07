package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.User;

public class UserDaoImpl implements UserDao {
    private final String TABLE_NAME = "users";

    public UserDaoImpl() {
    }

    // make the table
    
    @Override
    public void setup() throws SQLException {
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (username VARCHAR(10) NOT NULL, " +
                         "firstname VARCHAR(50), lastname VARCHAR(50), password VARCHAR(8) NOT NULL, PRIMARY KEY (username))";
            stmt.executeUpdate(sql);
        }
    }

    @Override
    public User getUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ? AND password = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),   
                        rs.getString("firstname"),  
                        rs.getString("lastname"),  
                        rs.getString("password"),   
                        rs.getBoolean("isVIP")      
                    );
                }
                return null;
            }
        }
    }

    // create user in database
    @Override
    public boolean createUser(String username, String firstname, String lastname, String password) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (username, firstname, lastname, password) VALUES (?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, firstname);
            stmt.setString(3, lastname);
            stmt.setString(4, password);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        }
    }
    
    // update user in database

    @Override
    public boolean updateUser(String username, String updatedFirstname, String updatedLastname, String updatedPassword) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET firstname = ?, lastname = ?, password = ? WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, updatedFirstname);
            stmt.setString(2, updatedLastname);
            stmt.setString(3, updatedPassword);
            stmt.setString(4, username);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }
    
    // get the user
    @Override
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),    
                        rs.getString("username"), 
                        rs.getString("firstname"),  
                        rs.getString("lastname"),   
                        rs.getString("password"),   
                        rs.getBoolean("isVIP")      
                    );
                }
                return null;
            }
        }
    }
    
    @Override
    public void upgradeToVIP(int userId) throws SQLException {
        String sql = "UPDATE users SET isVIP = ? WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, true); // isVIP to true
            stmt.setInt(2, userId);

            stmt.executeUpdate();
        }
    }
}
