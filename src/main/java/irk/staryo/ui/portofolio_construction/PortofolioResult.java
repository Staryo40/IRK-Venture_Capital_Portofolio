package irk.staryo.ui.portofolio_construction;

import irk.staryo.model.DiscretePMF;
import irk.staryo.model.PortofolioDpResult;
import irk.staryo.model.Startup;
import irk.staryo.ui.deal_flow.DealFlow;
import irk.staryo.utils.PmfCalculator;
import irk.staryo.utils.Repository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PortofolioResult {
    public static PortofolioDpResult result;
    public static LocalDate date;
    public static int targetFund;
    public static int investmentBudget;

    public static ScrollPane getPortofolioResult(Stage stage, PortofolioDpResult res, LocalDate d, int target, int budget){
        result = res;
        date = d;
        targetFund = target;
        investmentBudget = budget;

        // -------------------- CONTAINER --------------------
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
            navigateBackToPortofolioConstruction(stage);
        });
        backButtonContainer.getChildren().add(backButton);

        // -------------------- HEADER --------------------
        VBox headerSection = new VBox(5);
        Label nameLabel = new Label("Portofolio Construction Result");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label descLabel = new Label("Analysis of constructed investment portofolio");
        descLabel.setFont(Font.font("Arial", 14));
        descLabel.setWrapText(true);

        headerSection.getChildren().addAll(nameLabel, descLabel);

        // -------------------- CHANCE --------------------
        VBox chanceContainer = new VBox(5);
        chanceContainer.setAlignment(Pos.CENTER);

        double probabilityValue = result.getDistribution().chanceFromMinimum(target);
        double percentage = probabilityValue * 100;
        String percentageText = String.format("%.3f%%", percentage);
        Circle probCircle = new Circle(80);
        probCircle.setFill(Color.LIGHTBLUE);
        probCircle.setStroke(Color.DODGERBLUE);
        probCircle.setStrokeWidth(3);

        Text probText = new Text(percentageText);
        probText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        probText.setFill(Color.DARKBLUE);

        Text labelText = new Text("Target Probability");
        labelText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        labelText.setFill(Color.DARKBLUE);

        VBox probabilityLabel = new VBox(5, probText, labelText);
        probabilityLabel.setAlignment(Pos.CENTER);
        StackPane probStack = new StackPane(probCircle, probabilityLabel);
        probStack.setAlignment(Pos.CENTER);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        Label probLabel = new Label("Probability of reaching $" + target + "M performance target on "  + date.format(formatter));
        probLabel.setFont(Font.font("Arial", 14));
        probLabel.setWrapText(true);
        probLabel.setAlignment(Pos.CENTER);

        chanceContainer.getChildren().addAll(probStack, probLabel);

        // -------------------- PROBABILITY DISTRIBUTION --------------------
        VBox distributionBox = createProbabilityBox(result.getDistribution());

        // -------------------- EXPECTED FUND RETURN --------------------
        VBox expectedContainer = new VBox();

        Label distributionTitle = new Label("Expected Fund Performance");
        distributionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        distributionTitle.setPadding(new Insets(10, 0, 0, 0));

        HBox returnValueBox = new HBox(20);
        returnValueBox.setAlignment(Pos.CENTER);

        double expectedReturn = result.getDistribution().expectedGain();
        String returnText = String.format("$%.3fM", expectedReturn);
        VBox expectedReturnBox = createScenarioBox("Expected Return", "#E6F7E6", "#00AA00", returnText);

        double expectedMultiple = expectedReturn / budget;
        String multipleText = String.format("%.3fx", expectedMultiple);
        VBox realisticBox = createScenarioBox("Expected Multiple", "#F3E6FF", "#8800FF", multipleText);

        returnValueBox.getChildren().addAll(expectedReturnBox, realisticBox);
        expectedContainer.getChildren().addAll(distributionTitle, returnValueBox);

        // -------------------- STARTUPS --------------------
        List<Startup> startups = res.getStartups();

        VBox allStartupContainer = new VBox();
        allStartupContainer.setSpacing(10);

        Label startupTitle = new Label("Selected Startups (" + startups.size() + ")");
        startupTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        startupTitle.setPadding(new Insets(10, 0, 0, 0));

        FlowPane startupFlow = new FlowPane();
        startupFlow.setHgap(10);
        startupFlow.setVgap(10);
        startupFlow.setPadding(new Insets(5, 0, 0, 0));

        for (Startup s : startups) {
            VBox startupContainer = new VBox();
            startupContainer.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
            startupContainer.setSpacing(5);
            startupContainer.setPrefWidth(300);

            Label lbl = new Label(s.getName());
            lbl.setStyle("-fx-font-size: 14px; -fx-padding: 2; -fx-font-weight: bold;");

            HBox sectorSection = new HBox();
            sectorSection.setAlignment(Pos.CENTER_LEFT);
            sectorSection.setSpacing(5);
            Circle colorCircle = new Circle(8, Repository.getInstance().getSectorColor().get(s.getSector()));
            Label sectorLabel = new Label(s.getSector());
            sectorLabel.setStyle("-fx-font-size: 10px; -fx-padding: 2;");

            sectorSection.getChildren().addAll(colorCircle, sectorLabel);
            startupContainer.getChildren().addAll(lbl, sectorSection);

            startupFlow.getChildren().add(startupContainer);
        }

        allStartupContainer.getChildren().addAll(startupTitle, startupFlow);

        mainContainer.getChildren().addAll(
                backButtonContainer,
                headerSection,
                chanceContainer,
                distributionBox,
                expectedContainer,
                allStartupContainer
        );

        mainContent.setContent(mainContainer);

        return mainContent;
    }

    private static VBox createScenarioBox(String scenario, String bgColor, String textColor, String value) {
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

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valueLabel.setStyle("-fx-text-fill: " + textColor + ";");

        box.getChildren().addAll(titleLabel, valueLabel);
        container.getChildren().add(box);

        return container;
    }

    private static VBox createProbabilityBox(DiscretePMF distribution) {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 10; -fx-background-radius: 10;");

        // -------------------- TITLE --------------------
        Label distributionTitle = new Label("Probability Performance Distribution");
        distributionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        distributionTitle.setPadding(new Insets(10, 0, 0, 0));

        // -------------------- PROBABILITY DISTRIBUTION --------------------
        BarChart<String, Number> probabilityChart = createProbabilityChart(distribution);

        container.getChildren().addAll(distributionTitle, probabilityChart);

        return container;
    }

    private static BarChart<String, Number> createProbabilityChart(DiscretePMF distribution) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Proceeds (Millions USD)");
        yAxis.setLabel("Probability");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Probability Distribution");
        chart.setPrefHeight(300);
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (int i = 0; i < distribution.p.size(); i++) {
            int value = distribution.min + i;
            double probability = distribution.p.get(i);
            series.getData().add(new XYChart.Data<>(String.valueOf(value), probability));
        }

        chart.getData().add(series);

        return chart;
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
}
