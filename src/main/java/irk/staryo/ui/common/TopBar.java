package irk.staryo.ui.common;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.InputStream;

public class TopBar {
    public static HBox getTopBar(Stage stage){
        // ====== Top Bar ======
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setStyle("-fx-background-color: #FFFFFF;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Logo
        InputStream inputStream = TopBar.class.getResourceAsStream("/images/VCPLogo.png");
        Image icon = new Image(inputStream);
        ImageView logo = new ImageView(icon);
        logo.setFitWidth(40);
        logo.setFitHeight(40);

        // Text
        Label title = new Label("VCP Builder");
        title.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");

        topBar.getChildren().addAll(logo, title);

        return topBar;
    }
}
