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

package something.disciplines;

import something.CharacterModel;

import java.util.function.BiConsumer;

public class Ability {
    public String name;
    public int abilityRange;
    //current amount of turns to wait
    public int abilityTimer;
    //turns to wait right after using ability
    public int abilityRefresh;
    public boolean selfBuff;
    public boolean targetGrid;
    public String imageURL;
    public int apCost;

    public BiConsumer<CharacterModel, CharacterModel> action;

    public Ability(String name, int range, int apCost, int refreshTime, boolean self, boolean grid, String imageURL, BiConsumer<CharacterModel, CharacterModel> thingy){
        this.name = name;
        abilityRange = range;
        abilityRefresh = refreshTime;
        abilityTimer = 0;
        selfBuff = self;
        targetGrid = grid;
        action = thingy;
        this.apCost = apCost;
        this.imageURL = imageURL;
    }

    public void abilityAction(CharacterModel self, CharacterModel other){
        action.accept(self, other);
        abilityTimer = abilityRefresh;
    }

    public void reduceTimer(){
        abilityTimer--;
        if (abilityTimer < 0)
            abilityTimer = 1;
    }
}