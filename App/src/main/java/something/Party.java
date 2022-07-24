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

import javafx.beans.property.SimpleIntegerProperty;
import something.battleScene.Grid;
import something.townScene.ItemCard;

import java.io.Serializable;
import java.util.ArrayList;

public class Party implements Serializable {

    ArrayList<PlayerModel> models;
    ArrayList<ItemCard> items;
    SimpleIntegerProperty gold;
    String name;
    PlayerModel savedSlots[][];
    String imageUrl;

    public Party(ArrayList<PlayerModel> mod, String na){
        models = mod;
        gold = new SimpleIntegerProperty(100);
        items = new ArrayList<>();
        name = na;
        savedSlots = new PlayerModel[Grid.ROWS][Grid.COLS];
        for ( PlayerModel model : models){
            savedSlots[model.getX()][model.getY()] = model;
        }
    }

    public Party(ArrayList<PlayerModel> mod, ArrayList<ItemCard> it, String na){
        models = mod;
        gold = new SimpleIntegerProperty(100);
        items = it;
        name = na;
        savedSlots = new PlayerModel[Grid.ROWS][Grid.COLS];
        for ( PlayerModel model : models){
            savedSlots[model.getX()][model.getY()] = model;
        }
    }

    public ArrayList<PlayerModel> getModels() {
        return models;
    }

    public int getGold() {
        return gold.get();
    }

    public SimpleIntegerProperty goldProperty() {
        return gold;
    }

    public void setModels(ArrayList<PlayerModel> models) {
        this.models = models;
    }

    public void setGold(int gold) {
        this.gold.setValue(gold);
    }

    public void addGold(int num) { this.gold.setValue(this.getGold() + num);}

    public void subGold(int num){
        this.gold.setValue(this.getGold() - num);
    }

    public ArrayList<ItemCard> getItems() {
        return items;
    }

    public void addItem(ItemCard card){
        items.add(card);
    }
    public void addItem(Item item){
        items.add(new ItemCard(item));
    }

    public String getName() {
        return name;
    }

    public void addModel(PlayerModel model){
        models.add(model);
    }

    public PlayerModel[][] getSavedSlots() {
        return savedSlots;
    }
}