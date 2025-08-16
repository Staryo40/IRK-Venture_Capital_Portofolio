package irk.staryo;

import com.sun.scenario.effect.impl.prism.PrImage;
import irk.staryo.model.Startup;
import irk.staryo.ui.common.MainContent;
import irk.staryo.ui.common.TopBar;
import irk.staryo.utils.DatabaseLoader;
import irk.staryo.utils.Repository;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        DatabaseLoader.load();
        for (Startup su : Repository.getInstance().getStartupList()){
            System.out.println(su);
        }

        InputStream inputStream = getClass().getResourceAsStream("/images/VCPLogo.png");
        Image icon = new Image(inputStream);
        primaryStage.getIcons().add(icon);

        HBox topBar  = TopBar.getTopBar(primaryStage);
        HBox mainContent = MainContent.getMainContent(primaryStage);
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(mainContent);

        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        Scene scene = new Scene(root, screenWidth * 0.8, screenHeight * 0.8);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setMaximized(true);
        primaryStage.setTitle("VCP Builder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}