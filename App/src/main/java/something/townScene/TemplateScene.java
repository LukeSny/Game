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

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import something.Party;
import something.worldScene.World;

public abstract class TemplateScene {
    public Party party;
    public Stage stage;
    public VBox root;
    public Button returnButton;
    public TownHub townHub;
    /*this is the scene for the town not this scene*/
    Scene townScene;
    /*scene for this thingy*/
    //Scene thisScene;

    public TemplateScene(TownHub townHub, World world){
        this.townHub = townHub;
        party = world.party.party;
        stage = world.primaryStage;
        root = new VBox();
//        root.setMinWidth(Runnable.SCREEN_SIZE);
//        root.setMinHeight(Runnable.SCREEN_SIZE);
        returnButton = new Button("return to town [q]");
        returnButton.setOnAction(c -> displayTownHub());
        root.getChildren().add(returnButton);

        root.setOnKeyPressed(c -> {
            if (c.getCode() == KeyCode.Q){
                displayTownHub();
            }
        });
    }

    public void displayTownHub(){
        System.out.println("moving to hub");
        stage.getScene().setRoot(townHub.root);
    }
}