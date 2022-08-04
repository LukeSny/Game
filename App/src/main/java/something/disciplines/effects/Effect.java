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

public class Effect {

    public int timer;
    public CharacterModel model;
    public String imageURL;
    public String name;
    public String description;
    public int effect;
    public EffectType type;

    public Effect(CharacterModel mod, int time, String name, String imageURL, int effect){
        model = mod;
        timer = time;
        this.name = name;
        this.imageURL = imageURL;
        this.effect = effect;
    }

    public void activate(){

    }

    void checkTimer(){
        timer--;
    }

    public void remove(){
        model.getEffects().remove(this);
    }

}