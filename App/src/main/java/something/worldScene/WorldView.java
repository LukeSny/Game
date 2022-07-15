package something.worldScene;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import something.Creator;
import something.Party;
import something.Runnable;

import java.util.ArrayList;

public class WorldView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinHeight(Runnable.SCREEN_SIZE);
        primaryStage.setMinWidth(Runnable.SCREEN_SIZE);
        primaryStage.setAlwaysOnTop(true);


        Creator.worldStart(primaryStage);
        primaryStage.show();

    }
}
