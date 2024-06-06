package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class OrderHistoryController {

    @FXML
    private Button button_logout;

    @FXML
    private Button button_home;

    @FXML
    private Button button_viewprofile;

    @FXML
    public void initialize() {
        button_logout.setOnAction(this::handleLogout);
        button_home.setOnAction(this::handleHome);
        button_viewprofile.setOnAction(this::handleViewProfile);
    }

    @FXML
    private void handleLogout(ActionEvent event) {

        Stage stage = (Stage) button_logout.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleHome(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoggedIn.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) button_home.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/profile.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
