package something.townScene;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import something.*;
import something.Runnable;
import something.disciplines.Ranger;
import something.disciplines.Warrior;
import something.worldScene.World;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TownView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinHeight(Runnable.SCREEN_SIZE);
        primaryStage.setMinWidth(Runnable.SCREEN_SIZE);
        primaryStage.setAlwaysOnTop(true);
        ArrayList<PlayerModel> players = new ArrayList<>();
        something.Character character = new something.Character("Homie", new Warrior());
        ArrayList<Weapon> weapons= Creator.createWeapons();
        character.weapon = weapons.get(0);
        PlayerModel model = new PlayerModel(character,1,2);
        players.add(model);

        something.Character character2 = new something.Character("Reggi", new Ranger());
        character2.giveXp(150);
        PlayerModel model2 = new PlayerModel(character2,1,3);
        players.add(model2);


        Party party = new Party(players, "The Boys");
        party.setGold(10000);

        World world = Creator.createWorld(primaryStage);
        world.stillMoving = false;
        world.party.root.setTranslateX(50);
        world.party.root.setTranslateY(50);
        //Scene scene = world.town.scene;
        primaryStage.setTitle("PoopTown");
        //primaryStage.setScene(scene);

        primaryStage.show();
    }
}
