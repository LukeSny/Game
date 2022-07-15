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

package something.worldScene;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import something.CharacterModel;
import something.Party;

import java.io.Serializable;
import java.util.Stack;

public class WorldModel implements Serializable {
    private final int IMAGE_SIZE = 50;
    public static final int HEIGHT = 60;
    public static final int WIDTH = 50;
    public StackPane root;
    public VBox container;

    public Label nameLabel;
    public ImageView image;
    double x;
    double y;

    Rectangle collisionBox;

    public WorldModel(){
        root = new StackPane();
        container = new VBox();
        nameLabel = new Label();
        image = new ImageView();
        image.setPreserveRatio(false);

        nameLabel.setPrefSize(WIDTH, HEIGHT- WIDTH);
        image.setFitWidth(WIDTH);
        image.setFitHeight(HEIGHT);

        collisionBox = new Rectangle(WIDTH, HEIGHT);

        root.translateXProperty().addListener(c -> collisionBox.setTranslateX(root.getTranslateX()));
        root.translateYProperty().addListener(c -> collisionBox.setTranslateY(root.getTranslateY()));


        container.getChildren().addAll(nameLabel, image);
        root.getChildren().addAll(container);
    }

    public void setImage(String url){
        image.setImage(new Image(url));
    }



}