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

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import something.Armor;
import something.Creator;
import something.Weapon;
import something.worldScene.World;

import java.util.ArrayList;

public class ShopScene extends TemplateScene{

    ImageView topImage;
    TilePane itemHolder;
    ArrayList<ItemCard> items;
    HBox itemDescription;
    Label showGold;

    public ShopScene(TownHub hub, World world){
        super(hub, world);
        System.out.println("constructed shop");
        items = new ArrayList<>();
        itemHolder= new TilePane();
        topImage = new ImageView("places/blackSmithShop.jpg");
        showGold = new Label("Current gold: " + party.getGold());
        party.goldProperty().addListener(c -> showGold.setText("Current gold: " +party.getGold()));
        itemDescription = new HBox();
        topImage.setFitWidth(150); topImage.setFitHeight(150);
        root.getChildren().add(topImage);
        root.getChildren().add(showGold);
        root.getChildren().add(itemDescription);
        root.getChildren().add(itemHolder);


        for(Weapon weapon: Creator.createWeapons()){
            ItemCard card = new ItemCard(weapon);
            items.add(card);
            itemHolder.getChildren().add(card.root);
        }
        for(Armor armor : Creator.createArmor()){
            ItemCard card = new ItemCard(armor);
            items.add(card);
            itemHolder.getChildren().add(card.root);
        }

        initBindings();
    }

    public void initBindings(){
        for (ItemCard card : items){
            card.root.hoverProperty().addListener(c -> {
                itemDescription.getChildren().clear();
                //itemDescription.getChildren().add(itemCard.getImage());
                String text = card.item.description + "\n" + "Price: " + card.item.price;
                if (card.item instanceof Weapon) {
                    Weapon temp = (Weapon) card.item;
                    text += "\nStrength: " + temp.damage;
                }
                Label lbl = new Label(text);
                lbl.setWrapText(true);
                itemDescription.getChildren().add(lbl);
            });
            card.root.setOnMouseClicked(c -> {
                System.out.println("click");
                if (c.getButton() == MouseButton.SECONDARY){
                    System.out.println("right click found");
                    //if they have enough gold, move item to their inv, sub gold, and then remove the onClick event
                    if(party.getGold() >= card.item.price){
                        party.subGold(card.item.price);
                        party.addItem(card);
                        items.remove(card);
                        placeItems();
                        System.out.println("sold: " + card.item.name);
                        card.root.setOnMouseClicked(null);
                    }
                }
            });
        }
    }

    public void placeItems(){
        itemHolder.getChildren().clear();
        for (ItemCard item : items){
            itemHolder.getChildren().add(item.root);
        }
    }

}