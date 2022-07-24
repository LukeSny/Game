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

    public DOT(String name, String image, CharacterModel mod, int time, int dam){
        super(mod, time, name, image, dam);
        description = "take " + effect + " per turn";
        type = EffectType.DOT;
    }

    public DOT(Effect ef){
        super(ef.model, ef.timer, ef.name, ef.imageURL, ef.effect);
        description = "take " + effect + " per turn";
        type = EffectType.DOT;
    }

    public void activate(){
        model.getCharacter().takeDamage(effect);
        checkTimer();
    }
}