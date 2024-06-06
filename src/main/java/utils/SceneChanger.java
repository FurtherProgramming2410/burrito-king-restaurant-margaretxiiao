package utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class SceneChanger {

    // Method to change scene using ActionEvent
    public static void changeScene(ActionEvent event, String fxmlFile, String title, double width, double height) {
        try {
            Parent root = FXMLLoader.load(SceneChanger.class.getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Overloaded method to change scene using Stage directly
    public static void changeScene(Stage stage, String fxmlFile, String title, double width, double height) {
        try {
            Parent root = FXMLLoader.load(SceneChanger.class.getResource(fxmlFile));
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to change scene with controller customization
    public static <T> void changeScene(ActionEvent event, String fxmlFile, String title, double width, double height, Consumer<T> controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneChanger.class.getResource(fxmlFile));
            Parent root = loader.load();

            T controller = loader.getController();
            controllerConsumer.accept(controller);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
