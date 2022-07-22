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

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.ImageView;
import something.disciplines.Discipline;
import something.disciplines.Perk;


import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;



public class Character {
    private final Random random = new Random();

    private final int HP_RANGE = 20;
    private final int MIN_HP = 50;

    private final int STRNGTH_RANGE = 10;
    private final int MIN_STRNGTH = 10;

    private final int DODGE_RANGE = 10;
    private final int MIN_DODGE = 10;

    private final int HIT_RANGE = 10;
    private final int MIN_HIT = 10;

   public static final int MAX_ACTION_POINT = 10;

    public String name;
    public Discipline discipline;
    public int maxHp;
    public int maxXp;

    public int dodge;
    public int hit;

    public int strength;
    public Weapon weapon;
    public ImageView image;

    public double damageMod;
    public int moveDist;
    public int extraDef;
    public int actionPoints;
    public int maxAction;
    public int actionRegen;

    public int skillPoint;

    public Armor helmet;
    public Armor torso;
    public Armor legs;


    public SimpleIntegerProperty hp;
    public SimpleIntegerProperty xp;


    public double healthPercent;
    public double xpPercent;

    //random test character
    public Character(){

        name = getRandomName();
        ArrayList<Discipline> list = Creator.listPlayerDisciplines();
        discipline = list.get(random.nextInt(list.size()));
        maxHp = random.nextInt(MIN_HP,MIN_HP + HP_RANGE);
        strength = random.nextInt(MIN_STRNGTH, STRNGTH_RANGE + MIN_STRNGTH);
        dodge = random.nextInt(MIN_DODGE, DODGE_RANGE + MIN_DODGE);
        hit = random.nextInt(MIN_HIT, HIT_RANGE + MIN_HIT);
        xp = new SimpleIntegerProperty(0);


        healthPercent = 1.0;
        xpPercent = 1.0;
        maxXp = 100;
        xp.addListener(c -> xpPercent = xp.doubleValue() / maxXp);

        hp = new SimpleIntegerProperty(maxHp);
        hp.addListener(c -> healthPercent = hp.doubleValue() / maxHp);

        damageMod = discipline.damageMod;
        moveDist = discipline.moveDist;
        extraDef = 0;

        maxAction = 10;
        actionPoints = 5;
        actionRegen = 5;

        this.image = new ImageView(discipline.imageURL);
        skillPoint = 0;
    }

    public Character(String name, Discipline dis){
        this.name = name;
        discipline = dis;
        maxHp = random.nextInt(MIN_HP,MIN_HP + HP_RANGE);
        strength = random.nextInt(MIN_STRNGTH, STRNGTH_RANGE + MIN_STRNGTH);
        xp = new SimpleIntegerProperty(0);
        healthPercent = 1.0;
        xpPercent = 0;
        maxXp = 100;
        xp.addListener(c -> xpPercent = xp.doubleValue() / maxXp);

        hp = new SimpleIntegerProperty(maxHp);
        hp.addListener(c -> healthPercent = hp.doubleValue() / maxHp);
        damageMod = discipline.damageMod;
        moveDist = discipline.moveDist;
        extraDef = 0;

        maxAction = 10;
        actionPoints = 5;
        actionRegen = 5;

        this.image = new ImageView(discipline.imageURL);
        skillPoint = 0;
    }

    public Character(String name, Discipline dis, int hp, int maxHp, int xp, int maxXp, int str, int dodge, int hit, Armor helmet, Armor torso, Armor legs, Weapon weapon){
        this.name = name;
        this.discipline = dis;
        damageMod = discipline.damageMod;
        moveDist = discipline.moveDist;
        extraDef = 0;
        this.hp = new SimpleIntegerProperty(hp);
        this.maxHp =maxHp;
        this.xp = new SimpleIntegerProperty(xp);
        this.maxXp = maxXp;
        this.strength = str;
        this.dodge = dodge;
        this.hit = hit;
        this.helmet = helmet;
        this.torso = torso;
        this.legs = legs;
        this.weapon = weapon;
        this.image = new ImageView(discipline.imageURL);

        maxAction = 10;
        actionPoints = 5;
        actionRegen = 5;
        skillPoint = 0;
    }

    public int attack(){
        if (weapon != null)
            return (int) ((this.strength + weapon.damage) * 2 * damageMod + random.nextInt(10));
        return (int) (this.strength * 2 * damageMod + random.nextInt(10) );
    }

    public void levelStrength(int num){
        this.strength+=num;
    }

    public int getDodgeChance(){
        return this.dodge + discipline.dodgeChance;
    }
    public void levelDodge(int num){
        this.dodge+= num;
    }

    public int getHitChance(){
        return this.hit + discipline.hitChance;
    }
    public void levelHitChance(int num){
        this.hit+=num;
    }

    public void takeDamage(int num) {
        if (num <= 0)
            return;
        int health = this.hp.getValue();
        this.hp.set(health - num);
    }


    public void giveXp(int num){
        xp.setValue(xp.getValue() + num);
    }


    public void setWeapon(Weapon wpn){
        this.weapon = wpn;
    }

    public boolean hasWeapon(){
        return this.weapon != null;
    }
    public int getWpnDmg(){
        return this.weapon.damage;
    }

    public int getDamage(){
        if (hasWeapon())
            return (int) ((getWpnDmg() + this.strength) * damageMod);
        return (int) (this.strength * damageMod);
    }


    private String getRandomName(){
        //File file = new File(Runnable.class.getResource("names.txt"));
        Path resourceDirectory = Paths.get("App","src", "main", "resources", "misc");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath() + "\\names.txt";
        File file = new File(absolutePath);
        String name;
        try {
            RandomAccessFile randFile = new RandomAccessFile(file, "r");
            long randomLocation = (long) (Math.random() * randFile.length());
            randFile.seek(randomLocation);
            randFile.readLine();
            name = randFile.readLine();
            System.out.println("random name: " + name);
            randFile.close();
        }catch (Exception e ){
            name = "FNF";
        }
        return name;

    }

    public void regenAction(){
        actionPoints+=actionRegen;
        if (actionPoints > MAX_ACTION_POINT)
            actionPoints = MAX_ACTION_POINT;
    }
    public boolean canAttack(){
        return actionPoints >= discipline.attackActionCost;
    }

    public Character cloneObj(){
        Character out = new Character(this.name, this.discipline);
        out.name = name;
        out.discipline = this.discipline.cloneObj();
        out.maxHp = this.maxHp;
        out.strength = strength;
        out.xp = new SimpleIntegerProperty(this.xp.get());
        out.healthPercent = this.healthPercent;
        out.xpPercent = this.healthPercent;
        out.maxXp = 100;
        out.xp.addListener(c -> out.xpPercent = out.xp.doubleValue() / out.maxXp);

        out.maxAction = this.maxAction;
        out.actionPoints = this.actionPoints;
        out.actionRegen = this.actionRegen;

        out.hp = new SimpleIntegerProperty(maxHp);
        out.hp.addListener(c -> out.healthPercent = out.hp.doubleValue() / out.maxHp);

        if (this.hasWeapon())
            out.weapon = weapon.cloneObj();
        this.image = new ImageView(this.discipline.imageURL);
        return out;
    }
}