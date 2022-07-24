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

    public HOT(String name, String image, CharacterModel mod, int time, int heal){
        super(mod, time, name, image, heal);
        description = "heal " + effect + " per turn";
        type = EffectType.HOT;
    }

    public HOT(Effect ef){
        super(ef.model, ef.timer, ef.name, ef.imageURL, ef.effect);
        description = "heal " + effect + " per turn";
        type = EffectType.HOT;
    }

    public void activate(){
        model.heal(effect);
        checkTimer();
    }

}