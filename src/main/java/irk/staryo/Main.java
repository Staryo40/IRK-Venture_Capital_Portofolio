package irk.staryo;

import irk.staryo.model.Startup;
import irk.staryo.utils.DatabaseLoader;
import irk.staryo.utils.Repository;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        DatabaseLoader.load();
        for (Startup su : Repository.getInstance().getStartupList()){
            System.out.println(su);
        }

        Label label = new Label("Hello, JavaFX!");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("JavaFX Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}