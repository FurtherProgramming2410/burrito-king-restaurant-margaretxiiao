import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtils {
	
    private static final String DB_URL = "jdbc:mysql://localhost:3306/javafx-burritoking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
	
	
    public static void updateProfile(String username, String updatedFirstname, String updatedLastname, String updatedPassword) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "UPDATE users SET firstname=?, lastname=?, password=? WHERE username=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, updatedFirstname);
            preparedStatement.setString(2, updatedLastname);
            preparedStatement.setString(3, updatedPassword);
            preparedStatement.setString(4, username);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Profile updated successfully.");
            } else {
                System.out.println("No profile updated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username, String firstname, String lastname, double width, double height){
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(DatabaseUtils.class.getResource(fxmlFile));
            root = loader.load();
           

            if (firstname != null && lastname != null) {
                LoggedInController loggedInController = loader.getController();
                loggedInController.setUserInformation(firstname, lastname);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, width, height));
        stage.show();
    }
    
    
    

    // Updated signUpUser method
    public static void signUpUser(ActionEvent event, String username, String firstname, String lastname, String password) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javafx-burritoking", "root", "root");
            psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            if (resultSet.next()) {
                System.out.println("User already exists");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username.");
                alert.show();
            } else {
                psInsert = connection.prepareStatement("INSERT INTO users (username, firstname, lastname, password) VALUES (?, ?, ?, ?)");
                psInsert.setString(1, username);
                psInsert.setString(2, firstname);
                psInsert.setString(3, lastname);
                psInsert.setString(4, password);
                psInsert.executeUpdate(); // Use executeUpdate instead of execute for INSERT statements

                // Call changeScene method from DatabaseUtils class with the correct number of arguments
                DatabaseUtils.changeScene(event, "/view/loggedIn.fxml", "Welcome", username, null, null, 1200.0, 800.0);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (psCheckUserExists != null) psCheckUserExists.close();
                if (psInsert != null) psInsert.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    public static void LogInUser(ActionEvent event, String username, String password) {
        if (authenticateUser(username, password)) {
            // Get user's first and last name from the database
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            String retrievedFirstName = null;
            String retrievedLastName = null;

            try {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                preparedStatement = connection.prepareStatement("SELECT firstname, lastname FROM users WHERE username = ?");
                preparedStatement.setString(1, username);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    retrievedFirstName = resultSet.getString("firstname");
                    retrievedLastName = resultSet.getString("lastname");
                }

                DatabaseUtils.changeScene(event, "/view/loggedIn.fxml", "Welcome", username, null, null, 1200.0, 800.0);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close resources
                try {
                    if (resultSet != null) resultSet.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Authentication failed");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Provided credentials are incorrect!");
            alert.show();
        }
    }

    public static boolean authenticateUser(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            return resultSet.next(); // Returns true if there's at least one row with the given username and password
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String[] getUserInfo(String username) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT firstname, lastname FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                return new String[]{firstname, lastname};
            } else {
                System.out.println("User not found in the database for username: " + username);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching user info from database: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Error while closing database resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


}