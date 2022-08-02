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

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import something.battleScene.Grid;
import something.disciplines.*;
import something.townScene.ItemCard;
import something.worldScene.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * formed out of laziness, whenever I want a list of weapons, players, enemies, whatever
 * just make this class and take the arrayList from it
 *
 *
 *
 * ALL NEW ARMOR AND WEAPONS MUUUUUUST BE ADDED TO CREATEARMOR AND CREATEWEAPON OR ELSE SEARCH WONT WORK!!!!!!!!!!!
 * ALSO< NEW DISCIPLINES MUST BE ADDED TO THEIR RESPECTIVE LIST
 */
public class Creator {
    private static int yCounter = 0;
    static Random rand = new Random();

    public static ArrayList<PlayerModel> createPlayers(){
        ArrayList<PlayerModel> mod = new ArrayList<>();
        Character character = new Character("Homie", new Warrior());
        character.setWeapon(createWeapons().get(0));
        character.xp.set(150);
        character.strength = 100;
        PlayerModel model = new PlayerModel(character,1,2);
        mod.add(model);

        Character character2 = new Character("Reggi", new Ranger());
        character2.giveXp(20);
        character2.strength = 100;
        PlayerModel model2 = new PlayerModel(character2,1,3);
        mod.add(model2);
        return mod;
    }

    public static Party createDefaultParty(){
        Party pa = new Party(Creator.createPlayers(), "default party");
        return pa;
    }

    public static ArrayList<EnemyModel> createEnemies(){
        ArrayList<EnemyModel> en = new ArrayList<>();
        Character enemy = new Character("Pop", new Orc());
        EnemyModel enemyModel = new EnemyModel(enemy, 6,6);
        en.add(enemyModel);

        Character enemy2 = new Character("MyChild", new Goblin());
        EnemyModel enemyModel2 = new EnemyModel(enemy2, 7,2);
        en.add(enemyModel2);
        return en;
    }

    public static ArrayList<Weapon> createWeapons(){
        ArrayList<Weapon> wpn= new ArrayList<>();

        Weapon w1 = new Weapon("sword", 100, "items/sword.jpg", 10, null, "Trying to be a " +
                "knight? start with a sword");
        Weapon w2 = new Weapon("rapier", 100, "items/rapier.png", 13, null, "A slender blade" +
                " meant for thrusts");
        Weapon w3 = new Weapon("saber", 100, "items/saber.jpg", 15, null, "A weapon favored" +
                " by the desert Kingdoms");
        Weapon w4 = new Weapon("Black Blade", 200, "items/watchBlade.png", 18, new Warrior(), "One of the " +
                "blades wielded by the Northern Watch. The Gold inscription reads \"Hold the torch of hope high\"");
        wpn.add(w1);
        wpn.add(w2);
        wpn.add(w3);
        wpn.add(w4);
        return wpn;
    }

    public static ArrayList<Armor> createArmor(){
        ArrayList<Armor> out = new ArrayList<>();
        Armor a1 = new Armor("basic helmet", 50, "items/helmet.png", Slot.Helmet, 3, "A pretty standard way of " +
                "keeping your head where it belongs");
        Armor a2 = new Armor("basic plate", 100, "items/breastPlate.jpg", Slot.Torso, 10, "Keeps your insides in " +
                "and outsides out");
        Armor a3 = new Armor("basic grieves", 70, "items/grieves.jpg", Slot.Legs, 7, "A pair of armored pants");
        out.add(a1); out.add(a2); out.add(a3);
        return out;
    }

    public static EnemyWorldModel createWorldEnemies(){
        EnemyWorldModel mod = new EnemyWorldModel(Creator.createEnemies(), "test", "items/sword.jpg", 2);

        return mod;
    }

    public static World createWorld(Stage stage){
        return new World(new PartyWorldModel(Creator.createDefaultParty(), "poop.jpg"), stage, false);
    }

    public static World createWorld(Stage stage, Party part){
        return new World(new PartyWorldModel(part, "poop.jpg"), stage, false);
    }

    public static ArrayList<ItemCard> createListOfLootItems(){
        ArrayList<ItemCard> out = new ArrayList<>();
        for (Weapon wpn : Creator.createWeapons()){
            out.add(new ItemCard(wpn));
        }
        for (Armor arm : Creator.createArmor())
            out.add(new ItemCard(arm));
        return out;
    }

    public static PlayerModel createRandomPlayer(){
        return new PlayerModel();
    }

