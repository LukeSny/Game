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

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import something.Party;
import something.Runnable;
import something.townScene.ItemCard;

import java.util.ArrayList;
import java.util.Random;

public class TextScreen {

    VBox root;
    ImageView image;
    Label title;
    Label text;
    public Button option1;
    public Button option2;
    public VBox container;
    PlaceWorldModel place;
    Encounter encounter;
    ArrayList<ItemCard> loot;
    int goldReward;
    Party party;
    Panel panel;

    public TextScreen(String titl, String url, String tex, String button1, PlaceWorldModel plac, World world, Encounter en){
        root = new VBox();
        party = world.party.party;
        panel = world.currentPanel;
        world.stylePopUp(root);
        title = new Label(titl);
        System.out.println("url gang: " + url);
        image = new ImageView(url);
        text = new Label(tex);
        place = plac;
        encounter = en;
        container = new VBox();
        option1 = new Button(button1);
        option1.setOnAction(c -> {
            System.out.println("option1 clicked");
            if (encounter.index == encounter.sequence.length()) {
                removeSelf();
                world.currentPanel.places.remove(place);
                encounter.panelRoot.getChildren().remove(place.root);
                party.addGold(goldReward);
                if (loot != null){
                    System.out.println("adding after exit");
                    loot.forEach(e -> party.addItem(e));
                }
                world.leave(place);
            }
            else {
                removeSelf();
                encounter.next();
            }
        });

        root.getChildren().addAll(title, image, text, container, option1);
    }

    public void setText(String tex){
        text.setText(tex);
    }

    public void removeSelf(){
        encounter.panelRoot.getChildren().remove(root);
    }

    public void display(){
        encounter.panelRoot.getChildren().add(root);
    }

    public void addOption2(String text){
        option2 = new Button(text);
        root.getChildren().add(option2);
    }

    public void addLoot(ArrayList<ItemCard> items){
        HBox displayLoot = new HBox();
        loot = items;
        for (ItemCard thing: items){
            displayLoot.getChildren().add(thing.getRoot());
        }
        container.getChildren().add(displayLoot);
    }

    public void addGold(int goldLow, int goldHigh){
        Random rand = new Random();
        int gold = rand.nextInt(goldLow, goldHigh);
        goldReward = gold;
        Label goldDisplay = new Label("Gold earned: " + gold);
        container.getChildren().add(goldDisplay);
    }

}