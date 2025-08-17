package irk.staryo;

import com.sun.scenario.effect.impl.prism.PrImage;
import irk.staryo.model.*;
import irk.staryo.ui.common.MainContent;
import irk.staryo.ui.common.TopBar;
import irk.staryo.ui.deal_flow.DealFlow;
import irk.staryo.utils.ConvolutionCalculator;
import irk.staryo.utils.DatabaseLoader;
import irk.staryo.utils.PmfCalculator;
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

import javax.sound.sampled.Line;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // -------- SETUP --------
        DatabaseLoader.load();
        DealFlow.startups = Repository.getInstance().getSortedStartups();
//        for (Startup su : Repository.getInstance().getStartupList()){
//            System.out.println(su);
//        }
//        System.out.println("Map length: " + Repository.getInstance().getSectorColor().size());



        // -------- ICON --------
        InputStream inputStream = getClass().getResourceAsStream("/images/VCPLogo.png");
        Image icon = new Image(inputStream);
        primaryStage.getIcons().add(icon);

        // -------- Setting content --------
        HBox topBar  = TopBar.getTopBar(primaryStage);
        HBox mainContent = MainContent.getMainContent(primaryStage);
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(mainContent);

        // -------- Screen Configuration --------
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