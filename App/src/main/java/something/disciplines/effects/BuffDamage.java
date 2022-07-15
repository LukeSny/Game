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

    int scalar;
    boolean hasTripped;
    double originalDamage;

    public BuffDamage(String name, String url, CharacterModel mod, int time, int multiple){
        super(mod, time, name, url);
        scalar = multiple;
    }

    @Override
    public void activate() {
        if (hasTripped){
            checkTimer();
        }
        else{
            originalDamage = model.damageMod();
            model.setDamageMod(originalDamage * scalar);
            hasTripped = true;
        }
    }

    public void remove(){
        model.getEffects().remove(this);
        model.setDamageMod(originalDamage);
    }
}