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

package something.battleScene;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import something.*;
import something.Runnable;
import something.townScene.ItemCard;

import java.util.ArrayList;

//public class Controller {
//    Grid grid;
//    Scene scene;
//    Stage primaryStage;
//    Scene worldScene;
//
//    boolean attackMode;
//    boolean abilityMode;
//    boolean stillFighting;
//
//    ArrayList<ItemCard> reclaimed;
//
//
//    public Controller(Grid g, Scene sc, Stage stage, Scene world){
//        grid = g;
//        scene = sc;
//        worldScene = world;
//        stillFighting = true;
//        reclaimed = new ArrayList<>();
//        primaryStage = stage;
//
//        attackMode = false;
//        abilityMode = false;
//
//        initBindings();
//
//    }
//
//    public Controller(Grid g, Scene sc){
//        grid = g;
//        scene = sc;
//        stillFighting = true;
//        reclaimed = new ArrayList<>();
//
//        attackMode = false;
//        abilityMode = false;
//
//        initBindings();
//
//    }
//
//    /**
//     * set the bindings for the fight
//     * PlayerModel - if the model is clicked, set that model to be the current selectedModel in the grid
//     * EnemyModel - if the enemy is clicked, be attacked by the selectedModel if the attack is possible
//     * Tile - if the tile is clicked, make that tile the current selectedEmpty
//     * EndTurn - on button click call the endTurn function
//     * Bottom is dedicated to keyBinds
//     * 1 - enter attackMode and highlight area if valid
//     * q - call the endTurn function
//     */
//    public void initBindings(){
//        //all bindings that should happen for player
//        grid.party.getModels().forEach(c -> System.out.println("name: " + c.getCharacter().name));
//        for (PlayerModel model : grid.party.getModels()){
//            model.getRoot().setOnMouseClicked(c -> {
//                if(!stillFighting) return;
//                if (abilityMode) {
//                    grid.selectedModel.get().getCharacter().discipline.ability(grid.selectedModel.get(), model);
//                    abilityMode = false;
//                }
//                else {
//                    grid.selectedModel.set(model);
//                    attackMode = false;
//                }
//            });
//        }
//
//        //all bindings for enemies
//        for (EnemyModel enemy : grid.enemies.getEnemies()){
//            enemy.getRoot().setOnMouseClicked(c -> {
//                if (attackMode && grid.selectedModel.get().getCharacter().canAttack() && attackMode && stillFighting) {
//                    attack(grid.selectedModel.get(), enemy);
//                    attackMode = false;
//                    grid.removeHighlight();
//                }
//                else if (abilityMode && stillFighting){
//                    grid.selectedModel.get().getCharacter().discipline.ability(grid.selectedModel.get(), enemy);
//                }
//            });
//        }
//
//        //all bindings for empty tiles
//        for (int i = 0; i < Grid.GRID_ROWS; i++) {
//            for (int j = 0; j < Grid.GRID_COL; j++) {
//
//                Tile tile = grid.emptyTiles[i][j];
//                tile.back.setOnMouseClicked(c-> {
//                    if(!stillFighting) return;
//                    grid.selectedEmpty.set(tile);
//                });
//            }
//        }
//
//
//        //this section is dedicated to quick bind keys
//
//        scene.setOnKeyReleased(c -> {
//            PlayerModel selected = grid.selectedModel.get();
//            //end turn
//            if (c.getCode() == KeyCode.Q) {
//                if(!stillFighting) return;
//                endRound();
//                attackMode = false;
//            }
//            //attack
//            else if(c.getCode() == KeyCode.DIGIT1) {
//                System.out.println("pressed 1: " + stillFighting + " | " + attackMode);
//                if (!stillFighting) return;
//                if (!attackMode && selected.isCanAttack()) {
//                    grid.highlightAttack();
//                    attackMode = true;
//                    abilityMode = false;
//                } else {
//                    attackMode = false;
//                    grid.removeHighlight();
//                    grid.highlightMove();
//                }
//            }
//            else if(c.getCode() == KeyCode.DIGIT2){
//                    System.out.println("pressed 2 ab|at: "+ abilityMode + " | " + attackMode);
//                    if(!stillFighting) return;
//                    //if its a self buff, just do the thing
//                    if (selected.getCharacter().discipline.selfBuff)
//                        selected.getCharacter().discipline.ability(grid);
//                    else if(!abilityMode) {
//                        abilityMode = true;
//                        attackMode = false;
//                        grid.highLightAbility(selected.getCharacter().discipline.abilityRange);
//                        if (grid.selectedModel.get().getCharacter().discipline.targetGrid)
//                            selected.getCharacter().discipline.ability(grid);
//                    }
//                    else{
//                        abilityMode = false;
//                        grid.removeHighlight();
//                        grid.highlightMove();
//                    }
//            }
//            else if (c.getCode() == KeyCode.DIGIT0)
//                victory();
//        });
//
//    }
//
//
//    /** just returns if the two given models are in touching tiles*/
//    public boolean nextTo(CharacterModel thing1, CharacterModel thing2){
//        boolean xClose = (Math.abs(thing1.getX() - thing2.getX()) < 2);
//        boolean yClose = (Math.abs(thing1.getY() - thing2.getY()) < 2);
//        return xClose && yClose;
//    }
//
//    /**
//     * ends the current round, which involves
//     * having each enemy move and/or attack
//     * resetting each player's canMove and canAttack
//     * clearing all highlights
//     */
//    public void endRound(){
//        if (!stillFighting) return;
//        System.out.println("ending turn");
//        EnemyController enCon = new EnemyController(grid);
//        for (EnemyModel enemy : grid.enemies.getEnemies()){
//            //try and move
//            enCon.enemyMovement(enemy);
//
//            //attack if next to someone
//            for (PlayerModel model : grid.party.getModels()){
//                if(inRange(enemy, model)) {
//                    System.out.println("en: " + enemy.getCharacter().name + " model: " + model.getCharacter().name);
//                    attack(enemy, model);
//                    break;
//                }
//
//            }
//
//        }
//        //activate status effects for everyone at the end of the round
//        for (EnemyModel enemy : grid.enemies.getEnemies()){
//            enemy.playEffects();
//        }
//        for (PlayerModel model : grid.party.getModels()){
//            model.playEffects();
//            model.setCanMove(true);
//            model.setCanAttack(true);
//        }
//
//        //grid.printModelGrid();
//        //grid.printEmptyGrid();
//        grid.highlightMove();
//
//
//    }
//
//    /**
//     * if the attacker is a PlayerModel, the disable it attacking again
//     * if the defender's health goes below 1, remove them from the board and modelTiles
//     * if there are no more enemies, call victory, basically locks everything up
//     * @param attacker the Model that is attacking
//     * @param defender the Model that is getting rekt m8
//     */
//    public TranslateTransition attack(CharacterModel attacker, CharacterModel defender){
//        TranslateTransition attackAni = new TranslateTransition();
//        if(inRange(attacker, defender)) {
//            System.out.println("defended " + defender.getDefense() + " amount of damage");
//            defender.getCharacter().takeDamage(attacker.getCharacter().attack() - defender.getDefense());
//
//            attackAni.setFromX(attacker.getRoot().getTranslateX());
//            attackAni.setFromY(attacker.getRoot().getTranslateY());
//            attackAni.setToX(defender.getRoot().getTranslateX());
//            attackAni.setToY(defender.getRoot().getTranslateY());
//            attackAni.setCycleCount(2);
//            attackAni.setInterpolator(Interpolator.LINEAR);
//            attackAni.setAutoReverse(true);
//            attackAni.setNode(attacker.getRoot());
//            if (attacker instanceof PlayerModel) {
//                ((PlayerModel) attacker).setCanAttack(false);
//                attackAni.play();
//            }
//            if(defender.getCharacter().hp.getValue() <= 0 ) {
//                System.out.println("here here");
//                grid.remove(defender);
//                if (defender instanceof PlayerModel) {
//                    if (defender.hasWeapon()){
//                        reclaimed.add(new ItemCard(defender.getCharacter().weapon));
//                    }
//                    grid.party.getModels().remove(defender);
//                }
//            }
//            if(grid.enemies.getEnemies().isEmpty()){
//                victory();
//            }
//            if(grid.party.getModels().isEmpty()){
//                defeat();
//            }
//        }
//        return attackAni;
//    }
//
//    public void defeat(){
//        VBox back = new VBox();
//        grid.world.stylePopUp(back);
//        back.setAlignment(Pos.CENTER);
//
//        ImageView skull = new ImageView("misc/skull.png");
//
//        Label defeatLabel = new Label("Your soldiers lie dead at the hands of the enemy\nMay they find rest in the dirt");
//        defeatLabel.setWrapText(true);
//        grid.root.getChildren().add(back);
//
//        stillFighting = false;
//        Button restart = new Button("restart");
//        restart.setOnAction(c -> {
//            Creator.worldStart(primaryStage);
//        });
//
//        back.getChildren().addAll(skull, defeatLabel, restart);
//    }
//
//    /**
//     * locks everything up and prevents user from playing after the battle, adds a label indicating victory
//     */
//    public void victory(){
//
//        stillFighting = false;
//        afterBattleUpdate();
//    }
//
//
//    private void afterBattleUpdate(){
//        for (PlayerModel model : grid.party.getModels()){
//            model.getCharacter().giveXp(grid.enemies.getXpReward() / grid.party.getModels().size());
//        }
//
//        VBox back = new VBox();
//        Label title = new Label("Victory!");
//        Label xpGain = new Label("Xp for each fighter: " + grid.enemies.getXpReward() / grid.party.getModels().size());
//        Label itemTitle = new Label("Items looted:");
//        TilePane itemReward = new TilePane();
//
//        Label reclaimedTitle = new Label("Gathered from the fallen:");
//        TilePane reclaimedHolder = new TilePane();
//
//
//        Button exit = new Button("Exit");
//
//        back.setBackground(new Background(new BackgroundFill(Color.WHITE,
//                CornerRadii.EMPTY,
//                Insets.EMPTY)));
//        back.setMaxSize(Runnable.SCREEN_SIZE / 3, Runnable.SCREEN_SIZE / 3);
//        back.setAlignment(Pos.CENTER);
//
//        back.getChildren().addAll(title, xpGain, itemTitle, itemReward);
//        grid.root.getChildren().add(back);
//        if (grid.enemies.getLootDrop().isEmpty())
//            itemTitle.setText("Items Looted: None");
//        for (ItemCard item : grid.enemies.getLootDrop()){
//            grid.party.addItem(item);
//            itemReward.getChildren().add(item.getRoot());
//        }
//        if (!reclaimed.isEmpty()) {
//            for (ItemCard item : reclaimed) {
//                grid.party.addItem(item);
//                reclaimedHolder.getChildren().add(item.getRoot());
//            }
//            back.getChildren().addAll(reclaimedTitle, reclaimedHolder);
//        }
//        back.getChildren().add(exit);
//
//        exit.setOnAction(c -> {
//            System.out.println(worldScene);
//            primaryStage.setScene(worldScene);
//            primaryStage.setFullScreen(true);
//        });
//    }
//
//    public double getDistance(CharacterModel thing1, CharacterModel thing2){
//        int x = thing2.getX() - thing1.getX();
//        int y = thing2.getY() - thing1.getY();
//        return Math.sqrt(x*x + y*y);
//    }
//
//    public double getDistance(CharacterModel thing1, Tile thing2){
//        int x = thing2.getX() - thing1.getX();
//        int y = thing2.getY() - thing1.getY();
//        return Math.sqrt(x*x + y*y);
//    }
//
//    public boolean inRange(CharacterModel attacker, CharacterModel defender){
//        System.out.println("dist: " + getDistance(attacker, defender) + " |range: " + Math.sqrt(attacker.range()*attacker.range() * 2) + .1);
//        System.out.println("attacker x,y: " + attacker.getX() + "|" + attacker.getY());
//        System.out.println("defender x,y: " + defender.getX() + "|" + defender.getY());
//        return getDistance(attacker, defender) < Math.sqrt(attacker.range()*attacker.range() * 2) + .1;
//    }
//
//
//

//}