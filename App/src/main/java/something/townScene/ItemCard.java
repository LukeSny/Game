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
import javafx.scene.layout.VBox;
import something.Item;
import something.Runnable;

@SuppressWarnings("ALL")
public class ItemCard {
    private static final int LABEL_SIZE = 10;

    VBox root;
    Label nameLabel;
    ImageView image;
    Item item;

    public ItemCard(Item itm){

        root = new VBox();
        nameLabel= new Label(itm.name);
        image = itm.image;
        item = itm;

        root.getChildren().addAll(nameLabel, image);
        sizing();
    }

    private void sizing(){


        root.setPrefWidth(Runnable.SCREEN_SIZE / Runnable.ROW_SIZE);
        root.setPrefHeight(Runnable.SCREEN_SIZE / Runnable.ROW_SIZE  + (LABEL_SIZE));

        image.setFitWidth(root.getPrefWidth());
        image.setFitHeight(root.getPrefHeight() - (LABEL_SIZE));
        System.out.println(root.getPrefWidth());
        //System.out.println("image heigh: " + root.getPrefHeight() + " : " + (LABEL_SIZE*2));

        nameLabel.setPrefSize(root.getPrefWidth(), LABEL_SIZE);

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

    public Item getItem() {
        return item;
    }

    public ItemCard cloneObj(){
       Item temp = new Item(item.name, item.price, item.image.getImage().getUrl(), item.description);
       return new ItemCard(temp);
    }
}