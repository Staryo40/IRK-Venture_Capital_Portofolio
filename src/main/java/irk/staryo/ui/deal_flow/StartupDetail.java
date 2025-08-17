package irk.staryo.ui.deal_flow;

import irk.staryo.model.DiscretePMF;
import irk.staryo.model.ProceedsScenarioTrend;
import irk.staryo.model.Startup;
import irk.staryo.utils.PmfCalculator;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StartupDetail {
    private static Startup currentStartup;
    private static BarChart<String, Number> probabilityChart;
    private static LineChart<Number, Number> trendsChart;
    private static Label pessimisticValue;
    private static Label realisticValue;
    private static Label optimisticValue;
    private static Label gpsTitle;
    private static int selectedDayIndex = 0; // 0 = today (last day)

    public static ScrollPane getStartupDetail(Startup startup, Stage stage) {
        currentStartup = startup;

        ScrollPane mainContent = new ScrollPane();
        mainContent.setFitToWidth(true);
        mainContent.setFitToHeight(false);
        mainContent.setStyle(
                "-fx-background: #FFFFFF;" +
                        "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: transparent;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #FFFFFF;");

        // -------------------- BACK BUTTON --------------------
        HBox backButtonContainer = new HBox();
        Button backButton = new Button();
        FontIcon backIcon = new FontIcon("mdi-arrow-left");
        backIcon.getStyleClass().add("button-icon");
        backButton.setGraphic(backIcon);
        backButton.setText(" Back to Deal Flow");
        backButton.getStyleClass().add("regular-button-off");
        backButton.setPrefHeight(40);
        backButton.setOnAction(e -> {
            navigateBackToDealFlow(stage);
        });
        backButtonContainer.getChildren().add(backButton);

        // -------------------- HEADER --------------------
        VBox headerSection = new VBox(5);
        Label nameLabel = new Label(startup.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label descLabel = new Label(startup.getDescription());
        descLabel.setFont(Font.font("Arial", 14));
        descLabel.setWrapText(true);

        headerSection.getChildren().addAll(nameLabel, descLabel);

        // -------------------- SECTIONS --------------------
        HBox infoBoxes = createInfoBoxes(startup);
        VBox gpsBox = createGPSBox(startup);
        VBox trendsBox = createTrendsBox(startup);

        mainContainer.getChildren().addAll(
                backButtonContainer,
                headerSection,
                infoBoxes,
                gpsBox,
                trendsBox
        );

        mainContent.setContent(mainContainer);
        updateGPSData(0); // Initialize with 4 August 2025

        return mainContent;
    }

    private static HBox createInfoBoxes(Startup startup) {
        HBox container = new HBox(20);
        container.setAlignment(Pos.CENTER);

        // Create info boxes
        VBox sectorBox = createInfoBox("Sector", startup.getSector());
        VBox foundedBox = createInfoBox("Founded Year", String.valueOf(startup.getFoundYear()));
        VBox locationBox = createInfoBox("Location", startup.getLocation());
        VBox fundingBox = createInfoBox("Funding Stage", startup.getFundingStage());
        VBox ticketBox = createInfoBox("Ticket Size", "$" + startup.getTicketSize() + "M");

        container.getChildren().addAll(sectorBox, foundedBox, locationBox, fundingBox, ticketBox);

        return container;
    }

    private static VBox createInfoBox(String title, String value) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setStyle("-fx-text-fill: #888888;");

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        box.getChildren().addAll(titleLabel, valueLabel);
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);

        return box;
    }

    private static VBox createGPSBox(Startup startup) {
        VBox gpsBox = new VBox(15);
        gpsBox.setPadding(new Insets(20));
        gpsBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 10; -fx-background-radius: 10;");

        // -------------------- Title --------------------
        gpsTitle = new Label();
        gpsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        updateGPSTitle(0);

        // -------------------- Scenarios --------------------
        HBox scenarioBoxes = new HBox(20);
        scenarioBoxes.setAlignment(Pos.CENTER);

        VBox pessimisticBox = createScenarioBox("Pessimistic", "#FFE6E6", "#CC0000");
        pessimisticValue = (Label) ((VBox) pessimisticBox.getChildren().getFirst()).getChildren().get(1);

        VBox realisticBox = createScenarioBox("Realistic", "#FFF2E6", "#FF8800");
        realisticValue = (Label) ((VBox) realisticBox.getChildren().getFirst()).getChildren().get(1);

        VBox optimisticBox = createScenarioBox("Optimistic", "#E6F7E6", "#00AA00");
        optimisticValue = (Label) ((VBox) optimisticBox.getChildren().getFirst()).getChildren().get(1);

        scenarioBoxes.getChildren().addAll(pessimisticBox, realisticBox, optimisticBox);

        // -------------------- PROBABILITY DISTRIBUTION --------------------
        Label probTitle = new Label("Probability Distribution");
        probTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        probTitle.setPadding(new Insets(10, 0, 0, 0));
        probabilityChart = createProbabilityChart();

        gpsBox.getChildren().addAll(gpsTitle, scenarioBoxes, probTitle, probabilityChart);

        return gpsBox;
    }

    private static VBox createScenarioBox(String scenario, String bgColor, String textColor) {
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        HBox.setHgrow(container, Priority.ALWAYS);

        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: " + bgColor + "; -fx-border-radius: 8; -fx-background-radius: 8;");
        box.setMaxWidth(Double.MAX_VALUE);

        Label titleLabel = new Label(scenario);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setStyle("-fx-text-fill: #666666;");

        Label valueLabel = new Label("0");
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valueLabel.setStyle("-fx-text-fill: " + textColor + ";");

        box.getChildren().addAll(titleLabel, valueLabel);
        container.getChildren().add(box);

        return container;
    }

    private static BarChart<String, Number> createProbabilityChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Proceeds (Millions USD)");
        yAxis.setLabel("Probability");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Probability Distribution");
        chart.setPrefHeight(300);
        chart.setLegendVisible(false);

        return chart;
    }

    private static VBox createTrendsBox(Startup startup) {
        VBox trendsBox = new VBox(15);
        trendsBox.setPadding(new Insets(20));
        trendsBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 10; -fx-background-radius: 10;");

        ProceedsScenarioTrend trend = startup.getProceedsScenarioTrend();
        int daysCount = trend.getPessimistic().size();

        Label trendsTitle = new Label("Proceeds Trends - Last " + daysCount + " Days");
        trendsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        trendsChart = createTrendsChart(startup);
        trendsBox.getChildren().addAll(trendsTitle, trendsChart);

        return trendsBox;
    }

    private static LineChart<Number, Number> createTrendsChart(Startup startup) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Days Ago");
        yAxis.setLabel("Proceeds (Millions USD)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Proceeds Trends");
        chart.setPrefHeight(400);
        chart.setCreateSymbols(true);

        ProceedsScenarioTrend trend = startup.getProceedsScenarioTrend();

        XYChart.Series<Number, Number> pessimisticSeries = new XYChart.Series<>();
        pessimisticSeries.setName("Pessimistic");

        XYChart.Series<Number, Number> realisticSeries = new XYChart.Series<>();
        realisticSeries.setName("Realistic");

        XYChart.Series<Number, Number> optimisticSeries = new XYChart.Series<>();
        optimisticSeries.setName("Optimistic");

        for (int i = 0; i < trend.getPessimistic().size(); i++) {
            int daysAgo = trend.getPessimistic().size() - 1 - i;
            pessimisticSeries.getData().add(new XYChart.Data<>(daysAgo, trend.getPessimistic().get(i)));
            realisticSeries.getData().add(new XYChart.Data<>(daysAgo, trend.getRealistic().get(i)));
            optimisticSeries.getData().add(new XYChart.Data<>(daysAgo, trend.getOptimistic().get(i)));
        }

        chart.getData().addAll(pessimisticSeries, realisticSeries, optimisticSeries);
        chart.setStyle("-fx-cursor: hand;");

        chart.setOnMouseClicked(e -> {
            NumberAxis xAxisNode = (NumberAxis) chart.getXAxis();
            Point2D mouseScene = new Point2D(e.getSceneX(), e.getSceneY());
            Point2D mouseInXAxis = xAxisNode.sceneToLocal(mouseScene);

            double xValue = xAxisNode.getValueForDisplay(mouseInXAxis.getX()).doubleValue();

            // Find the closest day index
            int closestDayIndex = (int) Math.round(trend.getPessimistic().size() - 1 - xValue);
            closestDayIndex = Math.max(0, Math.min(trend.getPessimistic().size() - 1, closestDayIndex));

            if (closestDayIndex != selectedDayIndex) {
                clearDataPointHighlights(chart);
                selectedDayIndex = closestDayIndex;
                highlightDataPoints(chart, selectedDayIndex, trend.getPessimistic().size());
                updateGPSData(selectedDayIndex);
            }
        });

        // Style the lines
        chart.applyCss();
        chart.lookupAll(".chart-series-line").forEach(node -> {
            if (node.getStyleClass().contains("series0")) {
                node.setStyle("-fx-stroke: #CC0000; -fx-stroke-width: 2px;"); // Red for pessimistic
            } else if (node.getStyleClass().contains("series1")) {
                node.setStyle("-fx-stroke: #FF8800; -fx-stroke-width: 2px;"); // Orange for realistic
            } else if (node.getStyleClass().contains("series2")) {
                node.setStyle("-fx-stroke: #00AA00; -fx-stroke-width: 2px;"); // Green for optimistic
            }
        });

        return chart;
    }

    private static void updateGPSData(int dayIndex) {
        selectedDayIndex = dayIndex;
        ProceedsScenarioTrend trend = currentStartup.getProceedsScenarioTrend();

        int arrayIndex = (trend.getPessimistic().size() - 1) - dayIndex;

        updateGPSTitle(dayIndex);
        pessimisticValue.setText(trend.getPessimistic().get(arrayIndex) + "M");
        realisticValue.setText(trend.getRealistic().get(arrayIndex) + "M");
        optimisticValue.setText(trend.getOptimistic().get(arrayIndex) + "M");

        updateProbabilityChart(
                trend.getPessimistic().get(arrayIndex),
                trend.getRealistic().get(arrayIndex),
                trend.getOptimistic().get(arrayIndex)
        );
    }

    private static void updateGPSTitle(int dayIndex) {
        LocalDate baseDate = LocalDate.of(2025, 8, 4);
        LocalDate selectedDate = baseDate.minusDays(dayIndex);
        String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        gpsTitle.setText("Gross Proceeds Scenario of " + formattedDate + " (Millions USD)");
    }

    private static void updateProbabilityChart(int pessimistic, int realistic, int optimistic) {
        DiscretePMF pmf = PmfCalculator.pmfFromPert(pessimistic, realistic, optimistic);

        probabilityChart.getData().clear();
        probabilityChart.setAnimated(false);
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (int i = 0; i < pmf.p.size(); i++) {
            int value = pmf.min + i;
            double probability = pmf.p.get(i);
            series.getData().add(new XYChart.Data<>(String.valueOf(value), probability));
        }

        probabilityChart.getData().add(series);
    }

    private static void clearDataPointHighlights(LineChart<Number, Number> chart) {
        chart.lookupAll(".chart-line-symbol").forEach(node -> {
            node.setStyle("-fx-background-radius: 3px; -fx-padding: 3px;");
        });
    }

    private static void highlightDataPoints(LineChart<Number, Number> chart, int dayIndex, int totalDays) {
        double targetXValue = totalDays - 1 - dayIndex;

        for (int seriesIndex = 0; seriesIndex < chart.getData().size(); seriesIndex++) {
            XYChart.Series<Number, Number> series = chart.getData().get(seriesIndex);

            for (XYChart.Data<Number, Number> dataPoint : series.getData()) {
                if (Math.abs(dataPoint.getXValue().doubleValue() - targetXValue) < 0.1) {
                    String highlightColor;
                    if (seriesIndex == 0) {
                        highlightColor = "#CC0000";
                    } else if (seriesIndex == 1) {
                        highlightColor = "#FF8800";
                    } else {
                        highlightColor = "#00AA00";
                    }

                    if (dataPoint.getNode() != null) {
                        dataPoint.getNode().setStyle(
                                "-fx-background-color: " + highlightColor + ";" +
                                        "-fx-background-radius: 4px;" +
                                        "-fx-padding: 4px;" +
                                        "-fx-border-color: white;" +
                                        "-fx-border-width: 2px;" +
                                        "-fx-border-radius: 4px;"
                        );
                    }
                }
            }
        }
    }

    private static void navigateBackToDealFlow(Stage stage) {
        BorderPane mainScene = (BorderPane) stage.getScene().getRoot();
        HBox mainContent = (HBox) mainScene.getCenter();

        if (mainContent.getChildren().size() >= 2) {
            VBox rightPanel = (VBox) mainContent.getChildren().get(1);
            rightPanel.getChildren().clear();
            rightPanel.getChildren().add(DealFlow.getDealFlow(stage));
        }
    }
}