package something.battleScene;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import something.CharacterModel;
import something.EnemyModel;
import something.PlayerModel;
import something.disciplines.Ability;
import something.disciplines.AbilityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* This exists as a method for me to fuck around and find out what I can do here. I'm dabbin.
Let's see what I can make while having no knowledge of what any of your code does and barely remembering java. Dummy.
 */
public class HoldenAI {

    Grid grid;
    EnemyController encon;
    public HoldenAI(Grid g) {
        grid = g;
        encon = new EnemyController(grid);
        for (EnemyModel enemy : grid.enemies.getEnemies()){
            System.out.println("now working with: " + enemy.getName());
            driver(enemy);
        }
    }

    /*
    What i want the enemy to do is find the optimal enemy it can target based on its class and abilities.
    This needs to be dynamic, so that as you add classes and abilities, enemies gain similar classes and abilities.
    Hopefully the way you've implemented that makes it possible.
     */

    /*
    Anyway, i outline in comments before I start coding, so deal with it.
    First, we need to know how the AI is going to make its decision. Further, we're not going to make an actual AI,
    it'll be a dumb AI. Here's the list of things the AI needs to look at:

    --Hero distance: If someone is in range, we may as well hit them, UNLESS a very attractive target is just out of range
    and hitting this hero will stop me from killing them. If no hero is in range, pick a target based on the closest
    attractive target, with attractive targets being detailed below.

    --Hero HP: prioritize kills. If an enemy has low defense, low hp, or an attack would result in a kill, do it.
    Action economy is important and killing player units both makes it harder and

    --Hero Abilities: Healing is powerful, and targeting tanks is pointless. Kill the squishy, powerful units if possible.
    Ignore tanks unless forced to by terrain, abilities, or something else.

    --Chance to hit: a 5% chance to hit is a bad idea. Don't do that.

    --Enemy abilities: This one is hard. It's a class by class decision is likely the best way to do it. But, if the
    enemy has an ability that lets them target extra range, or deal extra damage, that should be taken into account. If
    it lets them target an AT or net a kill, they should use it to do so. This can be implemented with a priority system
    to save computation time. For example:
        1. If an enemy would die to Big Smash, use big smash
        2. If an enemy is on >90% hp, use HP Halver (I'm making these up)
        3. If not enemy is in range of Big Smash or attack, use Throw Rock
    Such that if the first condition is met, the enemy will always do it. If the second and third are met but not 1,
    the enemy will do 2. If only 3 is met, the enemy will do 3. If no conditions are met, the enemy will only move.

    The other way to do this is to calculate the damage that each attack could do to each target. This can work well for
    martial classes, who just want to hit hard, but if any support abilities are introduced then you would need to
    introduce the priority system for the enemies to ever use them.

    --Random Stupid: sometimes, the enemy just makes a bad call. It's not often, but sometimes they're just a little dumb.
    Maybe they randomly choose a low priority ability, or choose the least attractive target. Unsure. Not entirely
    necessary either, just a possibility to consider adding.

    --Future additions: if support classes are created, they will need high weights or priority on support abilities
    to prevent them from unga bunga attacking people. Cool concept tho.

    Alright. Now that we know what we want to do, let's start by calculating target attractiveness. We'll look at all
    heroes on the grid, then make decisions only the top 2 or 3 heroes, who will almost always be the ones within our
    attack range.
     */

    public void driver(EnemyModel enemy){
        ArrayList<PlayerModel> targets = findAT(enemy);
        chooseActionWarrior(targets, enemy);
    }

