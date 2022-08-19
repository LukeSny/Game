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
import java.util.function.Consumer;

public class Warrior extends Discipline {

    public Warrior(){
        super(1,1, 1, 10, 10, 3, "models/warriorImage.png");
        name = "Warrior";

        abilities.add(Ability.shieldUp);

        perkTree = new PerkTree(start);
    }

    public Warrior cloneObj(){
        Warrior out = new Warrior();
        out.abilities.addAll(abilities);
        return out;
    }

    Perk start = new Perk("First Step", "models/warriorImage.png", "the first step on becoming a true warrior \nstrength and defense + 5", new Consumer<Character>() {
        @Override
        public void accept(Character character) {
            character.strength+=5;
            character.extraDef+=5;
        }
    });
    Perk tankStart = new Perk("Extra Def", "ability/buffDefense.png", "let them try and kill me \ndefense + 15", start, new Consumer<Character>() {
        @Override
        public void accept(Character character) {
            character.extraDef += 15;
        }
    });
    Perk tankHeal = new Perk("Tank heal", "ability/HOT.png", "just need a a bandage \nHeal over time ability", tankStart, new Consumer<Character>() {
        @Override
        public void accept(Character character) {
            character.discipline.abilities.add(Ability.regen);
        }
    });
    Perk tankDef2 = new Perk("Extra def2", "ability/buffDefense.png", "I'll add a few more plates \ndefense + 20", tankStart, new Consumer<Character>() {
        @Override
        public void accept(Character character) {
            character.extraDef += 20;
        }
    });
    Perk offenseStart = new Perk("Offense", "poop.jpg", "they should have stayed home \nStrength + 10", start, new Consumer<Character>() {
        @Override
        public void accept(Character character) {
            character.strength+= 10;
        }
    });
    Perk offenseBuff = new Perk("Buff Ability", "poop.jpg", "My might is outragous \nBuff Offense ability", offenseStart, new Consumer<Character>() {
        @Override
        public void accept(Character character) {
            character.discipline.abilities.add(Ability.buffOffense);
        }
    });


}