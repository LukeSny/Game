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

package something.disciplines;

import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import something.Character;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Perk {

    public String name;
    public boolean activated;
    public ImageView activeImage;
    public ImageView grayImage;
    String description;
    public Consumer<Character> action;
    public VBox root;
    ArrayList<Perk> unlocks;
    Perk requirement;

    public Perk(String nam, String url,  String descr, Perk req, Consumer<Character> act){
        name = nam;
        activeImage = new ImageView(url);
        activeImage.setFitHeight(50);
        activeImage.setFitWidth(50);
        grayImage = new ImageView(url);
        grayImage.setFitHeight(50);
        grayImage.setFitWidth(50);
        description = descr;
        action = act;
        root = new VBox();
        unlocks = new ArrayList<>();

        ColorAdjust grayScale = new ColorAdjust();
        grayScale.setBrightness(-.5);
        grayImage.setEffect(grayScale);

        req.unlocks.add(this);
        requirement = req;

    }

    public Perk(String nam, String url,  String descr, Consumer<Character> act){
        name = nam;
        activeImage = new ImageView(url);
        activeImage.setFitHeight(50);
        activeImage.setFitWidth(50);
        grayImage = new ImageView(url);
        grayImage.setFitHeight(50);
        grayImage.setFitWidth(50);
        description = descr;
        action = act;
        root = new VBox();
        unlocks = new ArrayList<>();

        ColorAdjust grayScale = new ColorAdjust();
        grayScale.setBrightness(-.5);
        grayImage.setEffect(grayScale);

    }

    public Perk(String nam){
        name = nam;
        unlocks = new ArrayList<>();
        root = new VBox();
        String url = "poop.jpg";
        activeImage = new ImageView(url);
        activeImage.setFitHeight(50);
        activeImage.setFitWidth(50);
        grayImage = new ImageView(url);
        grayImage.setFitHeight(50);
        grayImage.setFitWidth(50);
    }

    public void activate(Character self){
        if (activated) return;
        System.out.println("requirement: " + requirement);
        if (requirement == null || requirement.activated) {
            //System.out.println("req activated? " + requirement.activated);
            activated = true;
            self.skillPoint--;
            action.accept(self);
        }
    }
    private ImageView getProperImage(){
        if (activated)
            return activeImage;
        return grayImage;
    }

    public VBox getPerkImage(){
        root.getChildren().clear();
        Label text = new Label(name);
        root.getChildren().addAll(getProperImage(), text);
        root.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        return root;
    }
    public void setUnlock(Perk perk){
        unlocks.add(perk);
    }

    public void setPreReq(Perk perk){
        perk.unlocks.add(this);
    }

}