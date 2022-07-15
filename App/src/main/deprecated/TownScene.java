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

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import something.Party;
import something.worldScene.World;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TownScene {
    Stage stage;
    Party party;

    VBox root;
    ImageView townImage;
    TilePane sceneContainer;


    VBox shopTile; Button goShop;
    VBox levelTile; Button goLevel;
    VBox craftTile; Button goCraft;

    World world;

    public TownScene(Party pa, Stage st, World world){
        stage = st;
        party = pa;
        root = new VBox();
        this.world = world;
        townImage = new ImageView("places/town.jpg");
        townImage.setFitHeight(150);
        townImage.setFitWidth(300);
        root.getChildren().add(townImage);

        sceneContainer = new TilePane();
        root.getChildren().add(sceneContainer);

        initScene();
        initBindings();
    }

    public void initScene(){

        shopTile = new VBox();
        goShop = new Button("Go to Shop[1]");
        ImageView shopImage = new ImageView("places/blackSmithShop.jpg");
        shopImage.setId("placeImage");
        sizeImage(shopImage);
        shopTile.getChildren().add(shopImage);
        goShop.setOnAction(c -> shopTransition());
        shopTile.getChildren().add(goShop);
        sceneContainer.getChildren().add(shopTile);

        craftTile = new VBox();
        goCraft = new Button("Go to Craft[2]");
        ImageView craftImage = new ImageView("places/craftBench.jpg");
        sizeImage(craftImage);
        craftImage.setId("placeImage");
        craftTile.getChildren().add(craftImage);
        goCraft.setOnAction(c -> craftTransition());
        craftTile.getChildren().add(goCraft);
        sceneContainer.getChildren().add(craftTile);

        levelTile = new VBox();
        goLevel = new Button("Go to Roster[3]");
        ImageView levelImage = new ImageView("places/scroll.png");
        sizeImage(levelImage);
        levelImage.setId("placeImage");
        levelTile.getChildren().add(levelImage);
        goLevel.setOnAction(c -> {
            levelTransition();
        });
        levelTile.getChildren().add(goLevel);
        sceneContainer.getChildren().add(levelTile);

        VBox exitContainer = new VBox();
        ImageView forest = new ImageView("places/forest.jpg");
        sizeImage(forest);

        sceneContainer.getChildren().add(exitContainer);
        Button exit = new Button("Leave Town[4]");
        exitContainer.getChildren().addAll(forest, exit);
        exit.setOnAction(c -> leaveTown());

        VBox recruitContainer = new VBox();
        ImageView crowd = new ImageView("misc/crowd.jpg");
        sizeImage(crowd);
        Button goCrowd = new Button("Go to Recruit[5]");
        goCrowd.setOnAction(c -> recruitTransition());
        recruitContainer.getChildren().addAll(crowd, goCrowd);
        sceneContainer.getChildren().add(recruitContainer);
    }


    public void initBindings(){
        root.setOnKeyPressed(c -> {
            if (c.getCode() == KeyCode.DIGIT1){
                shopTransition();
            }
            else if (c.getCode() == KeyCode.DIGIT2){
                craftTransition();
            }
            else if (c.getCode() == KeyCode.DIGIT3){
                levelTransition();
            }
            else if (c.getCode() == KeyCode.DIGIT4){
                leaveTown();
            }
            else if (c.getCode() == KeyCode.DIGIT5){
                recruitTransition();
            }
        });
    }

    public void sizeImage(ImageView image){
        image.setFitHeight(100);
        image.setFitWidth(90);
    }

    private void shopTransition() {
        ShopScene shop = new ShopScene(this, party, stage);
        stage.getScene().setRoot(shop.root);
        stage.setFullScreen(true);
    }

    private void craftTransition() {
        CraftScene craftScene = new CraftScene(this,party,stage);
        stage.getScene().setRoot(craftScene.root);
        stage.setFullScreen(true);
    }

    private void levelTransition() {
        LevelUpScene levelUpScene = new LevelUpScene(this,party,stage);
        levelUpScene.updateScene();
        stage.getScene().setRoot(levelUpScene.root);
        stage.setFullScreen(true);
    }

    private void recruitTransition(){
        RecruitScene recruitScene = new RecruitScene(this, party,stage);
        stage.getScene().setRoot(recruitScene.root);
        stage.setFullScreen(true);
    }

    public Stage getStage() {
        return stage;
    }

    public Party getParty() {
        return party;
    }

    public VBox getRoot() {
        return root;
    }

    public ImageView getTownImage() {
        return townImage;
    }

    public void leaveTown(){
        world.leave(world.townCard);
        world.enterWorld();

    }
}