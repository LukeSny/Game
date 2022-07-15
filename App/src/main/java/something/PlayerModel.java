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

package something;

import something.townScene.LevelUpScene;

/**
 * model of specifically players
 * extra data are if the model is currently allowed to move or attack
 */
public class PlayerModel extends CharacterModel{
    public static int nextXOpen = 0;
    public double ATWeight = 0.0;
    public PlayerModel(Character character, int x, int y){
        super(character, x, y);
    }
    public PlayerModel(){
        super(nextXOpen % Runnable.NUM_COLS, 0);
        //canAttack = true;
        int y = nextXOpen % Runnable.NUM_COLS;
        int x = 0;
        if (nextXOpen > Runnable.NUM_COLS)
            x =1;
        this.setX(x);
        this.setY(y);
    }

    public void setCoords(int x, int y){
        this.x = x;
        this.y = y;
    }

    public PlayerModel cloneObj(){
        PlayerModel out = new PlayerModel(this.character.cloneObj(), this.x, this.y);
        return out;
    }

    public boolean canLevel(){
        return character.xp.get() >= character.maxXp;
    }
}