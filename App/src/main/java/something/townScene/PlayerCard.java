/* *****************************************

 * CSCI205 - Software Engineering and Design
 * Spring 2022
 * Instructor: Brian King
 * Section: 10 am

 * Name: Luke Snyder
 * Date: xx/xx/2022
 * Lab / Assignment:
 * Description:
 *
 * *****************************************/

package something.townScene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import something.PlayerModel;
import something.Runnable;


@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class PlayerCard {
    public static final int width = Runnable.SCREEN_SIZE / Runnable.ROW_SIZE;
    public static final int height = Runnable.SCREEN_SIZE / Runnable.ROW_SIZE;
    PlayerModel model;
    VBox root;
    Label xpName;
    Label xpLabel;
    Rectangle xpRectangle;
    StackPane xpContainer;
    boolean canLevel;

    public PlayerCard(PlayerModel mod){
        model = mod;
        root = new VBox();
        root.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        root.getChildren().add(model.getRoot());
        root.setAlignment(Pos.CENTER);
        xpName = new Label("Current XP");
        xpName.setAlignment(Pos.CENTER);
        root.getChildren().add(xpName);
        canLevel = model.canLevel();

        xpContainer = new StackPane();
        xpContainer.setMinWidth(width);
        xpContainer.setBackground(new Background(new BackgroundFill(Color.WHITE,
                CornerRadii.EMPTY,
                Insets.EMPTY)));
        xpContainer.setId("xpContainer");
        xpContainer.setAlignment(Pos.CENTER);
        xpRectangle = new Rectangle();
        xpRectangle.setFill(Color.LIGHTBLUE);
        xpRectangle.setId("xpBar");
        xpLabel = new Label(model.getCharacter().xp.getValue() + "/" + model.getCharacter().maxXp);
        xpLabel.setAlignment(Pos.CENTER);
        xpLabel.setPrefWidth(width);
        xpLabel.setId("xpLabel");
        xpContainer.getChildren().add(xpRectangle);
        xpContainer.getChildren().add(xpLabel);
        root.getChildren().add(xpContainer);

        sizing();

    }



    private void sizing(){
        root.setPrefSize(width, height);
        xpContainer.setPrefSize(width, model.getHealth().getPrefHeight());
        xpLabel.setPrefSize(width, model.getHpDisplay().getPrefHeight());
        xpRectangle.setHeight(model.getHpBar().getHeight());
        xpRectangle.setWidth(xpContainer.getPrefWidth() * model.getCharacter().xpPercent);
        if (model.canLevel())
            xpRectangle.setWidth(xpContainer.getPrefWidth());

    }

    public PlayerCard cloneObj(){
        return new PlayerCard(this.model.cloneObj());
    }

}