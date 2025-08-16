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
        btnView1.getStyleClass().add("main-button-on");

        btnView2.setPadding(new Insets(8));
        btnView2.getStyleClass().add("main-button-off");

        btnView1.setMaxWidth(Double.MAX_VALUE);
        btnView2.setMaxWidth(Double.MAX_VALUE);


        leftPanel.getChildren().addAll(btnView1, btnView2);

        // Right Panel Sections
        VBox view1 = new VBox(new Label("This is View 1"));
        VBox view2 = new VBox(new Label("This is View 2"));
        rightPanel.getChildren().add(view1);

        // Button Styling
        Button[] buttons = {btnView1, btnView2};

        // Button Action
        btnView1.setOnAction(e -> {
            rightPanel.getChildren().setAll(view1);

            for (Button btn : buttons) {
                btn.getStyleClass().removeAll("main-button-on", "main-button-off");
                if (btn == btnView1) {
                    btn.getStyleClass().add("main-button-on");
                } else {
                    btn.getStyleClass().add("main-button-off");
                }
            }
        });

        btnView2.setOnAction(e -> {
            rightPanel.getChildren().setAll(view2);

            for (Button btn : buttons) {
                btn.getStyleClass().removeAll("main-button-on", "main-button-off");
                if (btn == btnView2) {
                    btn.getStyleClass().add("main-button-on");
                } else {
                    btn.getStyleClass().add("main-button-off");
                }
            }
        });

        mainContent.getChildren().addAll(leftPanel, rightPanel);

        return mainContent;
    }
}
