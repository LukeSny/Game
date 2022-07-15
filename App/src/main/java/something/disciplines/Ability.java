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
import something.disciplines.effects.BuffDefense;
import something.disciplines.effects.DOT;
import something.disciplines.effects.HOT;

import java.util.function.BiConsumer;

public class Ability {
    public String name;
    public int abilityRange;
    //current amount of turns to wait
    public int abilityTimer;
    //turns to wait right after using ability
    public int abilityRefresh;
    public boolean targetGrid;
    public String imageURL;
    public int apCost;

    public BiConsumer<CharacterModel, CharacterModel> action;

    public Ability(String name, int range, int apCost, int refreshTime, boolean grid, String imageURL, BiConsumer<CharacterModel, CharacterModel> thingy){
        this.name = name;
        abilityRange = range;
        abilityRefresh = refreshTime;
        abilityTimer = 0;
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
        if (abilityTimer == 0) return;
        abilityTimer--;
    }
    public boolean isReady(){
        return abilityTimer == 0;
    }

    public static final Ability poison = new Ability("poison", 1, 2, 3, false, "ability/poison.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.addEffect(new DOT("poison", "ability/poison.png", other, 3, self.getDamage() / 2));
        }
    });

    public static final Ability heal = new Ability("heal", 2, 2,2, false, "ability/heal.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.heal(self.getCharacter().strength);
        }
    });
    public static final Ability regen = new Ability("regen", 3,2, 2, false, "ability/HOT.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.addEffect(new HOT("regen", "ability/HOT.png", other, 3, self.getCharacter().strength/2));
        }
    });
    public static final Ability healSelfImmediate = new Ability("Field Care", 1, 3, 3, false, "ability/heal.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            self.heal(self.getCharacter().hp.get() / 3);
        }
    });
    public static final Ability bigSmack = new Ability("Big Smak", 1, 3, 2, false, "ability/bigSmack.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.takeGuardedDamage(self.getDamage() * 2);
        }
    });
    public static final Ability doubleTap = new Ability("Double Shot", 4, 4, 3, false, "ability/doubleShot.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.takeGuardedDamage(self.getDamage() * 2);
        }
    });
    public static final Ability throwSpear = new Ability("throw spear", 4, 2,4, false, "ability/throwSpear.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.getCharacter().takeDamage(self.getDamage());
        }
    });

    public static final Ability shieldUp = new Ability("Shield Up", 1, 2, 4, false, "ability/buffDefense.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.addEffect(new BuffDefense("buff defense", "ability/buffDefense.png", self, 3, self.getDefense()/2));
        }
    });

}