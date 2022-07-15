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

import javafx.beans.value.ChangeListener;
import something.Character;
import something.CharacterModel;
import something.battleScene.Grid;
import something.disciplines.effects.DOT;
import something.disciplines.effects.Effect;
import something.disciplines.effects.HOT;

import java.util.function.BiConsumer;

public class Warrior extends Discipline {

    public Warrior(){
        super(1,1, 1, 10, 10, 3, "models/warriorImage.png");
        name = "Warrior";

        abilities.add(Ability.shieldUp);
    }

    public Warrior cloneObj(){
        Warrior out = new Warrior();
        out.abilities.addAll(abilities);
        return out;
    }
}