package irk.staryo;

import com.sun.scenario.effect.impl.prism.PrImage;
import irk.staryo.model.*;
import irk.staryo.ui.common.MainContent;
import irk.staryo.ui.common.TopBar;
import irk.staryo.ui.deal_flow.DealFlow;
import irk.staryo.utils.*;
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
import java.util.Map;

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

        Startup s1 = new Startup();
        s1.setName("1");
        s1.setTicketSize(2);
        s1.setSector("A");
        Startup s2 = new Startup();
        s2.setName("2");
        s2.setTicketSize(6);
        s2.setSector("A");
        Startup s3 = new Startup();
        s3.setName("3");
        s3.setTicketSize(7);
        s3.setSector("B");
        Startup s4 = new Startup();
        s4.setName("4");
        s4.setTicketSize(2);
        s4.setSector("B");
        Startup s5 = new Startup();
        s5.setName("5");
        s5.setTicketSize(5);
        s5.setSector("C");
        Startup s6 = new Startup();
        s6.setName("6");
        s6.setTicketSize(3);
        s6.setSector("C");

        List<Startup> check = new ArrayList<>(List.of(s1, s2, s3, s4, s5, s6));
        try{
            Map<Startup, Integer> mapping = StartupCombination.generateStartupIndex(check);
            List<String> mask = StartupCombination.generateCombinationBitmasks(check, 10);

            for (Map.Entry<Startup, Integer> item : mapping.entrySet()) {
                System.out.println("Startup: " + item.getKey().getName() + ", Index: " + item.getValue());
            }
            System.out.println(mask);
        } catch (Exception e){
            e.printStackTrace();
        }


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