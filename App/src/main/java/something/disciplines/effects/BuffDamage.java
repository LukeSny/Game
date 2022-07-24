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

public class BuffDamage extends Effect{
    /* effect should be treated as scalar / 100 because it has to be int to be saved*/
    boolean hasTripped;
    double originalDamage;

    public BuffDamage(String name, String url, CharacterModel mod, int time, int multiple){
        super(mod, time, name, url, multiple);
        description = "damage multiplied by " + (double) effect / 10;
        type = EffectType.buffDamage;
    }
    public BuffDamage(Effect ef){
        super(ef.model, ef.timer, ef.name, ef.imageURL, ef.effect);
        description = "damage multiplied by " + (double) effect / 10;
        type = EffectType.buffDamage;
    }

    @Override
    public void activate() {
        if (hasTripped){
            checkTimer();
        }
        else{
            originalDamage = model.damageMod();
            double scalar = (double) effect / 10;
            model.setDamageMod(originalDamage * scalar);
            hasTripped = true;
        }
    }

    public void remove(){
        model.getEffects().remove(this);
        model.setDamageMod(originalDamage);
    }
}