package banking;

import banking.dao.DBUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
/**
 * Main entry point for the Banking System application.
 * 
 * @author Banking System
 */
public class Main extends Application {
    
    /**
     * Starts the JavaFX application by displaying the login view.
     * 
     * @param stage The primary stage for the application
     */
    @Override
    public void start(Stage stage) {
        try {
            // Initialize database
            DBUtil.initDatabase();

            // Load login FXML
            URL fxmlLocation = getClass().getResource("/banking/view/login.fxml");
            if (fxmlLocation == null) {
                throw new IOException("Cannot find FXML file /banking/view/login.fxml");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            // Load stylesheet
            URL cssLocation = getClass().getResource("/banking/view/styles.css");
            if (cssLocation == null) {
                throw new IOException("Cannot find CSS file /banking/view/styles.css");
            }
            scene.getStylesheets().add(cssLocation.toExternalForm());

            stage.setTitle("Banking System - Login");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Application Error");
            alert.setHeaderText("Failed to start the application.");
            alert.setContentText("Error: " + e.getMessage() + "\n\nPlease check the console for details.");
            alert.setResizable(true);
            alert.getDialogPane().setPrefWidth(500);
            alert.showAndWait();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        // Close database connections on application exit
        DBUtil.closeDataSource();
    }

    /**
     * Main method to launch the application.
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
