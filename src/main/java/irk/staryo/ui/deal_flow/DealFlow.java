package irk.staryo.ui.deal_flow;

import irk.staryo.model.Startup;
import irk.staryo.utils.Repository;
import javafx.beans.binding.Bindings;
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
        topBar.setPadding(new Insets(20));
        topBar.setSpacing(20);
        topBar.setStyle("-fx-background-color: #FFFFFF;");

        // --- Left Text ---
        VBox leftText = new VBox();
        leftText.setAlignment(Pos.CENTER_LEFT);

        Text header = new Text("Deal Flow");
        header.setFont(Font.font("Arial", 24));
        header.setStyle("-fx-font-weight: bold;");

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

        Button filterButton = new Button();
        FontIcon filterIcon = new FontIcon("mdi-filter");
        filterIcon.getStyleClass().add("button-icon");
        filterButton.setGraphic(filterIcon);
        filterButton.getStyleClass().add("regular-button-off");
        filterButton.setPrefHeight(40);
        filterButton.setPrefWidth(40);

        rightControls.getChildren().addAll(searchBar, filterButton);

        HBox.setHgrow(leftText, Priority.ALWAYS);
        topBar.getChildren().addAll(leftText, rightControls);

        // -------------------- MAIN CONTENT --------------------
        ScrollPane mainContent = new ScrollPane();
        HBox content = new HBox();
        content.setFillHeight(false);
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
        mainContent.setFitToHeight(true);

        dealFlow.setTop(topBar);
        dealFlow.setCenter(mainContent);

        // -------------------- FILTER --------------------
        ContextMenu filterMenu = new ContextMenu();
        filterMenu.prefWidth(300);
        List<CheckBox> sectorCheckboxes = new ArrayList<>();

        filterMenu.showingProperty().addListener((obs, oldVal, newVal) -> {
            if(filterButton.getStyleClass().remove("regular-button-on")) {
                filterButton.getStyleClass().add("regular-button-off");

            } else {
                filterButton.getStyleClass().remove("regular-button-off");
                filterButton.getStyleClass().add("regular-button-on");
            }
        });

        for (String sector : startups.keySet()) {
            CheckBox cb = new CheckBox(sector);
            cb.setSelected(true);
            CustomMenuItem item = new CustomMenuItem(cb);
            item.setHideOnClick(false);
            filterMenu.getItems().add(item);
            sectorCheckboxes.add(cb);
        }

        filterButton.setOnAction(e -> {
            if (filterMenu.isShowing()) {
                filterMenu.hide();
                return;
            }

            filterMenu.getItems().clear();
            sectorCheckboxes.clear();

            for (String sector : startups.keySet()) {
                CheckBox cb = new CheckBox(sector);
                if (shownStartups.containsKey(sector)){
                    cb.setSelected(true);
                }
                CustomMenuItem item = new CustomMenuItem(cb);
                item.setHideOnClick(false);
                filterMenu.getItems().add(item);
                sectorCheckboxes.add(cb);

                cb.selectedProperty().addListener((obs, oldVal, newVal) ->
                        updateShownStartups(sectorCheckboxes, searchBar.getText(), content)
                );
            }

            Button showAllBtn = new Button("Show All");
            showAllBtn.setStyle("-fx-cursor: hand;");
            showAllBtn.setOnAction(ev -> {
                for (CheckBox cb : sectorCheckboxes) cb.setSelected(true);
                updateShownStartups(sectorCheckboxes, searchBar.getText(), content);
            });
            filterMenu.getItems().add(new CustomMenuItem(showAllBtn));

            filterMenu.show(filterButton, Side.BOTTOM, -100, 10);
        });

        // -------------------- FILTER & SEARCH --------------------
        searchBar.textProperty().addListener((obs, oldVal, newVal) ->
                updateShownStartups(sectorCheckboxes, newVal, content)
        );

        updateShownStartups(sectorCheckboxes, "", content);

        return dealFlow;
    }

    private static void updateShownStartups(List<CheckBox> sectorCheckboxes, String searchText, HBox content) {
        shownStartups.clear();

        Set<String> activeSectors = sectorCheckboxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toSet());

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

        content.getChildren().clear();
        for (Map.Entry<String, List<Startup>> entry : shownStartups.entrySet()) {
            VBox sectorBox = SectorColumn.buildSectorColumn(entry.getKey(), entry.getValue());
            content.getChildren().add(sectorBox);
        }
    }
}
