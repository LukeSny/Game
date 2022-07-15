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

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import something.Creator;
import something.EnemyModel;
import something.townScene.ItemCard;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class EnemyWorldModel extends WorldModel{

    ArrayList<EnemyModel> enemies;
    public ArrayList<ItemCard> lootDrop;
    public int xpReward;
    public int difficulty;
    public String imageUrl;

    public EnemyWorldModel(ArrayList<EnemyModel> ene, String name, String url, int dif){
        super();
        enemies = ene;
        if (hasNumber(name))
            nameLabel.setText(name);
        else
            nameLabel.setText(name + "(" + enemies.size() + ")");
        image.setImage(new Image(url));
        difficulty = dif;
        xpReward = enemies.size() * difficulty * 5;
        lootDrop = new ArrayList<>();
        imageUrl = url;
        root.setId("enemy world model");

        generateLoot();
    }

    /**
     * only use this constructor for fights not on the overworld
     * @param ene list of enemies in the fight
     * @param dif difficult rating
     */
    public EnemyWorldModel(ArrayList<EnemyModel> ene, int dif){
        super();
        enemies = ene;
        difficulty = dif;
        xpReward = enemies.size() * difficulty * 5;
        lootDrop = new ArrayList<>();

        generateLoot();
    }

    private void generateLoot(){
        Random rand = new Random();
        for (ItemCard item: Creator.createListOfLootItems()){
            int num = rand.nextInt(10);
            if (num == 1)
                lootDrop.add(item);
        }
    }

    public ArrayList<EnemyModel> getEnemies() {
        return enemies;
    }

    public ArrayList<ItemCard> getLootDrop() {
        return lootDrop;
    }

    public int getXpReward() {
        return xpReward;
    }

    public int getDifficulty() {
        return difficulty;
    }


    private boolean hasNumber(String name){
        for (char let : name.toCharArray()){
            if (Character.isDigit(let))
                return true;
        }
        return false;
    }
}