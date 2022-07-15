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

import javafx.scene.image.ImageView;

/**
 * simple class to hold data
 * represents the most basic, unspecific item possible
 */
public class Item {
    public String name;
    public int price;
    public ImageView image;
    public String description;
    public String imageUrl;

    public Item(String na, int pr, String imgURL, String des){
        name = na;
        price = pr;
        description = des;
        image = new ImageView(imgURL);
        image.setFitHeight(100);
        image.setFitWidth(100);
        imageUrl = imgURL;
    }


}