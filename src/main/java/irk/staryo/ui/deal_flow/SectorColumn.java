package irk.staryo.ui.deal_flow;

import irk.staryo.model.Startup;
import irk.staryo.utils.Repository;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class SectorColumn {
    public static VBox buildSectorColumn(String sector, List<Startup> startups, Stage stage){
        VBox sectorBox = new VBox(10);
        sectorBox.setPrefWidth(330);

        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        Circle colorCircle = new Circle(10, Repository.getInstance().getSectorColor().get(sector));
        Label title = new Label(sector);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        header.getChildren().addAll(colorCircle, title);

        sectorBox.getChildren().add(header);

        for (Startup s : startups) {
            Button card = StartupItem.buildStartupButton(s, Repository.getInstance().getSectorColor().get(sector), stage);
            sectorBox.getChildren().add(card);
        }

        return sectorBox;
    }
}
