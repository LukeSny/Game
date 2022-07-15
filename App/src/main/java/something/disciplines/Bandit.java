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

public class Bandit extends Discipline{
    public Bandit(){
        super(.5,3, 3, 5, 15, 3, "models/bandit.png");
        name = "Bandit";
    }



    public Bandit cloneObj(){
        return new Bandit();
    }
}