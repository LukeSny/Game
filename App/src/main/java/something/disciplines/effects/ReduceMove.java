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
    int amount;
    boolean hasTripped;
    int orginalMove;

    public ReduceMove(String name, String image, CharacterModel mod, int time, int amt){
        super(mod, time, name, image);
        amount = amt;
    }

    @Override
    public void activate() {
        if (hasTripped){
            checkTimer();
        }
        else{
            orginalMove = model.moveDist();
            model.setMoveDist(orginalMove - amount);
            hasTripped = true;
        }
    }

    public void remove(){
        model.getEffects().remove(this);
        model.setMoveDist(orginalMove);
    }
}