package irk.staryo.ui.deal_flow;

import irk.staryo.model.Startup;
import irk.staryo.utils.Visuals;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StartupItem {
    public static Button buildStartupButton(Startup s, Color accent){
        Button card = new Button();
        card.setPrefWidth(310);
        card.setWrapText(true);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: #CCCCCC;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 10;" +
                        "-fx-cursor: hand;"
        );

        HBox layout = new HBox(10);

        // Accent stripe
        Rectangle stripe = new Rectangle(5, 80, accent);
        stripe.heightProperty().bind(
                Bindings.createDoubleBinding(
                        () -> layout.getHeight(),
                        layout.heightProperty()
                )
        );

        // Info
        VBox info = new VBox(10);
        VBox general = new VBox();
        Label name = new Label(s.getName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label desc = new Label(s.getDescription());
        desc.setWrapText(true);
        general.getChildren().addAll(name, desc);

        GridPane details = new GridPane();
        details.setHgap(10);
        details.setVgap(5);

        Label fundingTitle = new Label("Funding:");
        Label ticketTitle = new Label("Ticket Size:");
        Label locationTitle = new Label("Location:");
        Label foundedTitle = new Label("Founded:");
        fundingTitle.setStyle("-fx-text-fill: #888888");
        ticketTitle.setStyle("-fx-text-fill: #888888");
        locationTitle.setStyle("-fx-text-fill: #888888");
        foundedTitle.setStyle("-fx-text-fill: #888888");

        Label fundingStage = new Label(s.getFundingStage());
        fundingStage.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Visuals.toHexString(accent));
        Label ticketSize = new Label("$" + s.getTicketSize() + "M");
        ticketSize.setStyle("-fx-font-weight: bold;");

        details.addRow(0, fundingTitle, fundingStage);
        details.addRow(1, ticketTitle, ticketSize);
        details.addRow(2, locationTitle, new Label(s.getLocation()));
        details.addRow(3, foundedTitle, new Label(String.valueOf(s.getFoundYear())));

        info.getChildren().addAll(general, details);

        layout.getChildren().addAll(stripe, info);
        card.setGraphic(layout);

        return card;

    }
}
