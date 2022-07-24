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

import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import something.worldScene.World;

public class TownHub {

    private static final int NUM_BUILDINGS = 4;

    public AnchorPane root;
    TilePane buildingHolder;
    World world;

    ShopScene shopScene;
    CraftScene craftScene;
    LevelUpScene levelUpScene;
    RecruitScene recruitScene;

    public TownHub(World wo){
        world = wo;
        root = new AnchorPane();
        buildingHolder = new TilePane();

        shopScene = new ShopScene(this, world);
        craftScene = new CraftScene(this, world);
        recruitScene = new RecruitScene(this, world);

        initHolder();
        //keyBinds();
    }

    private void initHolder(){

        ImageView shop = createImage("places/blackSmithShop.jpg");
        shop.setOnMouseClicked(c -> shopTransition());

        ImageView level = createImage("places/scroll.png");
        level.setOnMouseClicked(c -> levelTransition());

        ImageView craft = createImage("places/craftBench.jpg");
        craft.setOnMouseClicked(c -> craftTransition());

        ImageView recruit = createImage("misc/crowd.jpg");
        recruit.setOnMouseClicked(c -> recruitTransition());

        ImageView leave = makeExit("places/forest.jpg");
        leave.setOnMouseClicked(c -> leaveTown());

        //buildingHolder.setTranslateY(world.height - (2 * world.height / NUM_BUILDINGS));
        //buildingHolder.setTranslateY(world.height / 2);

        root.getChildren().addAll(buildingHolder, leave);
    }

    public void keyBinds(){
        world.primaryStage.getScene().setOnKeyReleased(c -> {
            System.out.println("detected key press in town");
            if (c.getCode() == KeyCode.DIGIT1)
                shopTransition();
            else if (c.getCode() == KeyCode.DIGIT2)
                levelTransition();
            else if (c.getCode() == KeyCode.DIGIT3)
                craftTransition();
            else if (c.getCode() == KeyCode.DIGIT4)
                recruitTransition();
            else if (c.getCode() == KeyCode.DIGIT5)
                leaveTown();
        });
    }

    private void shopTransition(){
        world.primaryStage.getScene().setRoot(shopScene.root);
    }
    private void levelTransition(){
        levelUpScene = new LevelUpScene(this, world);
        world.primaryStage.getScene().setRoot(levelUpScene.root);
    }
    private void craftTransition(){
        world.primaryStage.getScene().setRoot(craftScene.root);
    }
    private void recruitTransition(){
        recruitScene.generateRecruits();
        world.primaryStage.getScene().setRoot(recruitScene.root);
    }
    public void leaveTown(){
        world.enterWorld();
        world.leave(world.townCard);
    }

    private ImageView createImage(String url){
        ImageView image = new ImageView(url);
        image.setFitWidth(world.width / NUM_BUILDINGS);
        image.setFitHeight(world.width / NUM_BUILDINGS);
        buildingHolder.getChildren().add(image);
        return image;
    }

    private ImageView makeExit(String url){
        ImageView image = new ImageView(url);
        int imageWidth = world.width / NUM_BUILDINGS;
        int imageHeight = world.height / NUM_BUILDINGS;
        image.setFitWidth(imageWidth);
        image.setFitHeight(imageHeight);
        image.setTranslateX(world.width / 2 - imageWidth);
        image.setTranslateY(world.height - imageHeight);
        return image;
    }


}