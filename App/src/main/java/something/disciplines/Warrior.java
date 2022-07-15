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
        Ability throwSpear = new Ability("throw spear", 4, 2,4, false, false, "ability/throwSpear.png", new BiConsumer<CharacterModel, CharacterModel>() {
            @Override
            public void accept(CharacterModel self, CharacterModel other) {
                other.getCharacter().takeDamage(self.getDamage());
            }
        });
        Ability heal = new Ability("heal", 2, 2,4, false, false, "ability/heal.png", new BiConsumer<CharacterModel, CharacterModel>() {
            @Override
            public void accept(CharacterModel self, CharacterModel other) {
                other.heal(self.getCharacter().strength);
            }
        });
        Ability regen = new Ability("regen", 3,2, 4, false, false, "ability/HOT.png", new BiConsumer<CharacterModel, CharacterModel>() {
            @Override
            public void accept(CharacterModel self, CharacterModel other) {
                other.addEffect(new HOT("regen", "ability/HOT.png", other, 3, self.getCharacter().strength/2));
            }
        });
        abilities.add(throwSpear);
        abilities.add(heal);
        abilities.add(regen);
    }

    public Warrior cloneObj(){
        return new Warrior();
    }
}