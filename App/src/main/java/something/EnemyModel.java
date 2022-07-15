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

/**
 * if something needs to be special about an enemy model, it can go here
 */
public class EnemyModel extends CharacterModel{

    public EnemyModel(Character character, int x, int y){
        super(character, x, y);
    }


    public EnemyModel cloneObj(){
        return new EnemyModel(character.cloneObj(), x, y);
    }
}