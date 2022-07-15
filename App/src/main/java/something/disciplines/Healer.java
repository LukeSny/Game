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
import something.disciplines.effects.HOT;

import java.util.function.BiConsumer;


public class Healer extends Discipline{
    public Healer(){
        super(.1,1, 1, 5, 20, 3, "models/healer.png");
        name = "Healer";

        abilities.add(Ability.heal);
        abilities.add(Ability.regen);
    }



    public Healer cloneObj(){
        Healer out = new Healer();
        out.abilities.addAll(abilities);
        return out;
    }

}