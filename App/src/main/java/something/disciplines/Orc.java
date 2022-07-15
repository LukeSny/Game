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

package something.disciplines;

import something.CharacterModel;
import something.battleScene.Grid;

public class Orc extends Discipline{
    public Orc(){
        super(.5,1, 3, 5, 15, 3, "models/orc.jpg");
        name = "Orc";
    }



    public Orc cloneObj(){
        return new Orc();
    }
}