    public ArrayList<PlayerModel> findAT(EnemyModel enemy) {
        // First, find enemies in range. Add weight to those enemies.
        ArrayList<PlayerModel> targets = new ArrayList<>();
        for (PlayerModel character : grid.party.getModels()) {
            character.ATWeight = 0.0;
            if (encon.inRange(enemy, character)) {
                character.ATWeight += 100.0;
            }
            character.ATWeight -= (character.getDefense() / 2);
            character.ATWeight += (character.getDamage() / 2);
            character.ATWeight -= character.getCharacter().hp.getValue();
            //if(character.getClass() == healer){
            //character.ATWeight = character.ATWeight + 20.0}
            targets.add(character);

        }
        Comparator<PlayerModel> byAT = Comparator.comparing(c -> c.ATWeight);
        //ATSort.quickSort(targets, 1, 2);
        targets.sort(byAT.reversed());
        System.out.println("list of target attraction");
        targets.forEach(c -> {
            System.out.println(c.getName() + ": " + c.ATWeight);
        });
        return targets;
    }

// We have a nice list of sorted, valuable targets now. Let's kill them.
/*
We want to look at just a few targets, and we want to stop looking if we find a priority action we can do to that target.
So, looking at the highest priority target first, we want to know these specific things, in this order:

    1. Can I move within range of this target and still attack? If yes: continue the rest of the checks.
    If no; skip this target and find someone closer. If no closer target can be found,
    use as much AP as needed to move to this target.

    2. Can I kill this target? If yes: break loop and kill target. Check both regular attacks and skills. If a regular
    attack will kill the target, use it. Otherwise, check if skills will kill the target. If a skill will kill the target
    use the skill.
    If no: continue checks.

    3. Can I surround this enemy? If yes: break loop, surround and attack enemy. Use a skill to deal more damage if able.

    4.

    After I've done this, we want the enemy to check how much AP he has left. If he can act again, do it. Otherwise, he
    will move towards the most attractive target. If he is already next to the most attractive target, he will wait
    patiently for his chance to kill.
 */
    public void chooseActionWarrior(ArrayList<PlayerModel> targets, EnemyModel enemy){

        /*check if any of the targets can be killed, if so then do that*/
        boolean acted = checkLethalAction(targets, enemy);
        /*if no kill moves were found, see if you can attack/ability someone*/
        acted = checkNonLethalAction(targets, enemy, acted);
        System.out.println("enemy ability list: ");
        enemy.getCharacter().discipline.abilities.forEach(c -> System.out.println(c.name));
        /*if no offensive actions were found to be viable, move in range of the most valuable player*/
        if (!acted) {
            System.out.println("no offensive moves found, moving towards " + targets.get(0).getName());
            Tile tile = encon.getTileInAttackRange(enemy, targets.get(0));
            System.out.println("tile: " + tile.x + ", " + tile.y);
            encon.justMove(enemy, tile);
            System.out.println("move successful");
        }

        /*if they have enough action to make an impact, go again*/
        if (enemy.getAP() >= enemy.getCharacter().discipline.attackActionCost) {
            System.out.println("acting again");
            chooseActionWarrior(targets, enemy);
        }
        System.out.println("acted status: " + acted);
    }

    private boolean checkNonLethalAction(ArrayList<PlayerModel> targets, EnemyModel enemy, boolean acted) {
        for (PlayerModel target : targets){
            /* prioritize either attack or ability based on what will do more damage*/
            int expectedAttackDamage = enemy.getDamage() - target.getDefense();
            int expectedAbilityDamage = 0;
            int abilityAP = 1000; // ridiculous number so it only passes checks when actually assigned
            DumbHolder holder = getMostDamagingAbility(enemy, target);

            if (holder.ability != null) {
                expectedAbilityDamage = holder.num;
                abilityAP = encon.getAbilityMovementAP(enemy, target, holder.ability) + holder.ability.apCost;
            }
            int attackAP = encon.getAttackMoveAP(enemy, target) + enemy.getCharacter().discipline.attackActionCost;

            boolean hasAPforAttack = enemy.getAP() >= attackAP;
            boolean hasAPforAbility = enemy.getAP() >= abilityAP;

            if (!hasAPforAttack && !hasAPforAbility) {
                System.out.println("not enough ap for non-lethal");
                continue;
            }
            System.out.println("attack | ab damage: " + expectedAttackDamage + " | " + expectedAbilityDamage);
            /* prioritize regular attack over using an ability*/
            if (expectedAttackDamage >= expectedAbilityDamage && hasAPforAttack){
                System.out.println("attacking!");
                System.out.println("attack ap needed | has: " + attackAP + " | " + enemy.getAP());
                /*move to the first tile within range and then attack*/
//                encon.move(enemy, encon.getTileInAttackRange(enemy, target));
//                grid.attack(enemy, target);
                encon.moveAndAttack(enemy, target, encon.getTileInAttackRange(enemy, target));
                acted = true;
                break;
            }
            else if (hasAPforAbility && holder.ability != null){
                System.out.println("using an ability!");
                System.out.println("ability ap needed | has: " + abilityAP + " | " + enemy.getAP());
                encon.move(enemy, encon.getTileInAbilityRange(enemy, target, holder.ability));
                holder.ability.abilityAction(enemy, target, grid.party);
                acted = true;
            }

        }
        return acted;
    }

