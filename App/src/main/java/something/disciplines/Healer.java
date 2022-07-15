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

        Ability heal = new Ability("heal", 2, 2,2, false, false, "ability/heal.png", new BiConsumer<CharacterModel, CharacterModel>() {
            @Override
            public void accept(CharacterModel self, CharacterModel other) {
                other.heal(self.getCharacter().strength);
            }
        });
        Ability regen = new Ability("regen", 3,2, 2, false, false, "ability/HOT.png", new BiConsumer<CharacterModel, CharacterModel>() {
            @Override
            public void accept(CharacterModel self, CharacterModel other) {
                other.addEffect(new HOT("regen", "ability/HOT.png", other, 3, self.getCharacter().strength/2));
            }
        });
        abilities.add(heal);
        abilities.add(regen);
    }



    public Healer cloneObj(){
        return new Healer();
    }

}