package something.battleScene;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import something.CharacterModel;
import something.EnemyModel;
import something.PlayerModel;
import java.util.ArrayList;

/* This exists as a method for me to fuck around and find out what I can do here. I'm dabbin.
Let's see what I can make while having no knowledge of what any of your code does and barely remembering java.
 */
public class HoldenAI {

    Grid grid;

    public HoldenAI(Grid g){grid = g;}

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

public ArrayList<PlayerModel> FindAT(EnemyModel enemy){
    // First, find enemies in range. Add weight to those enemies.
    ArrayList<PlayerModel> targets = null;
    for (PlayerModel character:grid.party.getModels()){
        character.ATWeight = 0.0;
        if (inRange(enemy,character)){
            character.ATWeight += 1000.0;
        }
        character.ATWeight -= (character.getDefense()/2);
        character.ATWeight += (character.getDamage()/2);
        character.ATWeight -= character.getCharacter().hp.getValue();
        //if(character.getClass() == healer){
        //character.ATWeight = character.ATWeight + 20.0}
        targets.add(character);

    }
    return targets;
}



    /**
     * method that governs the movement of each enemy, includes an animation to the given tile
     * @param enemy enemyModel that needs to be moved
     * @param tile tile that the enemy will move to
     */
    public void move(EnemyModel enemy, Tile tile){

        //update modelTiles
        grid.swapSpot(enemy, tile);

        //remove things to be swapped
        grid.gridView.getChildren().remove(enemy.getRoot());

        TranslateTransition movement = new TranslateTransition();
        movement.setInterpolator(Interpolator.LINEAR);
        movement.setToX(tile.back.getTranslateX());
        movement.setToY(tile.back.getTranslateY());
        movement.setDuration(Duration.seconds(.5));
        movement.setNode(enemy.getRoot());
        movement.play();
        grid.gridView.getChildren().remove(tile.back);

        //swap around x and y values
        int tempX = enemy.getX();
        int tempY = enemy.getY();
        enemy.setX(tile.getX());
        enemy.setY(tile.getY());

        //add them back in
        grid.gridAdd(enemy);
        grid.gridAdd(grid.emptyTiles[tempX][tempY]);
    }

    /**
     * loops around the given center tile and compares the distance of the tiles, return the tile that has the least
     * distance and is not taken
     * @param thing the thing that we want to find a closer tile to
     * @param center the tile that we are searching around
     * @return the tile that is adjacent to the center tile and closer to the given thing
     */
    public Tile getCloserTile(CharacterModel thing, Tile center){
        double distance = getDistance(thing, center);
        //System.out.println("center: " + center);
        Tile closest = center;
        for (int i = center.getX() - 1; i < center.x + 2; i++) {
            for (int j = center.y - 1; j < center.y + 2; j++) {
                try {
                    Tile looking = grid.emptyTiles[i][j];
                    double lookingDist = getDistance(thing, looking);
                    //if the distance is shorter AND that spot isnt taken, make it the best candidate
                    if (lookingDist < distance && grid.tileIsFree(looking)) {

                        closest = looking;
                        distance = lookingDist;
                        //System.out.println("updated closest: " + closest + " : " + distance);
                    }
                }catch (Exception ignored){
                }
            }
        }
        //System.out.println("closest tile : " + closest);
        return closest;
    }

    public double getDistance(CharacterModel thing1, CharacterModel thing2){
        int x = thing2.getX() - thing1.getX();
        int y = thing2.getY() - thing1.getY();
        return Math.sqrt(x*x + y*y);
    }

    public double getDistance(CharacterModel thing1, Tile thing2){
        int x = thing2.getX() - thing1.getX();
        int y = thing2.getY() - thing1.getY();
        return Math.sqrt(x*x + y*y);
    }

    public PlayerModel findCloser(EnemyModel enemy){
        PlayerModel closest = grid.party.getModels().get(0);
        double distance = getDistance(closest, enemy);
        for (PlayerModel player : grid.party.getModels()){
            if (getDistance(player, enemy) < distance)
                closest = player;
        }
        return closest;

    }

    public boolean inRange(CharacterModel attacker, CharacterModel defender){
        System.out.println("dist: " + getDistance(attacker, defender) + " |range: " + Math.sqrt(attacker.range()*attacker.range() * 2) + .1);
        System.out.println("attacker x,y: " + attacker.getX() + "|" + attacker.getY());
        System.out.println("defender x,y: " + defender.getX() + "|" + defender.getY());
        return getDistance(attacker, defender) < Math.sqrt(attacker.range()*attacker.range() * 2) + .1;
    }



    public Tile findTile(EnemyModel enemy, Tile tile) {
        //System.out.println("Current distance: " + getDistance(enemy, tile));
        if (getDistance(enemy, tile) <= Math.sqrt(enemy.moveDist()* enemy.moveDist() * 2) +.01 && grid.tileIsFree(tile)) {
            //System.out.println("closest tile found for " + enemy.getCharacter().name + " x: " + tile.getX() + " y: " +tile.y);
            return tile;
        }
        Tile closer = getCloserTile(enemy, tile);
        return findTile(enemy, closer);
    }





}