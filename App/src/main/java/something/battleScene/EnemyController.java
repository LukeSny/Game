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

package something.battleScene;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import something.CharacterModel;
import something.EnemyModel;
import something.PlayerModel;

public class EnemyController{

    Grid grid;

    public EnemyController(Grid g){
        grid = g;
    }

    /**
     * best I could do right now
     * if the enemy is within attack range of the closest enemy, do not move
     * if the enemy could possibly move onto the same tile as the closest enemy, find the tile between enemy and character
     * and move there
     * else just move as close as possible
     * @param enemy Enemy model that will be moved
     */
    public void enemyMovement(EnemyModel enemy){
        PlayerModel player = findCloser(enemy);
        Tile closest;
        System.out.println(enemy.getName() + " targeting " + player.getName());
        //if within attacking distance, don't move
        if (inRange(enemy, player)) return;

        //if close enough to walk on them
        // this solution on works on things that can move 2 tiles
        if(getDistance(enemy, player) <= Math.sqrt(enemy.moveDist()*enemy.moveDist() * 2) + .01 ) {
            System.out.println("too close moving: " + player.getX() + player.getY());
            closest = getCloserTile(enemy, grid.emptyTiles[player.getX()][player.getY()]);
        }
        else {
            System.out.println("far away moving");
            closest = findTile(enemy, grid.emptyTiles[player.getX()][player.getY()]);
        }
//        System.out.println("dist: " + getDistance(enemy, closest));
//        System.out.println("range: " + Math.sqrt(enemy.moveDist()*enemy.moveDist() * 2));
//        System.out.println("lolololololol\n\n\n\n\n");
        move(enemy, closest);


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
     * algorithm starts at the PlayerModel tile, looks for the adjacent tile that is closest to the enemy
     * it then recursively moves one tile at a time until it hits a tile the enemy is able to move to
     * @param enemy enemyModel that needs to be moved
     * @param tile recursive parameter, initial condition is Playermodel's tile
     * @return a tile closer to the playerModel this enemy is targeting until finds a tile close enough to move
     */
    public Tile findTile(EnemyModel enemy, Tile tile) {
        //System.out.println("Current distance: " + getDistance(enemy, tile));
        if (getDistance(enemy, tile) <= Math.sqrt(enemy.moveDist()* enemy.moveDist() * 2) +.01 && grid.tileIsFree(tile)) {
            //System.out.println("closest tile found for " + enemy.getCharacter().name + " x: " + tile.getX() + " y: " +tile.y);
            return tile;
        }
        Tile closer = getCloserTile(enemy, tile);
        return findTile(enemy, closer);
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

    /**
     * returns the PlayerModel that is the closest to the given enemyModel
     * @param enemy which needs to have the closest enemy assigned
     * @return the PlayerModel that is the closest to the given enemy
     */
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


}