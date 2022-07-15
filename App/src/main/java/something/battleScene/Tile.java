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

package something.battleScene;

import javafx.scene.shape.Rectangle;
import something.Runnable;

/**
 * super simple class mainly to house information
 * just has a rectangle and the the (x,y) coords representing where it is
 * used in the Grid as the empty tiles
 */
public class Tile {
    public static int BORDERSIZE = 1;
    Rectangle back;
    int x;
    int y;

    public Tile(int x,int y, int width, int height){
        this.x = x; this.y = y;
        back = new Rectangle(width - (BORDERSIZE * 2),height - (BORDERSIZE*2) );
        back.setStyle("-fx-background-color: white; -fx-border-style: solid; -fx-border-width: 1;");
    }

    @Override
    public String toString() {
        return  "(" + x + ", " + y + ")";
    }

    public Rectangle getBack() {
        return back;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}