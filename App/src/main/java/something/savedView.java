package something;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import something.worldScene.World;

public class savedView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinHeight(Runnable.SCREEN_SIZE);
        primaryStage.setMinWidth(Runnable.SCREEN_SIZE);
        primaryStage.setAlwaysOnTop(true);


        World world = Save.readSave(primaryStage, "defaultSave.txt");
        world.stillMoving = true;
        world.party.root.setTranslateX(50);
        world.party.root.setTranslateY(50);
        System.out.println("currentPanPan: " + world.currentPanel.panelCoords());
        primaryStage.setTitle("PoopTown");

        primaryStage.show();
    }
}
