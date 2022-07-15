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

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import something.Runnable;

import java.io.Serializable;
import java.util.ArrayList;

public class Panel implements Serializable {

    public AnchorPane root;
    public ArrayList<EnemyWorldModel> enemies;
    public ArrayList<PlaceWorldModel> places;
    public int row;
    public int col;
    public boolean hasCamp;


    public Panel(int row, int col){
        this.row = row;
        this.col = col;
        root = new AnchorPane();
        enemies = new ArrayList<>();
        places = new ArrayList<>();
        root.setMinWidth(Runnable.SCREEN_SIZE);
        root.setMinHeight(Runnable.SCREEN_SIZE);
        hasCamp = false;
    }

    public Panel(ArrayList<EnemyWorldModel> enemies, ArrayList<PlaceWorldModel> places, int row, int col) {
        root = new AnchorPane();
        root.setMinWidth(Runnable.SCREEN_SIZE);
        root.setMinHeight(Runnable.SCREEN_SIZE);
        this.enemies = enemies;
        this.places = places;
        this.row = row;
        this.col = col;
        hasCamp = checkForCamp();

    }

    public String panelCoords(){
        return " " + row + " | " + col;
    }

    private boolean checkForCamp(){
        for (PlaceWorldModel place : places){
            if (place.encounter.isTown) continue;
            if (place.encounter.makerUrl.equals("banditCamp.txt"));
                return true;
        }
        return false;
    }

}