    public static EnemyWorldModel generateRandomFactionEnemy(){
        int faction = rand.nextInt(3);
        return getEnemyWorldModel(faction, rand);
    }
    public static EnemyWorldModel generateRandomFactionEnemy(int faction){
        return getEnemyWorldModel(faction, rand);
    }

    private static EnemyWorldModel getEnemyWorldModel(int faction, Random rand) {
        int numEnemy = rand.nextInt(2,6);
        EnemyWorldModel model = Creator.createWorldEnemies();
        ArrayList<EnemyModel> list = new ArrayList<>();
        if (faction == 0){
            for (int i = 0; i < numEnemy; i++) {
                EnemyModel en = new EnemyModel(new Character("goblin", new Goblin()), Grid.ROWS - 2, i);
                list.add(en);
            }
            model = new EnemyWorldModel(list, "goblin", "models/goblin.png",numEnemy);
        }
        else if (faction == 1){
            for (int i = 0; i < numEnemy; i++) {
                EnemyModel en = new EnemyModel(new Character("bandit", new Bandit()), Grid.ROWS - 2, i);
                list.add(en);
            }
            model = new EnemyWorldModel(list, "bandit", "models/bandit.png",numEnemy + 2);
        }
        else if (faction == 2){
            for (int i = 0; i < numEnemy; i++) {
                EnemyModel en = new EnemyModel(new Character("orc", new Orc()), Grid.ROWS - 2, i);
                list.add(en);
            }
            model = new EnemyWorldModel(list, "orcs", "models/orc.jpg",numEnemy + 3);
        }
        return model;
    }

    public static ArrayList<EnemyModel> listAllEnemies() {
        ArrayList<EnemyModel> list = new ArrayList<>();
        EnemyModel en1 = new EnemyModel(new Character("goblin", new Goblin()), Grid.ROWS - 2, yCounter % Grid.COLS);
        System.out.println("creating enemy y: " + yCounter % Grid.ROWS);
        list.add(en1); yCounter++;
        EnemyModel en2 = new EnemyModel(new Character("bandit", new Bandit()), Grid.ROWS - 2, yCounter % Grid.COLS);
        System.out.println("creating enemy y: " + yCounter % Grid.ROWS);
        list.add(en2); yCounter++;
        EnemyModel en3 = new EnemyModel(new Character("orc", new Orc()), Grid.ROWS - 2, yCounter % Grid.COLS);
        System.out.println("creating enemy y: " + yCounter % Grid.ROWS);
        list.add(en3); yCounter++;
        return list;
    }

    public static void worldStart(Stage primaryStage){
        Party party = new Party(Creator.createPlayers(), "Bad Boys");
        PartyWorldModel partyWorldModel = new PartyWorldModel(party, "poop.jpg");
        partyWorldModel.root.setTranslateX(50);
        partyWorldModel.root.setTranslateY(50);

        World world = new World(partyWorldModel, primaryStage, false);
        primaryStage.setScene(new Scene(world.currentPanel.root));
    }

    public static PlaceWorldModel createBanditCamp(World world){
        return EncounterMaker.make("banditCamp.txt", world, world.currentPanel.row, world.currentPanel.col);
    }

    public static Node[][] makeMap(double cellSize){
        Node[][] out = new Node[World.PANEL_LENGTH][World.PANEL_LENGTH];
        for (int i = 0; i < World.PANEL_LENGTH; i++) {
            for (int j = 0; j < World.PANEL_LENGTH; j++) {
                Rectangle placeHolder = new Rectangle(cellSize, cellSize);
                placeHolder.setFill(Color.WHITE);
                out[i][j] = placeHolder;
            }
        }
        ImageView town = new ImageView("places/town.jpg");
        sizeImage(cellSize, town);
        out[4][4] = town;
        return out;
    }

    public static ArrayList<Discipline> listPlayerDisciplines(){
        ArrayList<Discipline> list = new ArrayList<>();
        list.add(new Warrior());
        list.add(new Ranger());
        list.add(new SpearMan());
        return list;
    }

    public static ArrayList<Discipline> listEnemyDisciplines() {
        ArrayList<Discipline> list = new ArrayList<>();
        list.add(new Goblin());
        list.add(new Orc());
        list.add(new Bandit());
        return list;
    }

    public static ArrayList<Discipline> listAllDisciplines() {
        ArrayList<Discipline> list = Creator.listPlayerDisciplines();
        list.addAll(Creator.listEnemyDisciplines());
        return list;
    }

    private static void sizeImage(double num, ImageView image){
        image.setFitHeight(num);
        image.setFitWidth(num);
    }



}