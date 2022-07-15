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

public class Goblin extends Discipline{
    public Goblin(){
        super(.7,1, 4, 10, 20, 3,"models/goblin.png");
        name = "Goblin";
    }



    public Goblin cloneObj(){
        return new Goblin();
    }
}