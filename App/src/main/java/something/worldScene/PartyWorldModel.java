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

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import something.Party;

public class PartyWorldModel extends WorldModel{

    public Party party;
    public String imageUrl;

    public PartyWorldModel(Party par, String url){
        super();
        party = par;
        nameLabel.setText(party.getName());
        image.setImage(new Image(url));
        imageUrl = url;
        root.setId("party world model");
    }

}