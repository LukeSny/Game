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
import something.Party;
import something.disciplines.effects.BuffDamage;
import something.disciplines.effects.BuffDefense;
import something.disciplines.effects.DOT;
import something.disciplines.effects.HOT;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
    public AbilityType type;
    public BiConsumer<CharacterModel, CharacterModel> action;
    public Consumer<Party> partyAction;

    public static ArrayList<Ability> fullAbilityList = new ArrayList<>();

    public static ArrayList<Ability> getFullAbilityList() {
        fullAbilityList.add(poison);
        fullAbilityList.add(heal);
        fullAbilityList.add(regen);
        fullAbilityList.add(bigSmack);
        fullAbilityList.add(doubleTap);
        fullAbilityList.add(throwSpear);
        fullAbilityList.add(shieldUp);
        fullAbilityList.add(buffOffense);
        return fullAbilityList;
    }

    /**
     * constructor for a CharacterModel, CharacterModel ability
     */
    public Ability(String name, int range, int apCost, int refreshTime, boolean grid, AbilityType type, String imageURL, BiConsumer<CharacterModel, CharacterModel> thingy){
        generalAssignment(name, range, apCost, refreshTime, grid, type, imageURL);
        action = thingy;
    }

    /**
     * constructor for a Party Wide ability
     */
    public Ability(String name, int range, int apCost, int refreshTime, boolean grid, AbilityType type, String imageURL, Consumer<Party> thingy){
        generalAssignment(name, range, apCost, refreshTime, grid, type, imageURL);
        partyAction = thingy;
    }

    private void generalAssignment(String name, int range, int apCost, int refreshTime, boolean grid, AbilityType type, String imageURL){
        this.name = name;
        abilityRange = range;
        abilityRefresh = refreshTime;
        abilityTimer = 0;
        targetGrid = grid;
        this.apCost = apCost;
        this.type = type;
        this.imageURL = imageURL;
    }

    public void abilityAction(CharacterModel self, CharacterModel other, Party party){
        if (action != null)
            action.accept(self, other);
        else if (partyAction != null)
            partyAction.accept(party);
        abilityTimer = abilityRefresh;
        self.getCharacter().actionPoints -= this.apCost;
    }

    public void reduceTimer(){
        if (abilityTimer == 0) return;
        abilityTimer--;
    }
    public boolean isReady(){
        return abilityTimer == 0;
    }

    public static final Ability poison = new Ability("poison", 1, 2, 3, false, AbilityType.DOT,"ability/poison.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.addEffect(new DOT("poison", "ability/poison.png", other, 3, self.getDamage() / 2));
        }
    });

    public static final Ability heal = new Ability("heal", 2, 2,2, false, AbilityType.heal,"ability/heal.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.heal(self.getCharacter().strength);
        }
    });

    public static final Ability regen = new Ability("regen", 3,2, 2, false, AbilityType.heal,"ability/HOT.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.addEffect(new HOT("Regen", "ability/HOT.png", other, 3,self.getCharacter().strength/2));
        }
    });

    public static final Ability healSelfImmediate = new Ability("Field Care", 1, 3, 3, false, AbilityType.heal,"ability/heal.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            self.heal(self.getCharacter().hp.get() / 3);
        }
    });

    public static final Ability bigSmack = new Ability("Big Smak", 1, 3, 2, false, AbilityType.attack,"ability/bigSmack.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.takeGuardedDamage(self.getDamage() * 2);
        }
    });

    public static final Ability doubleTap = new Ability("Double Shot", 4, 4, 3, false, AbilityType.attack,"ability/doubleShot.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.takeGuardedDamage(self.getDamage() * 2);
        }
    });

    public static final Ability throwSpear = new Ability("throw spear", 4, 2,4, false, AbilityType.attack,"ability/throwSpear.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.getCharacter().takeDamage(self.getDamage());
        }
    });

    public static final Ability shieldUp = new Ability("Shield Up", 1, 2, 4, false, AbilityType.buff,"ability/buffDefense.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            other.addEffect(new BuffDefense("Buff Defense", "ability/buffDefense.png", self, 3, self.getDefense()/2));
        }
    });

    public static final Ability buffOffense = new Ability("Rage", 1, 2, 4, false, AbilityType.buff,"ability/buffOffense.png", new BiConsumer<CharacterModel, CharacterModel>() {
        @Override
        public void accept(CharacterModel self, CharacterModel other) {
            self.addEffect(new BuffDamage("Buff Offense", "ability/buffOffense.png", self, 4, 15));
        }
    });
}