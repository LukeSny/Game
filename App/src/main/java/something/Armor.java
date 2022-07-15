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

public class Armor extends Item{
    public Slot slot;
    public int def;

    public Armor(String na, int pr, String imgURL, Slot slot, int def, String des) {
        super(na, pr, imgURL, des);
        this.slot = slot;
        this.def = def;
    }

    public Armor cloneObj(){
        Armor out = new Armor(name, price, image.getImage().getUrl(), slot, def, description);
        return out;
        //this is a test
    }
}