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

import something.disciplines.Discipline;

public class Weapon extends Item{
    public int damage;
    public Discipline discipline;

    public Weapon(String na, int pr, String imgURL, int dmg, Discipline discipline, String des){
        super(na,pr, imgURL, des);
        damage = dmg;
        this.discipline = discipline;
    }

    public Weapon cloneObj(){
        Weapon out = new Weapon(name, price, image.getImage().getUrl(), damage, discipline, description);
        return out;
    }

}