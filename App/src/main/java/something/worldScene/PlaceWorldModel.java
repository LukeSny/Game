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

public class PlaceWorldModel extends WorldModel{

    public Encounter encounter;
    public String imageUrl;

    public PlaceWorldModel(String name, String url){
        super();
        nameLabel.setText(name);
        image.setImage(new Image(url));
        imageUrl = url;
        root.setId("place world model");
    }

    public PlaceWorldModel(){
        super();
        nameLabel.setText("Town");
        image.setImage(new Image("places/town.jpg"));
        imageUrl = "places/town.jpg";
        root.setId("town world model");
        encounter = new Encounter();
    }

    public void addEncounter(Encounter en){
        encounter = en;
    }

}