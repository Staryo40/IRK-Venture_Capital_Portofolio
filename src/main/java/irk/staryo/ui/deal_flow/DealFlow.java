package irk.staryo.ui.deal_flow;

import irk.staryo.model.Startup;
import irk.staryo.utils.Repository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.*;
import java.util.stream.Collectors;

public class DealFlow {
    public static Map<String, List<Startup>> startups = new HashMap<>();;
    public static Map<String, List<Startup>> shownStartups = new HashMap<>();

    public static BorderPane getDealFlow(Stage stage){
        BorderPane dealFlow = new BorderPane();

        // -------------------- TOP BAR --------------------
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(20);
        topBar.setStyle("-fx-background-color: #FFFFFF;");

        // --- Left Text ---
        VBox leftText = new VBox();
        leftText.setAlignment(Pos.CENTER_LEFT);

        Text header = new Text("Deal Flow");
        header.setFont(Font.font("Arial", 24));

        Text subText = new Text("Comprehensive database of all startups organized by sectors");
        subText.setFont(Font.font("Arial", 14));

        leftText.getChildren().addAll(header, subText);

        // --- Right Controls ---
        HBox rightControls = new HBox();
        rightControls.setAlignment(Pos.CENTER_RIGHT);
        rightControls.setSpacing(10);

        TextField searchBar = new TextField();
        searchBar.setPromptText("Search...");
        searchBar.setPrefHeight(40);
        searchBar.setPrefWidth(200);

        Button filterButton = new Button("Filter");
        filterButton.setGraphic(new FontIcon("mdi-filter"));

        rightControls.getChildren().addAll(searchBar, filterButton);

        HBox.setHgrow(leftText, Priority.ALWAYS);
        topBar.getChildren().addAll(leftText, rightControls);

        // -------------------- MAIN CONTENT --------------------
        ScrollPane mainContent = new ScrollPane();
        HBox content = new HBox(20);
        content.setStyle("-fx-background-color: #FFFFFF;");
        content.setPadding(new Insets(10));

        mainContent.setContent(content);
        mainContent.setStyle(
                "-fx-background: #FFFFFF;" +
                        "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: transparent;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );

        mainContent.setPannable(false);
        mainContent.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainContent.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainContent.setFitToWidth(false);
        mainContent.setFitToHeight(false);

        dealFlow.setTop(topBar);
        dealFlow.setCenter(mainContent);

        // -------------------- FILTER --------------------
        ContextMenu filterMenu = new ContextMenu();
        List<CheckBox> sectorCheckboxes = new ArrayList<>();

        for (String sector : startups.keySet()) {
            CheckBox cb = new CheckBox(sector);
            cb.setSelected(true);
            CustomMenuItem item = new CustomMenuItem(cb);
            item.setHideOnClick(false);
            filterMenu.getItems().add(item);
            sectorCheckboxes.add(cb);
        }

        // "Show All" button
        Button showAllBtn = new Button("Show All");
        showAllBtn.setOnAction(e -> {
            for (CheckBox cb : sectorCheckboxes) cb.setSelected(true);
            updateShownStartups(sectorCheckboxes, searchBar.getText(), content);
        });
        filterMenu.getItems().add(new CustomMenuItem(showAllBtn));

        filterButton.setOnAction(e -> filterMenu.show(filterButton, Side.BOTTOM, 0, 0));

        // -------------------- FILTER & SEARCH --------------------
        searchBar.textProperty().addListener((obs, oldVal, newVal) ->
                updateShownStartups(sectorCheckboxes, newVal, content)
        );

        updateShownStartups(sectorCheckboxes, "", content);

        return dealFlow;
    }

    private static void updateShownStartups(List<CheckBox> sectorCheckboxes, String searchText, HBox content) {
        shownStartups.clear();

        // Determine which sectors are checked
        Set<String> activeSectors = sectorCheckboxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toSet());

        // Filter startups
        for (Map.Entry<String, List<Startup>> entry : startups.entrySet()) {
            String sector = entry.getKey();
            if (!activeSectors.contains(sector)) continue;

            List<Startup> filtered = entry.getValue().stream()
                    .filter(s -> s.getName().toLowerCase().contains(searchText.toLowerCase())
                            || s.getDescription().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());

            if (!filtered.isEmpty()) {
                shownStartups.put(sector, filtered);
            }
        }

        // Rebuild UI
        content.getChildren().clear();
        for (Map.Entry<String, List<Startup>> entry : shownStartups.entrySet()) {
            VBox sectorBox = buildSectorBox(entry.getKey(), entry.getValue());
            content.getChildren().add(sectorBox);
        }
    }

    private static VBox buildSectorBox(String sector, List<Startup> startups) {
        VBox sectorBox = new VBox(10);
        sectorBox.setPrefWidth(250);

        // Header with color + name
        HBox header = new HBox(5);
        Rectangle colorSquare = new Rectangle(20, 20, Repository.getInstance().getSectorColor().get(sector));
        Label title = new Label(sector);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        header.getChildren().addAll(colorSquare, title);

        sectorBox.getChildren().add(header);

        // Startups
        for (Startup s : startups) {
            Button card = buildStartupCard(s, Repository.getInstance().getSectorColor().get(sector));
            sectorBox.getChildren().add(card);
        }

        return sectorBox;
    }

    private static Button buildStartupCard(Startup s, Color accent) {
        Button card = new Button();
        card.setPrefWidth(230);
        card.setWrapText(true);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: #CCCCCC;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 10;"
        );

        HBox layout = new HBox(10);

        // Accent stripe
        Rectangle stripe = new Rectangle(5, 80, accent);

        // Info
        VBox info = new VBox(5);
        Label name = new Label(s.getName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label desc = new Label(s.getDescription());
        desc.setWrapText(true);

        GridPane details = new GridPane();
        details.setHgap(10);
        details.setVgap(5);

        details.addRow(0, new Label("Funding:"), new Label(s.getFundingStage()));
        details.addRow(1, new Label("Ticket:"), new Label(String.valueOf(s.getTicketSize())));
        details.addRow(2, new Label("Location:"), new Label(s.getLocation()));
        details.addRow(3, new Label("Founded:"), new Label(String.valueOf(s.getFoundYear())));

        info.getChildren().addAll(name, desc, details);

        layout.getChildren().addAll(stripe, info);
        card.setGraphic(layout);

        return card;
    }
}
