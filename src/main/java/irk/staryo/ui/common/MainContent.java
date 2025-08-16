package irk.staryo.ui.common;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainContent {
    public static HBox getMainContent(Stage stage){
        // ====== Main Content ======
        HBox mainContent = new HBox();
        mainContent.setPadding(new Insets(10));
        mainContent.setSpacing(10);

        VBox leftPanel = new VBox(10);
        leftPanel.setStyle("-fx-background-color: #FFFFFF;");
        leftPanel.setPadding(new Insets(10));

        VBox rightPanel = new VBox();
        rightPanel.setStyle("-fx-background-color: #FFFFFF;");

        mainContent.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            leftPanel.setPrefWidth(width * 0.2);
            rightPanel.setPrefWidth(width * 0.8);
        });

        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // Left Panel Buttons
        Button btnView1 = new Button("Deal Flow");
        Button btnView2 = new Button("Portofolio Construction");

        btnView1.setPadding(new Insets(8));
        btnView1.setStyle("-fx-background-color: #FF7B00; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");

        btnView2.setPadding(new Insets(8));
        btnView2.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");

        btnView1.setMaxWidth(Double.MAX_VALUE);
        btnView2.setMaxWidth(Double.MAX_VALUE);


        leftPanel.getChildren().addAll(btnView1, btnView2);

        // Right Panel Sections
        VBox view1 = new VBox(new Label("This is View 1"));
        VBox view2 = new VBox(new Label("This is View 2"));
        rightPanel.getChildren().add(view1);

        // Button Styling
        Button[] buttons = {btnView1, btnView2};
        Button[] views = {btnView1, btnView2}; // dummy array, just for logic below
        Runnable highlight = () -> {
            for (Button btn : buttons) {
                btn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            }
        };

        // Button Action
        btnView1.setOnAction(e -> {
            rightPanel.getChildren().setAll(view1);
            highlight.run();
            btnView1.setStyle("-fx-background-color: #FF7B00; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        });

        btnView2.setOnAction(e -> {
            rightPanel.getChildren().setAll(view2);
            highlight.run();
            btnView2.setStyle("-fx-background-color: #FF7B00; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        });

        mainContent.getChildren().addAll(leftPanel, rightPanel);

        return mainContent;
    }
}
