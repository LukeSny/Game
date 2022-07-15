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

public class BuffDefense extends Effect{
    int addition;
    boolean hasTripped;
    double originalDefense;

    public BuffDefense(String name, String url, CharacterModel mod, int time, int adder){
        super(mod, time, name, url);
        addition = adder;
        if (addition == 0)
            addition = 5;
        description = "buff defense by " + addition;
    }

    @Override
    public void activate() {
        if (hasTripped){
            checkTimer();
        }
        else{
            originalDefense = model.getCharacter().extraDef;
            model.getCharacter().extraDef += addition;
            hasTripped = true;
        }
    }

    public void remove(){
        model.getEffects().remove(this);
        model.getCharacter().extraDef -= addition;
    }
}