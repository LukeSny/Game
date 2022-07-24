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

import javafx.stage.Stage;
import something.disciplines.Ability;
import something.disciplines.Discipline;
import something.disciplines.Perk;
import something.disciplines.PerkTree;
import something.disciplines.effects.*;
import something.townScene.ItemCard;
import something.worldScene.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;

public class Save {

    private static final File saveFile = new File(Paths.get("App", "src", "main",
            "resources").toFile().getAbsolutePath() + "\\savedGame.txt");

    private static final ArrayList<Ability> abilityList = Ability.getFullAbilityList();

    public static void writeSave(World world){

        try {
            PrintWriter writer = new PrintWriter(saveFile);

            writer.println(writeParty(world));

            for (int i = 0; i < World.PANEL_LENGTH; i++) {
                for (int j = 0; j < World.PANEL_LENGTH; j++) {
                    Panel currentPanel = world.panels[i][j];
                    writer.println(writePanel(currentPanel));
                }
            }
            System.out.println("closing writer");
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static void writeSave(World world, String file){
        File fileName = new File(Paths.get("App", "src", "main",
                "resources").toFile().getAbsolutePath() + "\\" + file);
        try {
            PrintWriter writer = new PrintWriter(fileName);

            writer.println(writeParty(world));

            for (int i = 0; i < World.PANEL_LENGTH; i++) {
                for (int j = 0; j < World.PANEL_LENGTH; j++) {
                    Panel currentPanel = world.panels[i][j];
                    writer.println(writePanel(currentPanel));
                }
            }
            System.out.println("closing writer");
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static World readSave(Stage stage){
        try {
            Scanner scnr = new Scanner(saveFile);
            StupidHolder holder = readOutParty(scnr);
            World world = new World(holder.party, stage, true);
            System.out.println("Successfully rebuilt World!!");
            world.party.party.getModels().forEach(c -> System.out.println(c.getName()));
            world.party.party.getItems().forEach(c -> System.out.println(c.getNameLabel().getText()));
            world.initCurrentPanel(holder.row, holder.col);

            for (int i = 0; i < World.PANEL_LENGTH; i++) {
                for (int j = 0; j < World.PANEL_LENGTH; j++) {
                   world.panels[i][j] = readOutPanel(scnr, world, i, j);
                   for (PlaceWorldModel model : world.panels[i][j].places){
                       if (model.nameLabel.getText().equals("Town"))
                           world.townCard = model;
                   }
                }
            }
            world.initCurrentPanel(holder.row, holder.col);
            world.initTown();


            //world.primaryStage.setScene(world.currentPanel.scene);
            return world;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static World readSave(Stage stage, String file){
        File fileName = new File(Paths.get("App", "src", "main",
                "resources").toFile().getAbsolutePath() + "\\" + file);
        try {
            Scanner scnr = new Scanner(fileName);
            StupidHolder holder = readOutParty(scnr);
            World world = new World(holder.party, stage, true);
            System.out.println("Successfully rebuilt World!!");
            world.party.party.getModels().forEach(c -> System.out.println(c.getName()));
            world.party.party.getItems().forEach(c -> System.out.println(c.getNameLabel().getText()));


            for (int i = 0; i < World.PANEL_LENGTH; i++) {
                for (int j = 0; j < World.PANEL_LENGTH; j++) {
                    world.panels[i][j] = readOutPanel(scnr, world, i, j);
                    for (PlaceWorldModel model : world.panels[i][j].places){
                        if (model.nameLabel.getText().equals("Town"))
                            world.townCard = model;
                    }
                }
            }
            world.initCurrentPanel(holder.row, holder.col);
            world.initTown();
            world.currentPanel.root.getChildren().forEach(System.out::println);
            System.out.println("world party: " + world.party.party);

            return world;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Panel readOutPanel(Scanner scnr, World world, int row, int col){
        int numFights = scnr.nextInt(); int numPlaces = scnr.nextInt();
        ArrayList<EnemyWorldModel> enemies = new ArrayList<>();
        ArrayList<PlaceWorldModel> places = new ArrayList<>();
        for (int i = 0; i < numFights; i++) {
            EnemyWorldModel en = readOutEnemyWorldModel(scnr);
            enemies.add(en);
        }
        for (int i = 0; i < numPlaces; i++) {
            System.out.println("skipped line: " + scnr.nextLine());
            String encounterUrl = scnr.next();
            String image = scnr.next();
            System.out.println("Strings: " + encounterUrl + " | " + image);
            double x = scnr.nextDouble(); double y = scnr.nextDouble();
            PlaceWorldModel place;
            if (!encounterUrl.equals("null")) {
                place = EncounterMaker.make(encounterUrl, world, row, col);
            }
            //this is for the town only
            else {
                place = new PlaceWorldModel();
            }

            place.root.setTranslateX(x);
            place.root.setTranslateY(y);
            places.add(place);
        }
        Panel panel = new Panel(enemies, places, row, col);
        enemies.forEach(c -> panel.root.getChildren().add(c.root));
        for (PlaceWorldModel model : places) {
            panel.root.getChildren().add(model.root);
            if (model.encounter != null)
                model.encounter.panelRoot = panel.root;
        }

        return panel;
    }

    private static EnemyWorldModel readOutEnemyWorldModel(Scanner scnr) {

        String name = scnr.next();
        String url = scnr.next();
        int numEnemies = scnr.nextInt();
        int numItems = scnr.nextInt();
        double x = scnr.nextDouble();
        double y = scnr.nextDouble();
        scnr.nextLine();
        ArrayList<EnemyModel> models = new ArrayList<>();
        ArrayList<ItemCard> items = new ArrayList<>();
        for (int j = 0; j < numEnemies; j++) {
            models.add(readEnemy(scnr));
        }
        for (int j = 0; j < numItems; j++) {
            items.add(readItem(scnr.nextLine()));
        }
        int xp = scnr.nextInt();
        int dif = scnr.nextInt();
        EnemyWorldModel en = new EnemyWorldModel(models, name, url, dif);
        en.xpReward = xp;
        en.lootDrop = items;
        en.root.setTranslateX(x);
        en.root.setTranslateY(y);
        return en;
    }

    private static StupidHolder readOutParty(Scanner scnr) {
        int gold = scnr.nextInt();
        int row = scnr.nextInt();
        int col = scnr.nextInt();
        System.out.println("row col: " + row + " " + col);
        int x = scnr.nextInt();
        int y = scnr.nextInt();
        System.out.println("x y: " + x + " " + y);
        int numModels = scnr.nextInt();
        System.out.println("numModels: " + numModels);
        int numItems = scnr.nextInt();
        System.out.println("number of items detected: " + numItems);
        String imageUrl = scnr.next();
        System.out.println("url: " + imageUrl);
        String name = scnr.nextLine();
        name = name.substring(1);
        ArrayList<PlayerModel> players = new ArrayList<>();
        ArrayList<ItemCard> items = new ArrayList<>();

        for (int i = 0; i < numModels; i++){
            players.add(readPlayer(scnr));
        }
        for (int i = 0; i < numItems; i++) {
            String next = scnr.nextLine();
            System.out.println("next: " + next);
            items.add(readItem(next));
        }
        Party pa = new Party(players, items, name);
        pa.setGold(gold);
        PartyWorldModel model = new PartyWorldModel(pa, imageUrl);
        model.root.setTranslateX(x);
        model.root.setTranslateY(y);
        return new StupidHolder(model, row, col);
    }

    private static String writeChar(CharacterModel player){
        int savedX =0 , savedY = 0;
        if (player instanceof PlayerModel){
            savedX = ((PlayerModel) player).savedX;
            savedY = ((PlayerModel) player).savedY;
        }
        Character ch = player.character;
        String out = ch.name + " " + ch.discipline.name + " " + player.x + " " + player.y + " " + " " + ch.hp.getValue() + " " + ch.maxHp + " ";
        out += ch.xp.getValue() + " " + ch.maxXp + " " + ch.strength + " " + ch.dodge + " " + ch.hit + " " + ch.skillPoint + " ";
        out += ch.actionPoints + " " + ch.damageMod + " " + ch.moveDist + " " + ch.extraDef + " " + ch.actionRegen + " " + savedX + " " + savedY + " ";
        String helm = ch.helmet == null ? null : removeSpace(ch.helmet.name);
        String torso = ch.torso == null ? null : removeSpace(ch.torso.name);
        String weapon = ch.weapon == null ? null : removeSpace(ch.weapon.name);
        String legs = ch.legs == null ? null : removeSpace(ch.legs.name);
        out += helm + " " + torso + " " + legs + " " + weapon;
        String perkLine = writeSkillTree(ch);
        String abilityLine = writeAbilityList(ch);
        String effectLine = writeStatusEffects(player);
        out += "\n" + perkLine + "\n" + abilityLine + "\n" + effectLine;
        System.out.println(out);
        return out;
    }
    private static String writeSkillTree(Character ch){
        if (ch.discipline.perkTree == null || !ch.discipline.perkTree.base.activated) {
            System.out.println("found an empty tree for " + ch.name);
            return "null";
        }
        String stepper = stepper(ch.discipline.perkTree.base);
        System.out.println("stepper: " + stepper);
        return stepper;
    }
    private static String stepper(Perk start){
        if (start.activated){
            if (start.unlocks.isEmpty())
                return  removeSpace(start.name) + " ";
            String next = "";
            for (Perk child : start.unlocks)
                next += stepper(child);
            return removeSpace(start.name) + " " + next;
        }
        return "";
    }
    private static String writeAbilityList(Character ch){
        if (ch.discipline.abilities.isEmpty()) {
            System.out.println("no abilities found");
            return "null";
        }
        String out = "";
        for (Ability ability : ch.discipline.abilities)
            out += removeSpace(ability.name) + " ";
        System.out.println("abilties found:" + ch.name + " | " + out);
        return out;
    }
    private static String writeStatusEffects(CharacterModel model){
        if (model.effects.isEmpty()) return null;
        StringBuilder out = new StringBuilder();
        for (Effect ef : model.effects){
            String current = swapSpaceOut(ef.name) + " " + ef.imageURL + " " + ef.timer + " " + ef.effect + " " + ef.type.name;
            out.append(current).append(" ");
        }
        return out.toString();
    }
    private static String writeItem(ItemCard it){
        return removeSpace(it.getItem().name);
    }
    private static String writePanel(Panel pa){
        int numEnemies = pa.enemies.size(); int numPlaces = pa.places.size();
        String header = numEnemies + " " + numPlaces;
        StringBuilder enemies = new StringBuilder();
        for (EnemyWorldModel enemy : pa.enemies){
            enemies.append(writeWorldEnemy(enemy));
        }
        StringBuilder places = new StringBuilder();
        for (PlaceWorldModel place : pa.places){
            places.append(writePlace(place));
        }
        String out = header;
        if (numEnemies >0)
            out += enemies;
        if (numPlaces > 0)
            out += places;
        return out;
    }

    private static String writeParty(World world){
        PartyWorldModel party = world.party;
        int gold = party.party.gold.get();
        int x = (int) party.root.getTranslateX();
        int y = (int) party.root.getTranslateY();
        Party pa = party.party;
        int numModels = pa.models.size();
        int numItems = pa.items.size();
        int row = world.currentPanel.row;
        int col = world.currentPanel.col;
        StringBuilder playerList = new StringBuilder();
        StringBuilder itemList = new StringBuilder();
        for (PlayerModel model : pa.models){
            playerList.append(writeChar(model));
            if (!model.equals(pa.models.get(pa.models.size()-1)))
                playerList.append("\n");
        }
        for (ItemCard item : pa.items){
            itemList.append(writeItem(item));
            if (!item.equals(pa.items.get(pa.items.size()-1)))
                itemList.append("\n");
        }
        if (numItems > 0)
            return gold + " " + row + " " + col + " " + x + " " + y + " " + numModels + " " + numItems + " " + party.imageUrl + " " + pa.name + "\n" + playerList + "\n" + itemList;
        return gold + " " + row + " " + col + " " + x + " " + y + " " + numModels + " " + numItems + " " + party.imageUrl + " " + pa.name + "\n" + playerList;
    }

    private static String writePlace(PlaceWorldModel place){
        String encounterName = place.encounter == null ? null : place.encounter.makerUrl;
        return "\n" + encounterName + " " + place.imageUrl + " " +
                place.root.getTranslateX() + " " + place.root.getTranslateY();
    }

    private static String writeWorldEnemy(EnemyWorldModel enemy){
        StringBuilder enemyList = new StringBuilder();
        StringBuilder itemList = new StringBuilder();
        String header = "\n" + enemy.nameLabel.getText() + " " + enemy.imageUrl + " " +
                enemy.getEnemies().size() + " " + enemy.getLootDrop().size() + " " +
                enemy.root.getTranslateX() + " " + enemy.root.getTranslateY();
        for (EnemyModel model : enemy.getEnemies()){
            enemyList.append(writeChar(model)).append("\n");
        }
        for (ItemCard item : enemy.getLootDrop()){
            itemList.append(writeItem(item)).append("\n");
        }

        return header +  "\n" + enemyList + itemList +
                enemy.getXpReward() + " " + enemy.getDifficulty();
    }



    private static Armor searchArmor(String name){
        for (Armor item : Creator.createArmor()){
            String check = removeSpace(item.name);
            System.out.println(check + " vs " + name);
            if (check.equals(name)) {
                return item;
            }
        }
        return null;
    }
    private static Weapon searchWpn(String name){
        for (Weapon item : Creator.createWeapons()){
            String check = removeSpace(item.name);
            System.out.println(check + " vs " + name);
            if (check.equals(name)) {
                return item;
            }
        }
        return null;
    }
    private static Discipline searchDiscipline(String name){
        for (Discipline dis : Creator.listAllDisciplines())
            if (name.equals(dis.name))
                return dis;
        return null;
    }
    private static Ability searchAbility(String name){
        for (Ability ab : abilityList)
            if (name.equals(removeSpace(ab.name)))
                return ab;
        return null;
    }
    private static boolean notPresent(Character ch, String ability){
        for (Ability ab : ch.discipline.abilities)
            if (ability.equals(removeSpace(ab.name)))
                return false;
        return true;
    }
    private static void constructTree(String line, Discipline dis){
        Scanner scnr = new Scanner(line);
        while (scnr.hasNext()){
            String name = scnr.next();
            PerkTree.traverseTree(dis.perkTree.base, new Consumer<Perk>() {
                @Override
                public void accept(Perk perk) {
                    if (perk.name.equals(name))
                        perk.saveActivate();
                }
            });
        }
    }


    private static PlayerModel readPlayer(Scanner overScan){
        int maxHp, maxXp, dodge, hit, str, moveDist, extraDef, actionPoints, actionRegen, skillPoint, hp, xp, x, y, savedX, savedY;
        double damageMod;
        Armor helm, torso, leg;
        Weapon wpn;
        String name;
        Discipline discipline;
        String line = overScan.nextLine();
        System.out.println("ch line");
        System.out.println(line);
        System.out.println("end ch line");
        /* read out a character in here*/
        Scanner scnr = new Scanner(line);
        name = scnr.next();
        discipline = searchDiscipline(scnr.next());
        x = scnr.nextInt(); y = scnr.nextInt(); hp = scnr.nextInt(); maxHp = scnr.nextInt(); xp = scnr.nextInt();
        maxXp = scnr.nextInt(); str = scnr.nextInt();
        dodge = scnr.nextInt(); hit = scnr.nextInt();
        skillPoint = scnr.nextInt(); actionPoints = scnr.nextInt(); damageMod = scnr.nextDouble(); moveDist = scnr.nextInt();
        extraDef = scnr.nextInt(); actionRegen = scnr.nextInt(); savedX = scnr.nextInt(); savedY = scnr.nextInt();
        helm = searchArmor(scnr.next()); torso = searchArmor(scnr.next()); leg = searchArmor(scnr.next());
        wpn = searchWpn(scnr.next());
        System.out.println("read in action: " + actionPoints);
        Character ch = new Character(name, discipline, hp, maxHp, xp, maxXp, str, dodge, hit, helm, torso, leg, wpn,
                moveDist, extraDef, actionPoints, actionRegen, skillPoint, damageMod);

        /* read out skill line */
        String skillLine = overScan.nextLine();
        System.out.println("skill line");
        System.out.println(skillLine);
        System.out.println("end skill line");
        if (!skillLine.equals("null"))
            constructTree(skillLine, ch.discipline);
        /* read out ability line */
        String abilityLine = overScan.nextLine();
        System.out.println("ab line");
        System.out.println(abilityLine);
        System.out.println("end ab line");
        if (!abilityLine.equals("null")) {
            scnr = new Scanner(abilityLine);
            while (scnr.hasNext()) {
                String ability = scnr.next();
                if (notPresent(ch, ability))
                    ch.discipline.abilities.add(searchAbility(ability));
            }
        }
        PlayerModel model = new PlayerModel(ch, x, y);
        model.setSavedCoords(savedX, savedY);
        String effectLine = overScan.nextLine();
        System.out.println("effect line");
        System.out.println(effectLine);
        System.out.println("end effect line");
        if (!effectLine.equals("null")){
            System.out.println("reading an effect");
            scnr = new Scanner(effectLine);
            while (scnr.hasNext()){
                String effName = scnr.next();
                String url = scnr.next();
                int timer = scnr.nextInt();
                int effect = scnr.nextInt();
                String type = scnr.next();
                Effect current = new Effect(model, timer, effName, url, effect);
                addEffect(model, current, type);
            }
        }
        return model;
    }

    private static EnemyModel readEnemy(Scanner overScan){
        int maxHp, maxXp, dodge, hit, str, moveDist, extraDef, actionPoints, actionRegen, skillPoint, hp, xp, x, y, savedX, savedY;
        double damageMod;
        Armor helm, torso, leg;
        Weapon wpn;
        String name;
        Discipline discipline;
        String line = overScan.nextLine();
        System.out.println("ch line");
        System.out.println(line);
        System.out.println("end ch line");
        /* read out a character in here*/
        Scanner scnr = new Scanner(line);
        name = scnr.next();
        discipline = searchDiscipline(scnr.next());
        x = scnr.nextInt(); y = scnr.nextInt(); hp = scnr.nextInt(); maxHp = scnr.nextInt(); xp = scnr.nextInt();
        maxXp = scnr.nextInt(); str = scnr.nextInt();
        dodge = scnr.nextInt(); hit = scnr.nextInt();
        skillPoint = scnr.nextInt(); actionPoints = scnr.nextInt(); damageMod = scnr.nextDouble(); moveDist = scnr.nextInt();
        extraDef = scnr.nextInt(); actionRegen = scnr.nextInt(); savedX = scnr.nextInt(); savedY = scnr.nextInt();
        helm = searchArmor(scnr.next()); torso = searchArmor(scnr.next()); leg = searchArmor(scnr.next());
        wpn = searchWpn(scnr.next());
        Character ch = new Character(name, discipline, hp, maxHp, xp, maxXp, str, dodge, hit, helm, torso, leg, wpn,
                moveDist, extraDef, actionPoints, actionRegen, skillPoint, damageMod);

        /* read out skill line */
        String skillLine = overScan.nextLine();
        System.out.println("skill line");
        System.out.println(skillLine);
        System.out.println("end skill line");
        if (!skillLine.equals("null"))
            constructTree(skillLine, ch.discipline);
        /* read out ability line */
        String abilityLine = overScan.nextLine();
        System.out.println("ab line");
        System.out.println(abilityLine);
        System.out.println("end ab line");
        if (!abilityLine.equals("null")) {
            scnr = new Scanner(abilityLine);
            while (scnr.hasNext()) {
                String ability = scnr.next();
                if (notPresent(ch, ability))
                    ch.discipline.abilities.add(searchAbility(ability));
            }
        }
        EnemyModel model = new EnemyModel(ch, x, y);
        String effectLine = overScan.nextLine();
        System.out.println("effect line");
        System.out.println(effectLine);
        System.out.println("end effect line");
        if (!effectLine.equals("null")){
            System.out.println("reading an effect");
            scnr = new Scanner(effectLine);
            while (scnr.hasNext()){
                String effName = scnr.next();
                String url = scnr.next();
                int timer = scnr.nextInt();
                int effect = scnr.nextInt();
                String type = scnr.next();
                Effect current = new Effect(model, timer, effName, url, effect);
                addEffect(model, current, type);
            }
        }
        return model;
    }

    private static ItemCard readItem(String line){
        System.out.println("reading item out of this line: " + line);
        String stripped = line.replaceAll(" ", "");
        Item item = searchArmor(stripped);
        item = item == null ? searchWpn(line) : item;
        return new ItemCard(item);
    }

    private static String removeSpace(String thing){
        return thing.replaceAll(" ", "");
    }

    private static String swapSpaceOut(String thing ) { return thing.replaceAll(" ", "*");}
    private static String swapSpaceIn(String thing ) { return thing.replaceAll("\\*", " ");}

    private static void addEffect(CharacterModel model, Effect effect, String type){
        effect.name = swapSpaceIn(effect.name);
        if (type.equals(EffectType.buffDamage.name))
            model.addEffect(new BuffDamage(effect));
        else if (type.equals(EffectType.buffDefense.name))
            model.addEffect(new BuffDefense(effect));
        else if (type.equals((EffectType.buffRange.name)));
            //model.addEffect(new BuffRana);
        else if (type.equals(EffectType.DOT.name))
            model.addEffect(new DOT(effect));
        else if (type.equals(EffectType.HOT.name))
            model.addEffect(new HOT(effect));
        else if (type.equals(EffectType.reduceMove.name))
            model.addEffect(new ReduceMove(effect));
    }
}

class StupidHolder{
    PartyWorldModel party;
    int row; int col;

    StupidHolder(PartyWorldModel party, int row, int col){
        this.party = party;
        this.row = row;
        this.col = col;
    }
}