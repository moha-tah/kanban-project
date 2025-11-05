package src.main.java.com.kanban;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Classe principale de l'application Kanban
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Kanban Project");

        Label label = new Label("Bienvenue dans l'application Kanban !");
        StackPane src.main.java = new StackPane();
        src.main.java.getChildren().add(label);

        Scene scene = new Scene(src.main.java, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

