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

package something.worldScene;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import something.Party;
import something.battleScene.Grid;

import java.io.Serializable;
import java.util.ArrayList;

public class Encounter implements Serializable {

    public ArrayList<TextScreen> texts;

    public ArrayList<EnemyWorldModel> fights;
    private int currentText;
    private int currentFight;
    public int index;
    public String sequence;
    public Pane panelRoot;
    Party party;
    Stage stage;
    public String makerUrl;
    public boolean isTown;
    World world;
    int row; int col;

    public Encounter(World wo, String url, int row, int col){
        System.out.println("Encounter row col: " + row + " | " + col);
        panelRoot = wo.panels[row][col].root;
        texts = new ArrayList<>();
        fights = new ArrayList<>();
        currentText = 0;
        currentFight = 0;
        index = 0;
        sequence = "";
        party = wo.party.party;
        stage = wo.primaryStage;
        makerUrl = url;
        isTown = false;
        world = wo;
        this.row = row; this.col = col;
    }
    /*this constructor is exclusively for the town*/
    public Encounter(){
        isTown = true;
    }

    public void setFights(ArrayList<EnemyWorldModel> fi){
        fights = fi;
    }
    public void setTexts(ArrayList<TextScreen> tex){
        texts = tex;
    }
    public void setArrays(ArrayList<TextScreen> tex, ArrayList<EnemyWorldModel> fi){
        texts = tex; fights = fi;
    }
    public void addText(TextScreen text){
        texts.add(text);
        sequence += "t";
    }
    public void addFight(EnemyWorldModel model){
        fights.add(model);
        sequence += "f";
        //System.out.println("add fight seq: " + sequence);
    }

    public void next() {
        System.out.println("Sequence: " + sequence);
        char letter = sequence.charAt(index);
        index++;
        System.out.println("letter: " + letter + "\nIndex/text/fight: " + index + "/" + currentText +"/" + currentFight);
        if (letter == 'f') {
            Grid grid = new Grid(party, fights.get(currentFight), world);
            currentFight++;
            party.getModels().forEach(c -> world.sizeForBattle(c));
        }
        else {
            TextScreen text = texts.get(currentText);
            currentText++;
            text.display();
        }
    }

//    public void setTranslations(){
//        for (PlayerModel model : party.getModels()){
//            for (int i = 0; i < Runnable.NUM_ROWS; i++) {
//                for (int j = 0; j < Runnable.NUM_COLS; j++){
//                    if (party.getSavedSlots()[i][j] == null) continue;
//                    if (model.getName().equalsIgnoreCase(party.getSavedSlots()[i][j].getName())){
//                        model.setCoords(i, j);
//                        model.getBackground().setTranslateX(i * Tile.SIZE);
//                        model.getBackground().setTranslateY(j * Tile.SIZE);
//                    }
//                }
//            }
//        }
//    }


}