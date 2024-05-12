import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
 
public class Main extends Application {
	
    public static void main(String[] args) {
        launch(args);
    }	   
    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
	    Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
	    primaryStage.setTitle("Burrito King");
	    primaryStage.setScene(new Scene(root, 700, 500));
	    primaryStage.show();
	    
    	
    	}
}
    	


   
