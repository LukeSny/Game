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

public class DOT extends Effect {
    int damage;

    public DOT(String name, String image, CharacterModel mod, int time, int dam){
        super(mod, time, name, image);
        damage = dam;
        description = "take " + dam + " per turn";
    }

    public void activate(){
        model.getCharacter().takeDamage(damage);
        checkTimer();
    }
}