    private boolean checkLethalAction(ArrayList<PlayerModel> targets, EnemyModel enemy) {
        boolean acted = false;
        for (PlayerModel target : targets){
            System.out.println("currently considering target: " + target.getName());
            /*action points needed to move and attack this enemy, if more than current ap, skip this target*/



            /*if their damage is enough to kill, then certainly move and attack*/
            int expectedAttackDamage = enemy.getDamage() - target.getDefense();
            int expectedAbilityDamage = 0;
            int abilityAP = 1000; // ridiculous number so it only passes checks when actually assigned
            DumbHolder holder = getMostDamagingAbility(enemy, target);

            if (holder.ability != null) {
                expectedAbilityDamage = holder.num;
                abilityAP = encon.getAbilityMovementAP(enemy, target, holder.ability) + holder.ability.apCost;
            }
            int attackAP = encon.getAttackMoveAP(enemy, target) + enemy.getCharacter().discipline.attackActionCost;
            boolean hasAPforAttack = enemy.getAP() >= attackAP;
            boolean hasAPforAbility = enemy.getAP() >= abilityAP;
            if (!hasAPforAttack && !hasAPforAbility) {
                System.out.println("not enough ap for lethal");
                continue;
            }

            System.out.println("attack | ab damage: " + expectedAttackDamage + " | " + expectedAbilityDamage);
            /* prioritize regular attack over using an ability*/
            if (expectedAttackDamage >= target.getCharacter().hp.get() && hasAPforAttack){
                System.out.println("doing a lethal attack");
                /*move to the first tile within range and then attack*/
                encon.moveAndAttack(enemy, target, encon.getTileInAttackRange(enemy, target));
                acted = true;
                break;
            }
            else if (expectedAbilityDamage >= target.getCharacter().hp.get() && hasAPforAbility && holder.ability != null){
                System.out.println("lethal ability");
                encon.move(enemy, encon.getTileInAbilityRange(enemy, target, holder.ability));
                holder.ability.abilityAction(enemy, target, grid.party);
                acted = true;
            }
        }
        return acted;
    }

    /**
     *
     * @return a stupid holder that contains the best ability and the predicted damage it will cause
     */
    private DumbHolder getMostDamagingAbility(EnemyModel enemy, PlayerModel model){
        PlayerModel clone = model.cloneObj();
        Ability best = null;
        int mostDamage = 0;
        for (Ability ab : enemy.getCharacter().discipline.getTypeAbilities(AbilityType.attack)){
            if (ab.isReady()) {
                clone.fullHeal();
                int hpBefore = clone.getCharacter().hp.get();
                ab.action.accept(enemy, clone);
                int damage = hpBefore - clone.getCharacter().hp.get();
                if (damage > mostDamage){
                    mostDamage = damage;
                    best = ab;
                }
            }
        }
        for (Ability ab : enemy.getCharacter().discipline.getTypeAbilities(AbilityType.DOT)){
            if (ab.isReady()){
                clone.getEffects().clear();
                clone.fullHeal();
                int hpBefore = clone.getCharacter().hp.get();
                ab.action.accept(enemy, clone);
                while (!clone.getEffects().isEmpty())
                    clone.playEffects();
                int damage = hpBefore - clone.getCharacter().hp.get();
                if (damage > mostDamage){
                    mostDamage = damage;
                    best = ab;
                }
            }
        }
        return new DumbHolder(mostDamage, best);
    }

}

class DumbHolder{
    int num;
    Ability ability;
    DumbHolder(int nu, Ability ab){
        num = nu;
        ability = ab;
    }
}