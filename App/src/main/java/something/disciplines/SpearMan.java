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

public class SpearMan extends Discipline {

    public SpearMan(){
        super(.7,2, 3, 10, 5, 3, "poop.jpg");
        name = "SpearMan";
    }

    @Override

    public SpearMan cloneObj(){
        return new SpearMan();
    }

}