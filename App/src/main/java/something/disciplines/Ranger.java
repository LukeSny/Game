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
import something.disciplines.effects.DOT;

public class Ranger extends Discipline {

    public Ranger(){
        super(.5,3, 2, 5, 15, 3, "models/rangerImage.jpg");
        name = "Ranger";
        abilities.add(Ability.doubleTap);
    }

    public Ranger cloneObj(){
        Ranger out = new Ranger();
        out.abilities.addAll(abilities);
        return out;
    }

}