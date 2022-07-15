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

public abstract class Effect {

    public int timer;
    public CharacterModel model;
    public String imageURL;
    public String name;
    public String description;

    public Effect(CharacterModel mod, int time, String name, String imageURL){
        model = mod;
        timer = time;
        this.name = name;
        this.imageURL = imageURL;
    }

    public void activate(){

    }

    void checkTimer(){
        if (timer == 0)
            remove();
        timer--;
    }

    void remove(){
        model.getEffects().remove(this);
    }

}