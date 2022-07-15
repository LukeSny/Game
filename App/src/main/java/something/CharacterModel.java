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

package something;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import something.disciplines.effects.Effect;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * class representing a model in the game
 * shows the name, class image, and health of the underlying character
 * dont ask me why bar size is 63
 */
@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public abstract class CharacterModel implements Serializable {

    public final int MAX_BAR_SIZE = 63;

    Character character;
    ImageView image;
    VBox root;
    Label nameLabel;
    StackPane health;
    Label hpDisplay;
    Rectangle hpBar;
    ArrayList<Effect> effects;

    int x; int y;

    /**
     * constructor does a lot
     * takes in a player and where that player is positioned
     * constructs health display to represent the % of health
     *
     * @param character the Character that this model visualizes
     * @param x x coord of model
     * @param y y coord of model
     */
    public CharacterModel(Character character, int x, int y){
        this.character = character;
        this.image = character.image;
        effects = new ArrayList<>();

        root = new VBox();
        root.setId("modelBackground");

        this.x = x; this.y = y;

        nameLabel = new Label(character.name);
        nameLabel.setId("nameLabel");

        health = new StackPane();
        health.setId("healthPane");
        health.setBackground(new Background(new BackgroundFill(Color.RED,
                CornerRadii.EMPTY,
                Insets.EMPTY)));

        hpDisplay = new Label(character.hp.getValue() + "/" + character.maxHp);
        hpDisplay.setId("hpDisplay");
        character.hp.addListener(c -> hpDisplay.setText(character.hp.getValue() + "/" + character.maxHp));


        hpBar = new Rectangle();
        hpBar.setId("hpBar");
        character.hp.addListener(c -> hpBar.widthProperty().setValue(MAX_BAR_SIZE * character.healthPercent));
        hpBar.setFill(Color.GREEN);

        placement();
    }

    public CharacterModel(int x, int y){
        effects = new ArrayList<>();

        this.character = new Character();
        this.image = character.image;
        root = new VBox();
        root.setId("modelBackground");
        this.x = x; this.y = y;

        nameLabel = new Label(character.name);
        nameLabel.setId("nameLabel");

        health = new StackPane();
        health.setId("healthPane");
        health.setBackground(new Background(new BackgroundFill(Color.RED,
                CornerRadii.EMPTY,
                Insets.EMPTY)));

        hpDisplay = new Label(character.hp.getValue() + "/" + character.maxHp);
        hpDisplay.setId("hpDisplay");
        character.hp.addListener(c -> hpDisplay.setText(character.hp.getValue() + "/" + character.maxHp));

        hpBar = new Rectangle();
        hpBar.setId("hpBar");
        hpBar.setFill(Color.GREEN);
        character.hp.addListener(c -> hpBar.widthProperty().setValue(root.getPrefWidth() * character.healthPercent));

        placement();
    }



    public void placement(){
        root.getChildren().add(nameLabel);
        root.getChildren().add(image);
        root.getChildren().add(health);

        health.getChildren().add(hpBar);
        health.getChildren().add(hpDisplay);
    }
    public void sizingForMenu(int height){
        System.out.println("running menu sizing");
        double totalHeight = height * .15;
        double totalWidth = height * .1;
        root.setPrefSize(totalWidth, totalHeight);
        nameLabel.setPrefSize(totalWidth, totalHeight * .5);
        image.setFitWidth(totalWidth); image.setFitHeight(totalWidth);
        health.setPrefSize(totalWidth, totalHeight / 3);
        hpBar.setHeight(totalHeight / 3);
    }
    public void defaultSizing() {
        this.image.setFitHeight(root.getPrefHeight() / 2);
        this.image.setFitWidth(root.getPrefWidth());

        nameLabel.setPrefHeight( root.getPrefHeight() /4);
        nameLabel.setPrefWidth(root.getPrefWidth());

        health.setPrefHeight(root.getPrefHeight() /4);
        health.setPrefWidth(root.getPrefWidth());
        health.setMinWidth(root.getPrefWidth());

        hpDisplay.setPrefWidth(root.getPrefWidth() /2);
        hpDisplay.setPrefHeight(health.getPrefHeight());

        hpBar.setHeight(health.getPrefHeight() +1);
        hpBar.setWidth(health.getPrefWidth() +1);
    }

    public int getMAX_BAR_SIZE() {
        return MAX_BAR_SIZE;
    }

    public Character getCharacter() {
        return character;
    }

    public ImageView getImage() {
        return image;
    }

    public VBox getRoot() {
        return root;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public StackPane getHealth() {
        return health;
    }

    public Label getHpDisplay() {
        return hpDisplay;
    }

    public Rectangle getHpBar() {
        return hpBar;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int moveDist(){
        return character.moveDist;
    }
    public void setMoveDist(int num){
        character.moveDist = num;
    }

    public double damageMod(){
        return this.getCharacter().damageMod;
    }
    public void setDamageMod(double num){this.getCharacter().damageMod = num;}
    public int range(){
        return this.getCharacter().discipline.range;
    }

    public void fullHeal(){
        this.getCharacter().hp.set(this.getCharacter().maxHp);
    }
    public void heal(int val){
        this.getCharacter().hp.set(this.getCharacter().hp.getValue() + val);
    }
    public String getName(){
        return this.getCharacter().name;
    }

    public int getDamage(){
        return character.getDamage();
    }

    public boolean hasWeapon(){
        return character.hasWeapon();
    }
    public Weapon getWeapon(){
        return this.character.weapon;
    }
    public void setWeapon(Weapon wpn){
        this.character.weapon = wpn;
    }

    public String printCoords(){
        return " (" + x +"," + y + ") ";
    }

    public boolean hasHelmet(){return this.getCharacter().helmet != null;}
    public Armor getHelmet(){return this.getCharacter().helmet;}
    public void setHelmet(Armor hel){this.getCharacter().helmet = hel;}

    public boolean hasTorso(){return this.getCharacter().torso != null;}
    public Armor getTorso(){return this.getCharacter().torso;}
    public void setTorso(Armor tor){this.getCharacter().torso = tor;}

    public boolean hasLegs(){return this.getCharacter().legs != null;}
    public Armor getLegs(){return this.getCharacter().legs;}
    public void setLegs(Armor leg){this.getCharacter().legs = leg;}

    public int getDefense(){
        int out = character.extraDef;
        if (hasHelmet())
            out+=this.getHelmet().def;
        if (hasTorso())
            out+= this.getTorso().def;
        if (hasLegs())
            out+= this.getLegs().def;
        return out;
    }

    public ArrayList<Effect> getEffects() {
        return effects;
    }


    public void playEffects(){
        effects.forEach(Effect::activate);
    }
    public void addEffect(Effect effect){
        effects.add(effect);
    }

}