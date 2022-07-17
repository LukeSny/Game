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

import something.Character;
import something.CharacterModel;
import something.battleScene.Grid;

import java.util.ArrayList;

public abstract class Discipline {

    public String name;
    //scalar damage multiplier, eg 1.0 will do 100% of character's strength + wpn damage
    public double damageMod;
    //number of tiles that can be moved for 1 action point
    public int moveDist;
    //diagonal number of tiles something can attack
    public int range;
    //class bonus to hit chance
    public int hitChance;
    //class bonus to dodge
    public int dodgeChance;
    //ImageView for this class
    public String imageURL;
    //cost this class has to do a basic attack
    public int attackActionCost;
    //abilities for this class
    public ArrayList<Ability> abilities;

    public PerkTree perkTree;


    public Discipline(double dmg, int range, int moveDist, int hit, int dodge, int actionCost, String url){
        this.damageMod = dmg;
        this.range = range;
        this.moveDist = moveDist;
        attackActionCost = actionCost;
        hitChance = hit;
        dodgeChance = dodge;
        imageURL = url;
        abilities = new ArrayList<>();
    }

    //never ever use the default clone!!
    public Discipline cloneObj(){
        return null;
    }
}