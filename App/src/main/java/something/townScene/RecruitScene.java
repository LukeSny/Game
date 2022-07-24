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

import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import something.Character;
import something.Creator;
import something.Runnable;
import something.worldScene.World;

import java.util.ArrayList;
import java.util.Random;

import static something.PlayerModel.nextYOpen;

public class RecruitScene extends TemplateScene{

    Label title;
    HBox container;
    HBox descriptionBox;
    ArrayList<PlayerCard> recruits;

    Random rand = new Random();

    public RecruitScene(TownHub hub, World world){
        super(hub, world);
        recruits = new ArrayList<>();
        container = new HBox();
        descriptionBox = new HBox();
        title = new Label("Recruit People");
        root.getChildren().addAll(title, descriptionBox, container);
        generateRecruits();
    }



    public void generateRecruits(){
        int numRecruits = rand.nextInt(1, 6);
        container.getChildren().clear();
        recruits.clear();
        for (int i = 0; i < numRecruits; i++) {
            PlayerCard card = new PlayerCard(Creator.createRandomPlayer());
            recruits.add(card);
            container.getChildren().add(card.root);
        }
        System.out.println("rand: " + numRecruits + " size: " + recruits.size());
        initBindings();
    }

    public void initBindings(){
        for (PlayerCard card: recruits){
            card.root.hoverProperty().addListener(c -> {
                descriptionBox.getChildren().clear();
                Character ch = card.model.getCharacter();
                String text = ch.name + "\nHP: " + ch.maxXp +"\nStrength: " + ch.strength + "\nClass: " + ch.discipline.name;
                Label lbl = new Label(text);
                lbl.setWrapText(true);
                descriptionBox.getChildren().add(lbl);
            });
            card.root.setOnMouseClicked(c -> {
                System.out.println("click");
                if (c.getButton() == MouseButton.SECONDARY){
                    System.out.println("right click found");
                    party.addModel(card.model);
                    recruits.remove(card);

                    int y = nextYOpen % Runnable.NUM_COLS;
                    int x = 0;
                    if (nextYOpen > Runnable.NUM_COLS)
                        x =1;
                    card.model.setX(x);
                    card.model.setY(y);
                    party.getSavedSlots()[x][y] = card.model;
                    System.out.println("x: " + x + " |y: " + y);
                    nextYOpen++;
                    placeRecruits();
                }
            });
        }
    }

    private void placeRecruits(){
        container.getChildren().clear();
        for (PlayerCard card : recruits)
            container.getChildren().add(card.root);
    }

}