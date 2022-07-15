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

import something.battleScene.Grid;
import something.townScene.ItemCard;
import something.worldScene.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

enum State{
    Default,
    Place,
    Title,
    Text,
    Image,
    Button,
    Enemy
}

public class EncounterMaker {
    private static final ArrayList<EnemyModel> enemies = Creator.listAllEnemies();
    private static final ArrayList<ItemCard> items = Creator.createListOfLootItems();

    public static PlaceWorldModel make(String fileURL, World world, int row, int col) {
        try {
            System.out.println("running encounterMaker");
            Encounter en = new Encounter(world, fileURL, row, col);
            State state = State.Default;
            // create file from given fileURL
            Path resourceDirectory = Paths.get("App","src", "main", "resources", "encounters");
            String absolutePath = resourceDirectory.toFile().getAbsolutePath() + "\\" + fileURL;
            File file = new File(absolutePath);
            Scanner scnr = new Scanner(file);

            String tit = scnr.nextLine();
            String thing = scnr.nextLine();
            PlaceWorldModel place = new PlaceWorldModel(tit, thing);


            /*local variables needed for each screen, default values*/
            String title = "title not found";
            StringBuilder text = new StringBuilder();
            String url = "poop.jpg";
            String button = "button not found";
            String button2 = "button2 not found";
            boolean setButton1 = false;
            boolean setButton2 = false;
            int goldL = 0; int goldH = 1;


            ArrayList<EnemyModel> fighters = new ArrayList<>();
            ArrayList<ItemCard> loot = new ArrayList<>();
            int dif = 1;
            int yCord = 0;

            while (scnr.hasNextLine()) {
                String next = scnr.nextLine();
                if (next.length() < 1) continue;
                Scanner temp = new Scanner(next.substring(1));
                char let = next.charAt(0);
                /*all the possible state changes*/
                if (next.equalsIgnoreCase("*")) {
                    state = State.Title;
                } else if (next.equalsIgnoreCase("+")) {
                    state = State.Text;
                } else if (next.equalsIgnoreCase("&")) {
                    state = State.Image;
                } else if (next.equalsIgnoreCase("-")) {
                    state = State.Button;
                } else if (next.equalsIgnoreCase("^")) {
                    TextScreen screen = new TextScreen(title, url, text.toString(), button, place, world, en);
                    if (goldL != 0)
                        screen.addGold(goldL, goldH); goldL = 0; goldH = 1;
                    screen.addLoot(loot); loot = new ArrayList<>();
                    if (setButton2)
                        screen.addOption2(button2);
                    en.addText(screen);
                    setButton1 = false;
                    setButton2 = false;
                    text = new StringBuilder();
                    state = State.Default;
                } else if (let == '$') {
                    goldL = temp.nextInt();
                    goldH = temp.nextInt();
                } else if (let == '%') {
                    int num = 1;
                    StringBuilder line = new StringBuilder();
                    if (temp.hasNextInt())
                        num = temp.nextInt();
                    while (temp.hasNext()) {
                        line.append(temp.next());
                    }


                    for (int i = 0; i < num; i++) {
                        ItemCard item = EncounterMaker.matchItem(line.toString());
                        loot.add(item);
                    }
                }

                else if (next.equalsIgnoreCase("#") && state != State.Enemy) {
                        state = State.Enemy;
                    } else if ((next.equalsIgnoreCase("#") && state == State.Enemy)) {
                        EnemyWorldModel fight = new EnemyWorldModel(fighters, dif);
                        en.addFight(fight);
                        state = State.Default;
                    }


                    /*what happens in each state*/
                    else if (state == State.Title) {
                        title = next;
                        state = State.Default;
                    } else if (state == State.Text) {
                        text.append("\n").append(next);
                    } else if (state == State.Image) {
                        url = next;
                        state = State.Default;
                    } else if (state == State.Button) {
                        if (!setButton1) {
                            button = next;
                            setButton1 = true;
                        } else {
                            button2 = next;
                            setButton2 = true;
                        }
                    } else if (state == State.Enemy) {
                        char letter = next.charAt(0);
                        String line = next.substring(1);
                        Scanner scanning = new Scanner(line);
                        if (letter == '+') {
                            dif = scanning.nextInt();
                        } else if (letter == '-') {
                            int num = 1;
                            if (scanning.hasNextInt()) {
                                num = scanning.nextInt();
                                line = scanning.next();
                            }
                            for (int i = 0; i < num; i++) {
                                EnemyModel model = EncounterMaker.matchEnemy(line);
                                model.setY(yCord);
                                yCord = (yCord + 1) % Grid.GRID_ROWS;
                                fighters.add(model);
                            }
                        }
                    }
                    //System.out.println("EncounterMaker generated:\n" + title + "\n" + url + "\n" + text + "\n" + button + "\n" + button2);
                }

            //System.out.println("exited the loop");
            place.addEncounter(en);
            System.out.println("successfully made place with encounter");
            return place;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("why am I here");
        return null;
    }

    private static EnemyModel matchEnemy(String name){
        EnemyModel out = Creator.createEnemies().get(0);
        for (EnemyModel model : enemies){
            //System.out.println("Looking | found: " + name + " | " + model.getName());
            if (model.getName().equalsIgnoreCase(name)) {
                //System.out.println("found match!");
                return model.cloneObj();
            }
        }
        return out;
    }

    private static ItemCard matchItem(String name){
        for (ItemCard item : items){

            String check = item.getItem().name.replaceAll(" ", "");
            //System.out.println("Looking | found: " + name + " | " + check);
            if(check.equalsIgnoreCase(name)){
                //System.out.println("found match");
                return item.cloneObj();
            }
        }
        return items.get(0);
    }

}