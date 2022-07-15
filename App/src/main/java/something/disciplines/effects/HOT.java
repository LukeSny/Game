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

package something.disciplines.effects;

import something.CharacterModel;

public class HOT extends Effect {
    int healAmt;

    public HOT(String name, String image, CharacterModel mod, int time, int heal){
        super(mod, time, name, image);
        healAmt = heal;
        description = "heal " + healAmt + " per turn";
    }

    public void activate(){
        model.heal(healAmt);
        checkTimer();
    }

}