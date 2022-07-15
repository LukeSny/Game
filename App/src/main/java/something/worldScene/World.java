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

import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import something.Runnable;
import something.*;
import something.battleScene.Grid;
import something.townScene.TownHub;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

import static something.Runnable.POPUP_SIZE;
import static something.Runnable.SCREEN_SIZE;


@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class World implements Serializable {

    private static final int SPACE_MOVED = SCREEN_SIZE /100 + 10;
    private static final int CHASE_DIST = 75;
    private static final int CLOCK_TIME =100;
    private static final int MAX_ENEMY_COUNT = 5;
    public static final int PANEL_LENGTH = 10;
    
    private boolean w;
    private boolean a;
    private boolean s;
    private boolean d;

    public TownHub townHub;
    public PlaceWorldModel townCard;

    public boolean stillMoving;

    private int clockCycles;

    public Stage primaryStage;

    public Panel currentPanel;
    public Panel[][] panels;
    public PartyWorldModel party;
    public Direction direction;

    private EnemyWorldModel currentFight;

    public PauseTransition pause;

    public int width;
    public int height;

    public World(PartyWorldModel party, Stage stage, boolean isSaved){
        w = false; a = false; s = false; d = false;
        stillMoving = true;
        this.party = party;
        primaryStage = stage;
        clockCycles = 0;
        panels = new Panel[PANEL_LENGTH][PANEL_LENGTH];
        initPanels();

        /*grad the user's current screen size*/
        GraphicsDevice gf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        width = gf.getDisplayMode().getWidth();
        height = gf.getDisplayMode().getHeight();
        System.out.println("width/height: " + width + " | " + height);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        String music = "adventureMusic.mp3";
//        Media sound = new Media(Runnable.class.getResource("adventureMusic.mp3").toExternalForm());
//        MediaPlayer player = new MediaPlayer(sound);
//        player.autoPlayProperty().set(true);
//        player.setVolume(.5);
//        player.setAutoPlay(true);
//        player.setRate(.5);
//        player.play();

        /* place the town into the scene*/
        if (!isSaved) {
            initCurrentPanel(PANEL_LENGTH/2 - 1, PANEL_LENGTH/2 - 1);
            townHub = new TownHub(this);
            townCard = new PlaceWorldModel("Town", "places/town.jpg");
            currentPanel.places.add(townCard);
            townCard.root.setTranslateX(SCREEN_SIZE / 2);
            townCard.root.setTranslateY(SCREEN_SIZE / 2);
            townCard.addEncounter(new Encounter());
            currentPanel.root.getChildren().add(townCard.root);

            PlaceWorldModel blackMesssage = EncounterMaker.make("BlackGuardMessage.txt", this, 4, 3);
            blackMesssage.root.setTranslateY(400);
            blackMesssage.root.setTranslateX(200);
            panels[3][4].places.add(blackMesssage);

            /*place test encounter into the scene*/
            try {
                PlaceWorldModel testModel = EncounterMaker.make("test.txt", this, PANEL_LENGTH/2 - 1, PANEL_LENGTH/2 - 1);
                testModel.root.setTranslateX(SCREEN_SIZE - 100);
                testModel.root.setTranslateY(100);
                currentPanel.places.add(testModel);
                currentPanel.root.getChildren().add(testModel.root);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        pause = new PauseTransition(Duration.millis(CLOCK_TIME));
        pause.setOnFinished(c -> {
            if(stillMoving) {
                //player control
                updateDirection();
                translate();
                checkTransition();


                //enemy movement
                moveEnemies();

                checkAllCollisions();
                if (clockCycles % 50 == 25)
                    generateEnemy();
                clockCycles++;

            }

            pause.playFromStart();
        });
        System.out.println("playing");
        pause.play();

    }

    public void initPanels(){
        for (int i = 0; i < PANEL_LENGTH; i++) {
            for (int j = 0; j < PANEL_LENGTH; j++) {
                Panel current = new Panel(i, j);
                panels[i][j] = current;
            }
        }
    }

    public void bindPanels(){
        primaryStage.getScene().setOnKeyPressed(this::handleKeyEvents);
        primaryStage.getScene().setOnKeyReleased(this::updateKeyReleased);
//        for (int i = 0; i < PANEL_LENGTH; i++) {
//            for (int j = 0; j < PANEL_LENGTH; j++) {
//                panels[i][j].scene.setOnKeyPressed(this::handleKeyEvents);
//                panels[i][j].scene.setOnKeyReleased(this::updateKeyReleased);
//                bindPanel(panels[i][j]);
//            }
//        }
    }
    private void bindPanel(Panel panel){
        primaryStage.getScene().setOnKeyPressed(this::handleKeyEvents);
        primaryStage.getScene().setOnKeyReleased(this::updateKeyReleased);
    }

    public void initCurrentPanel(int i, int j){
        currentPanel = panels[i][j];
        currentPanel.root.setFocusTraversable(true);
        direction = Direction.NONE;
        currentPanel.root.requestFocus();
        currentPanel.root.getChildren().add(party.root);
        primaryStage.setScene(new Scene(currentPanel.root));
        bindPanels();
    }

    public void initTown(){
        townHub = new TownHub(this);
    }

    public void checkAllCollisions() {
        for (EnemyWorldModel model : currentPanel.enemies){
            if(model.collisionBox.getBoundsInParent().intersects(party.collisionBox.getBoundsInParent()) && stillMoving){
                currentFight = model;
                model.enemies.forEach(this::sizeForBattle);
                Grid grid = new Grid(party.party, model, this);
                party.party.getModels().forEach(this::sizeForBattle);
                stillMoving = false;
            }
        }
        for (PlaceWorldModel place : currentPanel.places){
            if (place.collisionBox.getBoundsInParent().intersects(party.collisionBox.getBoundsInParent()) && stillMoving) {
                if (place.encounter.isTown) {
                    party.party.getModels().forEach(c -> c.sizingForMenu(height));
                    stillMoving = false;
                    townHub.keyBinds();
                    primaryStage.getScene().setRoot(townHub.root);
                    for (PlayerModel model : party.party.getModels()) {
                        model.fullHeal();
                    }
                }
                else {
                    System.out.println("place collision");
                    stillMoving = false;
                    place.encounter.next();
                }
            }
        }
    }

    private void moveEnemies(){
        for(WorldModel model : currentPanel.enemies){
            TranslateTransition translate = new TranslateTransition();
            translate.setInterpolator(Interpolator.LINEAR);
            translate.setNode(model.root);
            if (getDistance(model, party) < CHASE_DIST && stillMoving) {

                translate.setToX(party.root.getTranslateX());
                translate.setToY(party.root.getTranslateY());
                double time = getDistance(party, model);
                translate.setDuration(Duration.millis(time * SPACE_MOVED * 3));
                translate.play();
            }
            else if (clockCycles % 20 == 0){
                Random rand = new Random();
                double x = rand.nextDouble(model.root.getTranslateX() - 20, model.root.getTranslateX() + 20);
                double y = rand.nextDouble(model.root.getTranslateY() - 20, model.root.getTranslateY() + 20);
                translate.setToX(x);
                translate.setToY(y);
                translate.setDuration(Duration.millis(getDistance(x,y) * SPACE_MOVED * 4));
                translate.play();
            }


        }
    }

    private void generateEnemy(){
        if (currentPanel.enemies.size() >= MAX_ENEMY_COUNT) return;
        EnemyWorldModel enemy = Creator.generateRandomFactionEnemy();
        Random rand = new Random();
        while (true) {
            double x = rand.nextDouble(1, width - WorldModel.WIDTH);
            double y = rand.nextDouble(1, height - WorldModel.HEIGHT);
            if (getDistance(party, x, y) > CHASE_DIST){
                enemy.root.setTranslateX(x);
                enemy.root.setTranslateY(y);
                break;
            }
        }
        currentPanel.enemies.add(enemy);
        currentPanel.root.getChildren().add(enemy.root);

    }


    private void translate(){
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(party.root);
        translate.setDuration(Duration.millis(CLOCK_TIME));
        if(direction == Direction.NONE)
            return;
        if(direction == Direction.UP && spaceUp()) {
            translate.setToY(party.root.getTranslateY() - SPACE_MOVED);
            translate.setToX(party.root.getTranslateX());
        }
        if(direction == Direction.DOWN && spaceDown()) {
            translate.setToY(party.root.getTranslateY() + SPACE_MOVED);
            translate.setToX(party.root.getTranslateX());
        }
        if(direction == Direction.LEFT && spaceLeft()) {
            translate.setToX(party.root.getTranslateX() - SPACE_MOVED);
            translate.setToY(party.root.getTranslateY());
        }
        if(direction == Direction.RIGHT && spaceRight()) {
            translate.setToX(party.root.getTranslateX() + SPACE_MOVED);
            translate.setToY(party.root.getTranslateY());
        }
        if(direction == Direction.UP_LEFT) {
            moveUpLeft(translate);
        }
        if(direction == Direction.UP_RIGHT) {
            moveUpRight(translate);
        }
        if(direction == Direction.DOWN_LEFT) {
            moveDownLeft(translate);
        }
        if(direction == Direction.DOWN_RIGHT) {
            moveDownRight(translate);
        }

        translate.play();

    }
    private void handleKeyEvents(KeyEvent c){
        System.out.println("WorldKey Event: " + c.getCode());
        if (c.getCode() == KeyCode.M)
            map();
        if (c.getCode() == KeyCode.T)
            save();
        if (c.getCode() == KeyCode.R){
            World saved = Save.readSave(primaryStage);
            primaryStage.getScene().setRoot(saved.currentPanel.root);
            primaryStage.setFullScreen(true);
        }
        if (c.getCode() == KeyCode.ESCAPE){
            save();
            System.exit(0);
        }
        if (c.getCode() == KeyCode.Y){
            Save.writeSave(this, "defaultSave.txt");
        }
        if (c.getCode() == KeyCode.W)
            w =true;
        else if (c.getCode() == KeyCode.A)
            a = true;
        else if (c.getCode() == KeyCode.S)
            s = true;
        else if (c.getCode() == KeyCode.D)
            d = true;
    }
    private void updateKeyReleased(KeyEvent c){
        System.out.println("world key released: " + c.getCode());
        if (c.getCode() == KeyCode.W)
            w =false;
        else if (c.getCode() == KeyCode.A)
            a = false;
        else if (c.getCode() == KeyCode.S)
            s = false;
        else if (c.getCode() == KeyCode.D)
            d = false;
    }

    private void updateDirection(){
        //System.out.println("checking input: " + direction);
        if (!w && !a && !s && !d)
            direction = Direction.NONE;
        if (w)
            direction = Direction.UP;
        if(s)
            direction = Direction.DOWN;
        if(a)
            direction = Direction.LEFT;
        if(d)
            direction = Direction.RIGHT;
        if (w && a)
            direction = Direction.UP_LEFT;
        if (w && d)
            direction = Direction.UP_RIGHT;
        if (s && a)
            direction = Direction.DOWN_LEFT;
        if (s && d)
            direction = Direction.DOWN_RIGHT;

    }

    private boolean spaceUp(){
        return party.root.getTranslateY() > 0;
    }
    private boolean spaceLeft(){
        return party.root.getTranslateX() >0;
    }
    private boolean spaceDown(){
        return party.root.getTranslateY() < (height - WorldModel.HEIGHT);
    }
    private boolean spaceRight(){
        return party.root.getTranslateX() < (width - WorldModel.WIDTH);
    }

    private void moveUpLeft(TranslateTransition translate){
        if(spaceUp())
            translate.setToY(party.root.getTranslateY() - SPACE_MOVED);
        if(spaceLeft())
            translate.setToX(party.root.getTranslateX() - SPACE_MOVED);
        translate.play();
    }
    private void moveUpRight(TranslateTransition translate){
        if(spaceUp())
            translate.setToY(party.root.getTranslateY() - SPACE_MOVED);
        if(spaceRight())
            translate.setToX(party.root.getTranslateX() + SPACE_MOVED);
        translate.play();
    }
    private void moveDownLeft(TranslateTransition translate){
        if(spaceDown())
            translate.setToY(party.root.getTranslateY() + SPACE_MOVED);
        if(spaceLeft())
            translate.setToX(party.root.getTranslateX() - SPACE_MOVED);
        translate.play();
    }
    private void moveDownRight(TranslateTransition translate){
        if(spaceDown())
            translate.setToY(party.root.getTranslateY() + SPACE_MOVED);
        if(spaceRight())
            translate.setToX(party.root.getTranslateX() + SPACE_MOVED);
        translate.play();
    }

    private double getDistance(WorldModel mod1, WorldModel mod2){
        double x = mod1.root.getTranslateX() - mod2.root.getTranslateX();
        double y = mod1.root.getTranslateY() - mod2.root.getTranslateY();
        return Math.sqrt((x*x) + (y*y));

    }

    private double getDistance(double mod1, double mod2){
        double x = mod1 - mod2;
        double y = mod1 - mod2;
        return Math.sqrt((x*x) + (y*y));

    }

    public void setAllUp(){
        w = false;
        a = false;
        s = false;
        d = false;
    }

    public void reverseDirection(WorldModel place){
        double x = party.root.getTranslateX();
        double y = party.root.getTranslateY();
        int height = WorldModel.HEIGHT;
        int width = WorldModel.WIDTH;
        if(direction == Direction.NONE) {
            party.root.setTranslateX(place.root.getTranslateX() - width - 5);
            party.root.setTranslateY(place.root.getTranslateY());
        }
        if(direction == Direction.UP) {
            party.root.setTranslateY(y + height);
        }
        if(direction == Direction.DOWN) {
            party.root.setTranslateY(y - height);
        }
        if(direction == Direction.LEFT) {
            party.root.setTranslateX(x + width);
        }
        if(direction == Direction.RIGHT) {
            party.root.setTranslateX(x - width);
        }
        if(direction == Direction.UP_LEFT) {
            party.root.setTranslateX(x + width);
            party.root.setTranslateY(y + height);
        }
        if(direction == Direction.UP_RIGHT) {
            party.root.setTranslateX(x - width);
            party.root.setTranslateY(y + height);
        }
        if(direction == Direction.DOWN_LEFT) {
            party.root.setTranslateX(x + width);
            party.root.setTranslateY(y - height);
        }
        if(direction == Direction.DOWN_RIGHT) {
            party.root.setTranslateX(x - width);
            party.root.setTranslateY(y - height);
        }
    }

    public double getDistance(PartyWorldModel party, double x, double y){
        double xVal = party.root.getTranslateX() - x;
        double yVal = party.root.getTranslateY() - y;

        return Math.sqrt(xVal * xVal + yVal * yVal);
    }

    public void checkTransition(){
        int row = currentPanel.row;
        int col = currentPanel.col;
        if(!spaceUp()){
            if (currentPanel.row == 0) return;
            System.out.println("UP TRIG");
            currentPanel = panels[row-1][col];
            currentPanel.root.getChildren().remove(party);
            party.root.setTranslateY(height - WorldModel.HEIGHT - 5);
        }
        else if(!spaceDown()){
            if (currentPanel.row == PANEL_LENGTH-1) return;
            System.out.println("DOWN TRIG");
            currentPanel = panels[row+1][col];
            currentPanel.root.getChildren().remove(party);
            party.root.setTranslateY(15);
        }
        else if(!spaceLeft()){
            if (currentPanel.col == 0) return;
            System.out.println("LEFT TRIG");
            currentPanel = panels[row][col-1];
            currentPanel.root.getChildren().remove(party);
            party.root.setTranslateX(width - PartyWorldModel.WIDTH - 5);
        }
        else if(!spaceRight()){
            if (currentPanel.col == PANEL_LENGTH-1) return;
            System.out.println("RIGHT TRIG");
            currentPanel = panels[row][col+1];
            currentPanel.root.getChildren().remove(party);
            party.root.setTranslateX(15);
        }
        else
            return;

        currentPanel.root.getChildren().add(party.root);
        primaryStage.getScene().setOnKeyPressed(this::handleKeyEvents);
        primaryStage.getScene().setOnKeyReleased(this::updateKeyReleased);

        stillMoving = true;
        if (!currentPanel.hasCamp) {
            Random rand = new Random();
            if (rand.nextInt(3) == 1) {
                currentPanel.hasCamp = true;
                PlaceWorldModel place = Creator.createBanditCamp(this);
                currentPanel.places.add(place);
                int xCord = rand.nextInt(2 * WorldModel.WIDTH, width - 2 * WorldModel.WIDTH);
                int yCord = rand.nextInt(2 * WorldModel.HEIGHT, height - 2 * WorldModel.HEIGHT);
                place.root.setTranslateX(xCord);
                place.root.setTranslateY(yCord);
                currentPanel.root.getChildren().add(place.root);
            }
        }
        primaryStage.getScene().setRoot(currentPanel.root);
        primaryStage.setFullScreen(true);
    }
    
    public void leave(WorldModel place){
        reverseDirection(place);
        stillMoving = true;
        setAllUp();
        pause.playFromStart();
    }

    public void map(){
        stillMoving = false;
        VBox back = new VBox();
        GridPane grid = new GridPane();
        grid.setPrefSize(POPUP_SIZE - 10, POPUP_SIZE - 10);

        back.requestFocus();
        back.setOnKeyPressed(c -> {
            if (c.getCode() == KeyCode.Q){
                stillMoving = true;
                currentPanel.root.getChildren().remove(back);
            }
        });
        stylePopUp(back);
        back.setBackground(new Background(new BackgroundFill(Color.BLUE,
                CornerRadii.EMPTY,
                Insets.EMPTY)));
        grid.setAlignment(Pos.TOP_CENTER);
        double cellSize =  grid.getPrefWidth()/ PANEL_LENGTH;
        Node[][] map = Creator.makeMap(cellSize);
        for (int i = 0; i < PANEL_LENGTH; i++) {
            for (int j = 0; j < PANEL_LENGTH; j++) {
                grid.add(map[i][j], j, i);
            }
        }
        Rectangle marker = new Rectangle(cellSize/2, cellSize/2);
        marker.setFill(Color.RED);
        grid.add(marker, currentPanel.col, currentPanel.row);
        grid.setValignment(marker, VPos.CENTER);
        grid.setHalignment(marker, HPos.CENTER);
        System.out.println("Current Panel: " + currentPanel.panelCoords());
        Button exit = new Button("close map");
        exit.setOnAction(c -> {
            stillMoving = true;
            currentPanel.root.getChildren().remove(back);
        });
        currentPanel.root.getChildren().add(back);
        back.getChildren().addAll(grid, exit);
    }

    public void save(){
        try {
            Save.writeSave(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void moveBackToSaved(){
        for (PlayerModel model : party.party.getModels()){
            for (int i = 0; i < Runnable.NUM_ROWS; i++) {
                for (int j = 0; j < Runnable.NUM_COLS; j++){
                    if(party.party.getSavedSlots()[i][j] == null) continue;
                    if (model.getCharacter().name.equalsIgnoreCase(party.party.getSavedSlots()[i][j].getCharacter().name)){
                        model.setCoords(i, j);
                        model.getRoot().setTranslateX(0);
                        model.getRoot().setTranslateY(0);
                    }
                }
            }
        }
    }

    public void sizeForBattle(CharacterModel model){
        int tileWidth = width / Grid.GRID_COL;
        int tileHeight = height / Grid.GRID_ROWS;
        if (tileWidth > tileHeight){
            tileWidth = tileHeight;
        }
        model.getRoot().setPrefSize(tileWidth, tileHeight);
        model.defaultSizing();
    }


    public void enterWorld(){
        stillMoving = true;
        moveBackToSaved();
        direction = Direction.NONE;
        setAllUp();
        primaryStage.getScene().setRoot(currentPanel.root);
        bindPanel(currentPanel);
        System.out.println("entering world");
    }

    public void stylePopUp(VBox back){
        back.setBackground(new Background(new BackgroundFill(Color.WHITE,
                CornerRadii.EMPTY,
                Insets.EMPTY)));
        back.setMaxSize(width / 2, height / 2);
        back.setTranslateX(width / 4); back.setTranslateY(height / 4);
        back.setTranslateX(10); back.setTranslateY(10);
        back.setAlignment(Pos.CENTER);
    }

}