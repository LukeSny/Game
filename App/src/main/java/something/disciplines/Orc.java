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
import something.battleScene.Grid;

import java.util.function.BiConsumer;

public class Orc extends Discipline{
    public Orc(){
        super(.5,1, 1, 5, 15, 3, "models/orc.jpg");
        name = "Orc";

        abilities.add(Ability.bigSmack);
    }



    public Orc cloneObj(){
        Orc out = new Orc();
        out.abilities.addAll(abilities);
        return out;
    }
}