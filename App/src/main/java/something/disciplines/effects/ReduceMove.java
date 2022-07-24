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

public class ReduceMove extends Effect{
    boolean hasTripped;
    int orginalMove;

    public ReduceMove(String name, String image, CharacterModel mod, int time, int amt){
        super(mod, time, name, image, amt);
        description = "reduce move by " + effect;
        type = EffectType.reduceMove;
    }

    public ReduceMove(Effect ef){
        super(ef.model, ef.timer, ef.name, ef.imageURL, ef.effect);
        description = "reduce move by " + effect;
        type = EffectType.reduceMove;
    }

    @Override
    public void activate() {
        if (hasTripped){
            checkTimer();
        }
        else{
            orginalMove = model.moveDist();
            model.setMoveDist(orginalMove - effect);
            hasTripped = true;
        }
    }

    public void remove(){
        model.getEffects().remove(this);
        model.setMoveDist(orginalMove);
    }
}