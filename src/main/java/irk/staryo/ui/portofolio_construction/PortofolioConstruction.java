package irk.staryo.ui.portofolio_construction;

import irk.staryo.model.Startup;
import irk.staryo.ui.deal_flow.SectorColumn;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PortofolioConstruction {
    public static Map<String, List<Startup>> startups = new HashMap<>();;
    public static Map<String, List<Startup>> shownStartups = new HashMap<>();

    public static BorderPane getPortofolioConstruction(Stage stage) {
        BorderPane portofolioConstruction = new BorderPane();

        // -------------------- TOP BAR --------------------
        VBox topBar = new VBox();
        topBar.setSpacing(20);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: #FFFFFF;");

        // --- Title ---
        VBox leftText = new VBox();
        leftText.setAlignment(Pos.CENTER_LEFT);

        Text header = new Text("Portofolio Construction");
        header.setFont(Font.font("Arial", 24));
        header.setStyle("-fx-font-weight: bold;");

        Text subText = new Text("Build optimal investment portofolio based on budget and performance target.");
        subText.setFont(Font.font("Arial", 14));

        leftText.getChildren().addAll(header, subText);

        HBox.setHgrow(leftText, Priority.ALWAYS);

        // --- Inputs ---
        HBox topInputs = new HBox();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Left
        Label investmentBudgetTitle = new Label("Total Investment Budget (Millions USD):");
        TextField investmentBudgetField = new TextField();
        investmentBudgetField.setPromptText("17");
        investmentBudgetField.setPrefHeight(40);
        investmentBudgetField.setPrefWidth(200);
        VBox investmentBudget = new VBox(5);
        investmentBudget.setPadding(new Insets(10));
        investmentBudget.getChildren().addAll(investmentBudgetTitle, investmentBudgetField);

        Label targetFundTitle = new Label("Target Fund Gain (Millions USD)");
        TextField targetFundField = new TextField();
        targetFundField.setPromptText("30");
        targetFundField.setPrefHeight(40);
        targetFundField.setPrefWidth(200);
        VBox targetFund = new VBox(5);
        targetFund.setPadding(new Insets(10));
        targetFund.getChildren().addAll(targetFundTitle, targetFundField);

        Label constructionDateTitle = new Label("Construction Date");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("yyyy-MM-dd");
        datePicker.setValue(LocalDate.of(2025, 8, 4));
        datePicker.setPrefHeight(40);
        VBox constructionDate = new VBox(5);
        constructionDate.setPadding(new Insets(10));
        constructionDate.getChildren().addAll(constructionDateTitle, datePicker);

        // Right
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

        topInputs.getChildren().addAll(investmentBudget, targetFund, constructionDate, spacer, rightControls);

        // Date picker listener
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Selected date is: " + newValue);
        });

        HBox executionRow = new HBox();
        executionRow.setAlignment(Pos.CENTER_LEFT);
        executionRow.setSpacing(10);

        Button executionButton = new Button("Execute");
        executionButton.setPrefHeight(40);
        executionButton.setPrefWidth(240);

        HBox.setMargin(executionButton, new Insets(-10, 0, 0, 10));
        executionRow.getChildren().addAll(executionButton);

        topBar.getChildren().addAll(leftText, topInputs, executionRow);

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

        portofolioConstruction.setTop(topBar);
        portofolioConstruction.setCenter(mainContent);

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
            cb.setStyle("-fx-cursor: hand;");
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
                cb.setStyle("-fx-cursor: hand;");
                if (shownStartups.containsKey(sector)){
                    cb.setSelected(true);
                }
                CustomMenuItem item = new CustomMenuItem(cb);
                item.setHideOnClick(false);
                filterMenu.getItems().add(item);
                sectorCheckboxes.add(cb);

                cb.selectedProperty().addListener((obs, oldVal, newVal) ->
                        updateShownStartups(sectorCheckboxes, searchBar.getText(), content, stage)
                );
            }

            Button showAllBtn = new Button("Show All");
            showAllBtn.setStyle("-fx-cursor: hand;");
            showAllBtn.setOnAction(ev -> {
                for (CheckBox cb : sectorCheckboxes) cb.setSelected(true);
                updateShownStartups(sectorCheckboxes, searchBar.getText(), content, stage);
            });
            filterMenu.getItems().add(new CustomMenuItem(showAllBtn));

            filterMenu.show(filterButton, Side.BOTTOM, -100, 10);
        });

        // -------------------- FILTER & SEARCH --------------------
        searchBar.textProperty().addListener((obs, oldVal, newVal) ->
                updateShownStartups(sectorCheckboxes, newVal, content, stage)
        );

        updateShownStartups(sectorCheckboxes, "", content, stage);

        return portofolioConstruction;
    }

    private static void updateShownStartups(List<CheckBox> sectorCheckboxes, String searchText, HBox content, Stage stage) {
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
            VBox sectorBox = SectorColumnSelection.buildSectorColumn(entry.getKey(), entry.getValue(), stage);
            content.getChildren().add(sectorBox);
        }
    }
}
