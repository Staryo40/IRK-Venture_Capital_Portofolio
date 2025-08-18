package irk.staryo.ui.portofolio_construction;

import irk.staryo.model.FilteredStringbitStartups;
import irk.staryo.model.PortofolioDpResult;
import irk.staryo.model.Startup;
import irk.staryo.ui.deal_flow.SectorColumn;
import irk.staryo.ui.deal_flow.StartupDetail;
import irk.staryo.utils.LazyDynamicProgramming;
import irk.staryo.utils.Repository;
import irk.staryo.utils.StartupCombination;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class PortofolioConstruction {
    public static Map<String, List<Startup>> startups = new HashMap<>();;
    public static Map<String, List<Startup>> shownStartups = new HashMap<>();
    public static ObservableSet<Startup> selectedStartups = FXCollections.observableSet();
    public static VBox selectedPanelBox = new VBox(10);
    public static BooleanProperty selectedPanelVisible = new SimpleBooleanProperty(false);

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
        investmentBudgetField.getStyleClass().add("text-input");
        VBox investmentBudget = new VBox(5);
        investmentBudget.setPadding(new Insets(10));
        investmentBudget.getChildren().addAll(investmentBudgetTitle, investmentBudgetField);

        investmentBudgetField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));

        Label targetFundTitle = new Label("Target Fund Gain (Millions USD)");
        TextField targetFundField = new TextField();
        targetFundField.setPromptText("30");
        targetFundField.setPrefHeight(40);
        targetFundField.setPrefWidth(200);
        targetFundField.getStyleClass().add("text-input");
        VBox targetFund = new VBox(5);
        targetFund.setPadding(new Insets(10));
        targetFund.getChildren().addAll(targetFundTitle, targetFundField);

        targetFundField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));

        Label constructionDateTitle = new Label("Construction Date");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("yyyy-MM-dd");
        datePicker.setValue(LocalDate.of(2025, 8, 4));
        datePicker.setPrefHeight(40);
        datePicker.getStyleClass().add("date-picker");
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

        // --- Execution ---
        HBox executionRow = new HBox();
        executionRow.setAlignment(Pos.CENTER_LEFT);
        executionRow.setSpacing(10);

        Region executionSpacer = new Region();
        HBox.setHgrow(executionSpacer, Priority.ALWAYS);

        Button executionButton = new Button("Execute");
        executionButton.getStyleClass().add("execute-button");
        executionButton.setPrefHeight(40);
        executionButton.setPrefWidth(240);

        Button selectedStartupButton = new Button("Selected Startup (0)");
        selectedStartupButton.getStyleClass().add("execute-button");
        selectedStartupButton.setPrefHeight(40);
        selectedStartupButton.setPrefWidth(160);

        // Button text updates
        Runnable updateButtonText = () -> {
            int count = PortofolioConstruction.selectedStartups.size();
            selectedStartupButton.setText("Selected Startup (" + count + ")");
        };
        updateButtonText.run();
        PortofolioConstruction.selectedStartups.addListener((SetChangeListener<Startup>) change -> {
            updateButtonText.run();
        });

        // -------------------- RIGHT PANEL (selected startups) --------------------
        selectedPanelBox.setPadding(new Insets(10));
        selectedPanelBox.setStyle(
                "-fx-background-color: #F9F9F9;" +
                        "-fx-border-color: #DDDDDD; " +
                        "-fx-focus-color: transparent; " +
                        "-fx-faint-focus-color: transparent;"
        );
        selectedPanelBox.setPrefWidth(300);

        ScrollPane rightPanel = new ScrollPane(selectedPanelBox);
        rightPanel.setFitToWidth(true);
        rightPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        selectedStartups.addListener((SetChangeListener<Startup>) change -> {
            if (change.wasAdded()) addToSelectedPanel(change.getElementAdded());
            if (change.wasRemoved()) removeFromSelectedPanel(change.getElementRemoved());
        });

        selectedStartupButton.setOnAction(e ->
                PortofolioConstruction.selectedPanelVisible.set(!PortofolioConstruction.selectedPanelVisible.get())
        );

        PortofolioConstruction.selectedPanelVisible.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                portofolioConstruction.setRight(rightPanel);
                portofolioConstruction.getRight().setStyle(
                        "-fx-background-color: white;" +
                                "-fx-focus-color: transparent;" +
                                "-fx-faint-focus-color: transparent;"
                );
            } else {
                portofolioConstruction.setRight(null);
            }
        });

        HBox.setMargin(executionButton, new Insets(-10, 0, 0, 10));
        HBox.setMargin(selectedStartupButton, new Insets(-10, 0, 0, 0));
        executionRow.getChildren().addAll(executionButton, executionSpacer, selectedStartupButton);

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

        // -------------------- EXECUTION LOGIC --------------------
        executionButton.setOnAction(ev -> {
            List<Startup> selectedList = new ArrayList<>(PortofolioConstruction.selectedStartups);
            int investmentBudgetValue = 0;
            int targetFundGain = 0;
            LocalDate constructionDateValue = datePicker.getValue();

            if (selectedList.isEmpty()){
                showAlert("Error!", "Input Error Detected", "No startup selected");
                return;
            }
            try {
                investmentBudgetValue = Integer.parseInt(investmentBudgetField.getText());
                if (investmentBudgetValue <= 0){
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Investment Budget must be a number", "Please enter a valid number for Investment Budget that is above 0.");
                return;
            }

            try {
                targetFundGain = Integer.parseInt(targetFundField.getText());
                if (targetFundGain <= 0){
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Target Fund Gain must be a number", "Please enter a valid number for Target Fund Gain that is above 0.");
                return;
            }

            LocalDate maxDate = LocalDate.of(2025, 8, 4);
            if (constructionDateValue == null) {
                showAlert("Invalid Input", "Construction Date is required", "Please select a construction date.");
                return;
            }
            if (constructionDateValue.isAfter(maxDate)){
                showAlert("Invalid Date", "Construction Date cannot be after 4 August 2025", "Please select a valid date.");
                return;
            }

            FilteredStringbitStartups filteredStartups;
            int targetDateIndex = (int) ChronoUnit.DAYS.between(maxDate, constructionDateValue);
            try {
                filteredStartups = StartupCombination.filterTargetBased(selectedList, investmentBudgetValue, targetDateIndex, targetFundGain);
                if (filteredStartups.getStringBits().isEmpty()){
                    throw new Exception("No startup combination can reach specified target fund gain, chance is 0%");
                }
            } catch (Exception e){
                showAlert("Filter Error", "A problem occurred when filtering the selected startup list", e.getMessage());
                return;
            }

            showSpinner(stage);
            PortofolioDpResult result = null;
            try {
                result = LazyDynamicProgramming.execute(filteredStartups, targetFundGain, targetDateIndex);
                if (result.getStartups().isEmpty() && result.getDistribution() == null){
                    throw new Exception("Startup combination analysis shows that the max chance is 0%, no combination fit user input criteria.");
                }
            } catch (Exception e) {
                navigateBackToPortofolioConstruction(stage);
                showAlert("Dynamic Problem", "A problem occurred when processing portofolio construction", e.getMessage());
                return;
            }

            if (result == null){
                navigateBackToPortofolioConstruction(stage);
                showAlert("Result Error", "A problem occured with the process result", "Result is null");
                return;
            }

            navigateToResult(stage, result, constructionDateValue, targetFundGain, investmentBudgetValue);
        });

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

    public static void toggleSelection(Startup s) {
        if (selectedStartups.contains(s)) {
            selectedStartups.remove(s);
        } else {
            selectedStartups.add(s);
        }
    }

    public static void addToSelectedPanel(Startup s) {
        for (Node n : selectedPanelBox.getChildren()) {
            if (n.getUserData() == s) return;
        }

        HBox container = new HBox();
        container.setUserData(s);
        container.setPadding(new Insets(5));
        container.setStyle(
                "-fx-border-color: black; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 2; " +
                        "-fx-padding: 4;"
        );

        VBox startupContainer = new VBox();

        Label lbl = new Label(s.getName());
        lbl.setStyle("-fx-font-size: 14px; -fx-padding: 2; -fx-font-weight: bold;");

        HBox sectorSection = new HBox();
        sectorSection.setAlignment(Pos.CENTER_LEFT);
        Circle colorCircle = new Circle(8, Repository.getInstance().getSectorColor().get(s.getSector()));
        Label sectorLabel = new Label(s.getSector());
        sectorLabel.setStyle("-fx-font-size: 10px; -fx-padding: 2;");

        // DELETE BUTTON
        Button deleteButton = new Button();
        FontIcon closeIcon = new FontIcon(MaterialDesign.MDI_CLOSE);
        closeIcon.setIconSize(16);
        closeIcon.setIconColor(javafx.scene.paint.Color.BLACK);

        deleteButton.setGraphic(closeIcon);
        deleteButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-padding: 2 5 2 5;" +
                        "-fx-cursor: hand;"
        );
        deleteButton.setOnAction(e -> toggleSelection(s));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        sectorSection.getChildren().addAll(colorCircle, sectorLabel);
        startupContainer.getChildren().addAll(lbl, sectorSection);

        container.getChildren().addAll(startupContainer, spacer, deleteButton);

        selectedPanelBox.getChildren().add(container);
    }

    public static void removeFromSelectedPanel(Startup s) {
        selectedPanelBox.getChildren().removeIf(node -> node.getUserData() == s);
    }

    public static void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(400); // adjust width as needed
        contentLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");

        alert.getDialogPane().setContent(contentLabel);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: gray;" +
                        "-fx-border-width: 1;" +
                        "-fx-font-family: 'Arial';"
        );

        dialogPane.lookupButton(ButtonType.OK).setStyle(
                "-fx-background-color: #f44336; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        InputStream inputStream = PortofolioConstruction.class.getResourceAsStream("/images/VCPLogo.png");
        stage.getIcons().add(new Image(inputStream));

        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.showAndWait();
    }

    private static void navigateBackToPortofolioConstruction(Stage stage) {
        BorderPane mainScene = (BorderPane) stage.getScene().getRoot();
        HBox mainContent = (HBox) mainScene.getCenter();

        if (mainContent.getChildren().size() >= 2) {
            VBox rightPanel = (VBox) mainContent.getChildren().get(1);
            rightPanel.getChildren().clear();
            rightPanel.getChildren().add(PortofolioConstruction.getPortofolioConstruction(stage));
        }
    }

    private static void showSpinner(Stage stage) {
        BorderPane mainScene = (BorderPane) stage.getScene().getRoot();
        HBox mainContent = (HBox) mainScene.getCenter();

        if (mainContent.getChildren().size() >= 2) {
            VBox rightPanel = (VBox) mainContent.getChildren().get(1);
            rightPanel.getChildren().clear();

            ProgressIndicator spinner = new ProgressIndicator();
            spinner.setStyle(
                    "-fx-progress-color: orange;"
            );

            StackPane spinnerContainer = new StackPane(spinner);
            spinnerContainer.setPrefHeight(rightPanel.getHeight());
            spinnerContainer.setPrefWidth(rightPanel.getWidth());

            rightPanel.getChildren().add(spinnerContainer);
        }
    }

    private static void navigateToResult(Stage stage, PortofolioDpResult result, LocalDate date, int targetFund, int budget) {
        BorderPane mainScene = (BorderPane) stage.getScene().getRoot();
        HBox mainContent = (HBox) mainScene.getCenter();

        if (mainContent.getChildren().size() >= 2) {
            VBox rightPanel = (VBox) mainContent.getChildren().get(1);
            rightPanel.getChildren().clear();
            rightPanel.getChildren().add(PortofolioResult.getPortofolioResult(stage, result, date, targetFund, budget));
        }
    }
}
