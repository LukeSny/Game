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

package something.disciplines;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import something.Runnable;
import something.Save;
import something.worldScene.World;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PerkTree extends Application {
    ArrayList<PerkLayer> layers;
    public AnchorPane root = new AnchorPane();
    public Perk base;

    public PerkTree(Perk based){
        base = based;
        root = new AnchorPane();
        root.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        layers = new ArrayList<>();
    }
    public PerkTree(){}

    public void constructView(int width, int height){
        root.setPrefSize(width, height);
        root.getChildren().clear();
        plotPerks(width, height);
    }

    private void plotPerks(int width, int height) {
        int layerNum = 0;
        double layerHeight = height * .2;
        root.getChildren().add(base.getPerkImage());
        root.setMaxSize(width, height);
        base.root.setTranslateX(width / 2);
        base.root.setTranslateY(height - layerHeight);
        System.out.println("base coords: " + base.root.getTranslateX() + " | " + base.root.getTranslateY());
        placeChildren(height, layerNum+ 2, base, width);
    }


    private void placeChildren( int height, int layerNum, Perk node, int allowedWidth) {
        double layerHeight = height * .2;
        double centerX = node.root.getTranslateX();

        int widthStep = allowedWidth / node.unlocks.size();
        double numWidthstoMove = .5 * node.unlocks.size() - .5;
        double leftMostX = centerX - (widthStep * numWidthstoMove);
        for (int i = 0; i < node.unlocks.size(); i++) {
            Perk unlock = node.unlocks.get(i);

            //System.out.println("layerNum: " + layerNum);
            unlock.root.setTranslateX(i * widthStep + leftMostX);
            unlock.root.setTranslateY(height - (layerHeight * layerNum));
            //System.out.println("placing: " + unlock.name + " at " + unlock.root.getTranslateX() + " | " + unlock.root.getTranslateY());
            drawLine(node, unlock);
            root.getChildren().add(unlock.getPerkImage());
            if (unlock.unlocks.size() < 1)
                continue;
            placeChildren(height, layerNum+1, unlock, allowedWidth / node.unlocks.size());
        }
    }

    private void drawLine(Perk parent, Perk child){
        Line line = new Line(parent.root.getTranslateX(), parent.root.getTranslateY(), child.root.getTranslateX(),
                child.root.getTranslateY());
        root.getChildren().add(line);
    }

    public static void traverseTree(Perk start, Consumer<Perk> function){
        function.accept(start);
        for (Perk current : start.unlocks){
            traverseTree(current, function);
        }
    }


    private static class PerkLayer{
        ArrayList<Perk> perks;
        HBox root;

        public PerkLayer(ArrayList<Perk> perk){
            perks = perk;
            root = new HBox();
        }
        public void setPerkTranslates(int width){
            int step = width / perks.size();
            for (int i = 0; i < perks.size(); i++) {
                Perk current = perks.get(i);
                current.root.setTranslateX(step * i);
                root.getChildren().add(current.getPerkImage());
            }
        }
    }

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Perk one = new Perk("1");
        Perk two = new Perk("2");
        Perk three = new Perk("3");
        Perk four = new Perk("4");
        Perk five = new Perk("5");
        Perk six = new Perk("6");
        Perk seven = new Perk("7");
        Perk eight = new Perk("8");
        Perk nine = new Perk("9");
        Perk ten = new Perk("10");

        one.setUnlock(two); one.setUnlock(three);
        two.setUnlock(four); two.setUnlock(nine); two.setUnlock(ten);
        three.setUnlock(five); three.setUnlock(six);
        five.setUnlock(seven);
        five.setUnlock(eight);
        //six.setUnlock(eight);
        System.out.println("about to make tree");
        PerkTree tree = new PerkTree(one);
        tree.constructView(500, 500);

        System.out.println("two's unlocks");
        two.unlocks.forEach(c -> System.out.println(c.name));
        Scene scene = new Scene(tree.root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PoopTown");

        traverseTree(tree.base, new Consumer<Perk>() {
            @Override
            public void accept(Perk perk) {
                System.out.println(perk.name);
            }
        });

        primaryStage.show();
    }

}