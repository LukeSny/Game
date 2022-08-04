package something;

import javafx.application.Application;
import javafx.stage.Stage;
import something.worldScene.World;

public class AITester extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinHeight(Runnable.SCREEN_SIZE);
        primaryStage.setMinWidth(Runnable.SCREEN_SIZE);
        primaryStage.setAlwaysOnTop(true);


        World world = Save.readSave(primaryStage, "AITester.txt");
        world.stillMoving = true;
        world.oldAI = true;
        System.out.println("currentPanPan: " + world.currentPanel.panelCoords());
        primaryStage.setTitle("PoopTown");

        primaryStage.show();
    }
}
