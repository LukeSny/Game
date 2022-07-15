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

package something.battleScene;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import something.*;
import something.Character;
import something.Runnable;
import something.disciplines.Ability;
import something.disciplines.effects.Effect;
import something.townScene.ItemCard;
import something.worldScene.EnemyWorldModel;
import something.worldScene.World;

import java.util.ArrayList;

public class Grid {

    public static final int GRID_ROWS = 10;
    public static final int GRID_COL = 15;
    public AnchorPane gridView;
    public StackPane root;
    Party party;
    EnemyWorldModel enemies;

    public SimpleObjectProperty<PlayerModel> selectedModel;
    public SimpleObjectProperty<Tile> selectedEmpty;

    public Tile[][] emptyTiles;
    public CharacterModel[][] modelTiles;
    World world;
    int tileSize;
    int remainder;

    boolean stillFighting;
    boolean attackMode;
    boolean abilityMode;
    Ability selectedAbility;
    ArrayList<ItemCard> reclaimed;

    VBox sidePanel;

    /**
     * this class is the main driver for fights, which should probably be changed
     * has the underlying framework and visuals for the fightScene
     * also has logic to move PlayerModels based on user input
     *
     * @param pa user's party
     * @param en the enemies in this grid
     */
    public Grid(Party pa, EnemyWorldModel en, World world){
        this.world = world;
        root = new StackPane();
        root.setPrefSize(world.width, world.height);
        gridView = new AnchorPane();
        root.getChildren().add(gridView);
        System.out.println(pa.getModels());
        world.primaryStage.getScene().setRoot(root);

        selectedModel = new SimpleObjectProperty<>();
        selectedEmpty = new SimpleObjectProperty<>();
        emptyTiles = new Tile[GRID_ROWS][GRID_COL];
        modelTiles = new CharacterModel[GRID_ROWS][GRID_COL];

        sidePanel = new VBox();
        gridView.getChildren().add(sidePanel);

        party = pa;
        enemies = en;
        initModelGraph();
        buildMappy();

        for (EnemyModel enemyModel : enemies.getEnemies()){
            addEnemy(enemyModel);
        }

        selectedModel.addListener(c -> {
            if (selectedModel.get() == null)return;
            System.out.println("selected model moveDist: " + selectedModel.get().getCharacter().name + " " + selectedModel.get().moveDist());
            System.out.println("selected current action points: " + selectedModel.get().getCharacter().actionPoints);
            removeHighlight();
            highLightActionMove();
            updateSidePanel();
        });
        selectedEmpty.addListener(c-> moveSelected());

        stillFighting = true;
        abilityMode = false;
        attackMode = false;
        reclaimed = new ArrayList<>();
        initBindings();


    }


    /**
     * checks if a given tile has a model in the coresponding (x,y) coords in the modelTiles
     * @param tile that needs to be checked if its taken or not
     * @return boolean representing if the given tile is taken
     */
    public boolean tileTaken(Tile tile){
        return modelTiles[tile.x][tile.y] != null;
    }
    public void swapSpot(CharacterModel thing, Tile newSpot){
        modelTiles[thing.getX()][thing.getY()] = null;
        modelTiles[newSpot.x][newSpot.y] = thing;
    }

    /**
     * safer way to add player, adds it to the modelTiles as well as to the GridPane
     * @param player Model to be added
     */
    public void addPlayer(PlayerModel player){
        modelTiles[player.getX()][player.getY()] = player;
        gridAdd(player);
    }
    /**
     * safer way to add enemy, adds it to the modelTiles as well as to the GridPane
     * @param enemy Model to be added
     */
    public void addEnemy(EnemyModel enemy){
        System.out.println("enemy stats: " + enemy.getName() + " | " + enemy.getX() + " | " + enemy.getY());
        modelTiles[enemy.getX()][enemy.getY()] = enemy;
        gridAdd(enemy);
    }

    /**
     * creates the empty tiles for the game
     * adds them to the GridPane and to the emptyTiles array[][]
     */
    public void buildMappy(){
        for (int j = 0; j < GRID_COL; j++) {
            for (int i = 0; i < GRID_ROWS; i++) {
                Tile tile = new Tile(i, j, tileSize, tileSize);
                tile.back.setFill(Color.BLACK);
                tile.back.setId("empty");
                setInitialTranslate(tile);
                System.out.println("tile coord | tile trans: " + tile + ", (" + tile.getBack().getTranslateX() + ", " + tile.getBack().getTranslateY() + ")");
                if (!tileTaken(tile)){
                    gridView.getChildren().add(tile.back);
                }
                emptyTiles[i][j] = tile;
            }
        }
        System.out.println("empty grid");
        emptyTiles[GRID_ROWS-1][GRID_COL-1].back.setFill(Color.RED);
        emptyTiles[0][6].back.setFill(Color.PINK);
        emptyTiles[6][0].back.setFill(Color.ORANGE);
        printEmptyGrid();

        sidePanel.setPrefSize(remainder, world.height);
        //sidePanel.setTranslateX(0);

    }

    /**
     * initialize the grid's modelTiles to reflect the Players and Enemies of the grid
     */
    public void initModelGraph(){
        int tileWidth = world.width / GRID_COL;
        int tileHeight = world.height / GRID_ROWS;
        System.out.println("width/tile: " + world.width + " | " + tileWidth);
        System.out.println("height/tile: " + world.height + " | " + tileHeight);
        remainder = 0;
        if (tileWidth > tileHeight){
            remainder = (tileWidth - tileHeight) * GRID_COL;
            tileWidth = tileHeight;
        }
        tileSize = tileWidth;
        System.out.println("tileSize: " + tileSize);
        System.out.println("remainder: " + remainder);
        for (int i = 0 ; i < Runnable.NUM_ROWS; i++){
            for (int j = 0; j < Runnable.NUM_COLS; j++) {
                System.out.print(party.getSavedSlots()[i][j] + " | ");
                PlayerModel current = party.getSavedSlots()[i][j];
                if (current != null){
                    modelTiles[i][j] = current;
                    gridView.getChildren().add(current.getRoot());
                    current.setCoords(i, j);
                    setInitialTranslate(current);
                    current.getCharacter().actionPoints = 5;
                    updateSidePanel();
                }
            }
            System.out.println();
        }
        for (EnemyModel enemy : enemies.getEnemies()){
            modelTiles[enemy.getX()][enemy.getY()] = enemy;
            setInitialTranslate(enemy);
        }
    }

    /**
     * moves the selectedModel to the selectedTile if the tile is within the move distance
     * safeties on if the model or tile are null
     * at the end, set the selected tile to a not on grid tile so that it doesn't highlight weird things
     * stupid fix, but a fix nonetheless
     */
    public void moveSelected(){
        PlayerModel model = selectedModel.get();
        Tile empty = selectedEmpty.get();
        if (model == null) return;
        if (empty == null) return;

        long actionNeeded = Math.round(getDistance(model, empty) / model.getCharacter().moveDist);
        if (actionNeeded > model.getCharacter().actionPoints) return;
        model.getCharacter().actionPoints -= actionNeeded;
        updateSidePanel();
        int tempX = model.getX();
        int tempY = model.getY();

        //update modelTiles
        swapSpot(model, empty);


        TranslateTransition movement = new TranslateTransition();
        movement.setOnFinished(c -> gridView.getChildren().add(emptyTiles[tempX][tempY].back));
        movement.setInterpolator(Interpolator.LINEAR);
        movement.setToX(selectedEmpty.get().getBack().getTranslateX());
        movement.setToY(selectedEmpty.get().getBack().getTranslateY());
        movement.setNode(selectedModel.get().getRoot());
        movement.play();
        gridView.getChildren().remove(empty.back);
        model.setX(empty.x);
        model.setY(empty.y);

        Tile rand = new Tile(1000,1000, 0, 0);
        selectedEmpty.set(rand);

        //remove blue highlights
        removeHighlight();
        highLightActionMove();

        System.out.println("action points after move: " + model.getCharacter().actionPoints);
    }

    /**
     * sets all the empty tiles to be black
     */
    public void removeHighlight(){
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                emptyTiles[i][j].back.setFill(Color.BLACK);
            }
        }
    }

    public void highLightActionMove(){
        PlayerModel model = selectedModel.get();
        if (model == null) return;
        int actionCount = model.getCharacter().actionPoints;
        int movePerAction = model.getCharacter().moveDist;
        for (int count = actionCount; count > 0; count--) {
            for (int i = 0; i < GRID_ROWS; i++) {
                for (int j = 0; j < GRID_COL; j++) {
                    if (getDistance(model, emptyTiles[i][j]) <= (count * movePerAction)) {
                        //System.out.println("i, j, count, distance: " + i + " | " + j + " | " + count + " | " + getDistance(model, emptyTiles[i][j]));
                        Color color = Color.BLACK;
                        if (count > 4)
                            color = Color.DARKBLUE;
                        if (count == 4)
                            color = Color.DARKCYAN;
                        if (count == 3)
                            color = Color.BLUE;
                        if (count == 2)
                            color = Color.CORNFLOWERBLUE;
                        if (count == 1)
                            color = Color.LIGHTBLUE;
                        emptyTiles[i][j].back.setFill(color);
                    }
                }
            }
        }
    }

    public void highLightAbility(int num){
        PlayerModel model = selectedModel.get();
        if(model == null) return;
        //TODO: add a check to see if they can use the ability
        removeHighlight();
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                if (getDistance(model, emptyTiles[i][j]) <= num)
                    emptyTiles[i][j].back.setFill(Color.GREEN);
            }
        }
    }

    /**
     * highlights the empty tiles around the model to indicated where the model can attack
     * has safety  that check that a model is actually selected and that the model is currently able to attack
     */
    public void highlightAttack(){
        PlayerModel model = selectedModel.get();
        //safety checks
        if(model == null) return;
        System.out.println("attempting attack: " + model.getCharacter().canAttack());
        if(!model.getCharacter().canAttack())return;
        //attackMode = true;
        removeHighlight();
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                if (inAttackRange(model, emptyTiles[i][j]))
                    emptyTiles[i][j].back.setFill(Color.RED);
            }
        }
    }

    public void initBindings(){
        //all bindings that should happen for player
        party.getModels().forEach(c -> System.out.println("name: " + c.getCharacter().name));
        for (PlayerModel model : party.getModels()){
            model.getRoot().setOnMouseClicked(c -> {
                if(!stillFighting) return;
                if (abilityMode) {
                    selectedAbility.abilityAction(selectedModel.get(), model);
                    if (model.getCharacter().hp.getValue() <= 0)
                        killEntity(model);
                    abilityMode = false;
                    removeHighlight();
                    System.out.println("ability name: " + selectedAbility.name);
                    selectedModel.get().getCharacter().actionPoints -= selectedAbility.apCost;
                    System.out.println("reduced " + selectedModel.get().getName() + " ap by " + selectedAbility.apCost);
                    updateSidePanel();
                }
                else {
                    selectedModel.set(model);
                    attackMode = false;
                }
            });
        }
        //all bindings for enemies
        for (EnemyModel enemy : enemies.getEnemies()){
            enemy.getRoot().setOnMouseClicked(c -> {
                System.out.println("enemy clicked, attackMode = " + attackMode);
                if (attackMode && selectedModel.get().getCharacter().canAttack() && stillFighting) {
                    attack(selectedModel.get(), enemy);
                    attackMode = false;
                    removeHighlight();
                }
                else if (abilityMode && stillFighting){
                    selectedAbility.abilityAction(selectedModel.get(), enemy);
                    if (enemy.getCharacter().hp.getValue() <= 0)
                        killEntity(enemy);
                    removeHighlight();
                    selectedModel.get().getCharacter().actionPoints -= selectedAbility.apCost;
                    updateSidePanel();
                }
            });
        }

        //all bindings for empty tiles
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COL; j++) {

                Tile tile = emptyTiles[i][j];
                tile.back.setOnMouseClicked(c-> {
                    if(!stillFighting) return;
                    selectedEmpty.set(tile);
                });
            }
        }


        //this section is dedicated to quick bind keys

        world.primaryStage.getScene().setOnKeyReleased(c -> {
            System.out.println("battle key detected");
            PlayerModel selected = selectedModel.get();
            //end turn
            if (c.getCode() == KeyCode.Q) {
                if(!stillFighting) return;
                System.out.println("ending turn");
                endRound();
                attackMode = false;
            }
            else if (c.getCode() == KeyCode.DIGIT1){
                highLightActionMove();
            }
            //attack
            else if(c.getCode() == KeyCode.DIGIT2) {
                enterAttackMode(selected);
            }
            else if(c.getCode() == KeyCode.DIGIT3){
                if (selectedModel.get().getCharacter().discipline.abilities.size() > 0)
                    enterAbility1(selected);
            }
            else if(c.getCode() == KeyCode.DIGIT4){
                if (selectedModel.get().getCharacter().discipline.abilities.size() > 1)
                    enterAbility2(selected);
            }
            else if(c.getCode() == KeyCode.DIGIT5){
                if (selectedModel.get().getCharacter().discipline.abilities.size() > 2)
                    enterAbility3(selected);
            }
            else if (c.getCode() == KeyCode.DIGIT0)
                victory();
        });

    }

    private void enterAbility1(PlayerModel selected) {

        if (!stillFighting) return;
        if (selected == null) return;
        Ability temp = selected.getCharacter().discipline.abilities.get(0);
        doAbility(temp, selected);
    }

    private void enterAbility2(PlayerModel selected) {
        if (!stillFighting) return;
        if (selected == null) return;
        Ability temp = selected.getCharacter().discipline.abilities.get(1);
        doAbility(temp, selected);
    }

    private void enterAbility3(PlayerModel selected) {
        if (!stillFighting) return;
        if (selected == null) return;
        Ability temp = selected.getCharacter().discipline.abilities.get(2);
        doAbility(temp, selected);
    }

    private void doAbility(Ability temp, PlayerModel selected) {
        if (temp.abilityTimer != 0) return;
        if (temp.apCost > selectedModel.get().getCharacter().actionPoints) return;
        if (temp.apCost > selectedModel.get().getCharacter().actionPoints) return;
        selectedAbility = temp;
        //if its a self buff, just do the thing
        if (selectedAbility.selfBuff)
            selectedAbility.abilityAction(selected, null);
        abilityMode = true;
        attackMode = false;
        highLightAbility(selectedAbility.abilityRange);
//                   if (selectedModel.get().getCharacter().discipline.targetGrid)
//                       selected.getCharacter().discipline.ability(this);
        updateSidePanel();
    }

    private void enterAttackMode(PlayerModel selected) {
        if (!stillFighting) return;
        if (selected.getCharacter().canAttack()) {
            highlightAttack();
            attackMode = true;
            abilityMode = false;
        } else {
            attackMode = false;
            removeHighlight();
            highLightActionMove();
        }
    }


    /**
     * ends the current round, which involves
     * having each enemy move and/or attack
     * resetting each player's canMove and canAttack
     * clearing all highlights
     */
    public void endRound(){
        if (!stillFighting) return;
        System.out.println("ending turn");
        EnemyController enCon = new EnemyController(this);
        for (EnemyModel enemy : enemies.getEnemies()){
            //try and move
            enCon.enemyMovement(enemy);

            //attack if next to someone
            for (PlayerModel model : party.getModels()){
                if(inRange(enemy, model)) {
                    System.out.println("en: " + enemy.getCharacter().name + " model: " + model.getCharacter().name);
                    attack(enemy, model);
                    break;
                }

            }
            enemy.getCharacter().regenAction();
        }
        //activate status effects for everyone at the end of the round
        for (EnemyModel enemy : enemies.getEnemies()){
            enemy.playEffects();
        }
        for (PlayerModel model : party.getModels()){
            model.playEffects();
            model.getCharacter().regenAction();
            for (Ability ab : model.getCharacter().discipline.abilities){
                ab.reduceTimer();
            }
        }

        //printModelGrid();
        //printEmptyGrid();
        highLightActionMove();
        updateSidePanel();


    }

    /**
     * if the attacker is a PlayerModel, the disable it attacking again
     * if the defender's health goes below 1, remove them from the board and modelTiles
     * if there are no more enemies, call victory, basically locks everything up
     * @param attacker the Model that is attacking
     * @param defender the Model that is getting rekt m8
     */
    public TranslateTransition attack(CharacterModel attacker, CharacterModel defender){
        TranslateTransition attackAni = new TranslateTransition();
        if(inRange(attacker, defender)) {
            System.out.println("defended " + defender.getDefense() + " amount of damage");
            defender.getCharacter().takeDamage(attacker.getCharacter().attack() - defender.getDefense());

            attacker.getCharacter().actionPoints -= attacker.getCharacter().discipline.attackActionCost;
            updateSidePanel();

            attackAni.setFromX(attacker.getRoot().getTranslateX());
            attackAni.setFromY(attacker.getRoot().getTranslateY());
            attackAni.setToX(defender.getRoot().getTranslateX());
            attackAni.setToY(defender.getRoot().getTranslateY());
            attackAni.setCycleCount(2);
            attackAni.setInterpolator(Interpolator.LINEAR);
            attackAni.setAutoReverse(true);
            attackAni.setNode(attacker.getRoot());
            if (attacker instanceof PlayerModel) {
                attackAni.play();
            }
            System.out.println("defender's health after hit: " + defender.getCharacter().hp.getValue());
            if(defender.getCharacter().hp.getValue() <= 0 ) {
                killEntity(defender);
            }
        }
        return attackAni;
    }

    public void defeat(){
        VBox back = new VBox();
        world.stylePopUp(back);
        back.setAlignment(Pos.CENTER);

        ImageView skull = new ImageView("misc/skull.png");

        Label defeatLabel = new Label("Your soldiers lie dead at the hands of the enemy\nMay they find rest in the dirt");
        defeatLabel.setWrapText(true);
        root.getChildren().add(back);

        stillFighting = false;
        Button restart = new Button("restart");
        restart.setOnAction(c -> {
            Creator.worldStart(world.primaryStage);
        });

        back.getChildren().addAll(skull, defeatLabel, restart);
    }

    /**
     * locks everything up and prevents user from playing after the battle, adds a label indicating victory
     */
    public void victory(){

        stillFighting = false;
        world.currentPanel.enemies.remove(enemies);
        world.currentPanel.root.getChildren().remove(enemies.root);
        afterBattleUpdate();
    }

    private void afterBattleUpdate(){
        for (PlayerModel model : party.getModels()){
            model.getCharacter().giveXp(enemies.getXpReward() / party.getModels().size());
        }

        VBox back = new VBox();
        Label title = new Label("Victory!");
        Label xpGain = new Label("Xp for each fighter: " + enemies.getXpReward() / party.getModels().size());
        Label itemTitle = new Label("Items looted:");
        TilePane itemReward = new TilePane();

        Label reclaimedTitle = new Label("Gathered from the fallen:");
        TilePane reclaimedHolder = new TilePane();


        Button exit = new Button("Exit");

        back.setBackground(new Background(new BackgroundFill(Color.WHITE,
                CornerRadii.EMPTY,
                Insets.EMPTY)));
        back.setMaxSize(Runnable.SCREEN_SIZE / 3, Runnable.SCREEN_SIZE / 3);
        back.setAlignment(Pos.CENTER);

        back.getChildren().addAll(title, xpGain, itemTitle, itemReward);
        root.getChildren().add(back);
        if (enemies.getLootDrop().isEmpty())
            itemTitle.setText("Items Looted: None");
        for (ItemCard item : enemies.getLootDrop()){
            party.addItem(item);
            itemReward.getChildren().add(item.getRoot());
        }
        if (!reclaimed.isEmpty()) {
            for (ItemCard item : reclaimed) {
                party.addItem(item);
                reclaimedHolder.getChildren().add(item.getRoot());
            }
            back.getChildren().addAll(reclaimedTitle, reclaimedHolder);
        }
        back.getChildren().add(exit);

        exit.setOnAction(c -> {
            world.enterWorld();
        });
    }

    public boolean inRange(CharacterModel attacker, CharacterModel defender){
        System.out.println("dist: " + getDistance(attacker, defender) + " |range: " + Math.sqrt(attacker.range()*attacker.range() * 2) + .1);
        System.out.println("attacker x,y: " + attacker.getX() + "|" + attacker.getY());
        System.out.println("defender x,y: " + defender.getX() + "|" + defender.getY());
        System.out.println("result: " + (getDistance(attacker, defender) < Math.sqrt(attacker.range()*attacker.range() * 2) + .1));
        return getDistance(attacker, defender) < Math.sqrt(attacker.range()*attacker.range() * 2) + .1;
    }

    public void gridAdd(CharacterModel mod){
        double x = emptyTiles[mod.getX()][mod.getY()].back.getTranslateX();
        double y = emptyTiles[mod.getX()][mod.getY()].back.getTranslateY();
        gridView.getChildren().add(mod.getRoot());
        mod.getRoot().setTranslateX(x);
        mod.getRoot().setTranslateY(y);
    }

    public void gridAdd(Tile mod){
        gridView.getChildren().add(mod.back);
    }

    /**
     * safer way to remove a model from the grid
     * updates the modelTiles to reflect the removal
     * takes it out of the Gridpane and then adds the empty tile to replace it
     * @param thing the model to be removed
     */
    public void remove(CharacterModel thing){
        if (thing instanceof PlayerModel) {
            party.getModels().remove(thing);
        }
        else{
            enemies.getEnemies().remove(thing);
        }
        modelTiles[thing.getX()][thing.getY()] = null;
        gridView.getChildren().remove(thing.getRoot());
        gridAdd(emptyTiles[thing.getX()][thing.getY()]);
    }

    public boolean inMoveDist(CharacterModel model, Tile tile){
        return getDistance(model, tile) < Math.sqrt(model.moveDist() * model.moveDist() * 2) + .01;
    }
    public boolean inAttackRange(CharacterModel attacker, CharacterModel defender){
        return getDistance(attacker, defender) < Math.sqrt(attacker.range() * attacker.range() * 2) + .01;
    }
    public boolean inAttackRange(CharacterModel attacker, Tile tile){
        return getDistance(attacker, tile) < Math.sqrt(attacker.range() * attacker.range() * 2) + .01;
    }

    public double getDistance(CharacterModel thing1, Tile thing2){
        int x = thing2.getX() - thing1.getX();
        int y = thing2.getY() - thing1.getY();
        return Math.sqrt(x*x + y*y);
    }
    public double getDistance(CharacterModel thing1, CharacterModel thing2){
        int x = thing2.getX() - thing1.getX();
        int y = thing2.getY() - thing1.getY();
        return Math.sqrt(x*x + y*y);
    }

    /**
     * prints the current state of the underlying emptyTiles
     */
    public void printEmptyGrid(){
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                System.out.print(emptyTiles[i][j].x + ":" + emptyTiles[i][j].y + " | ");
            }
            System.out.println();
        }
    }

    /**
     * prints the current state of the underlying modelTiles
     */
    public void printModelGrid(){
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                if(modelTiles[i][j] != null )
                    System.out.print(modelTiles[i][j].getName() + " | ");
                else
                    System.out.print(modelTiles[i][j] + " | ");
            }
            System.out.println();
        }
    }


    public void setInitialTranslate(CharacterModel model){
        model.getRoot().setTranslateX(model.getY() * tileSize + remainder);
        model.getRoot().setTranslateY(model.getX() * tileSize);
    }

    public void setInitialTranslate(Tile tile){
        tile.getBack().setTranslateX(tile.getY() * tileSize + remainder);
        tile.getBack().setTranslateY(tile.getX() * tileSize);
    }

    private void killEntity(CharacterModel defender) {
        System.out.println("removing " + defender.getName());
        System.out.println("here here");
        remove(defender);
        if (defender instanceof PlayerModel) {
            if (defender.hasWeapon()) {
                reclaimed.add(new ItemCard(defender.getCharacter().weapon));
            }
            party.getModels().remove(defender);
            if (defender.equals(selectedModel.get())) {
                selectedModel.set(null);
                updateSidePanel();
            }
        }
        if (enemies.getEnemies().isEmpty()) {
            victory();
        }
        if (party.getModels().isEmpty()) {
            defeat();
        }
    }

    private void updateSidePanel(){
        if (selectedModel.get() == null) return;
        sidePanel.getChildren().clear();
        Label name = new Label(selectedModel.get().getName());
        Label ap = new Label("AP: " + selectedModel.get().getCharacter().actionPoints + "/" + Character.MAX_ACTION_POINT);
        AbilityBox move = new AbilityBox("move", 0,1, remainder, "ability/move.png");
        move.root.setOnMouseClicked(c -> highLightActionMove());
        AbilityBox attack = new AbilityBox("attack", 0,2, remainder, "ability/attackImage.png");
        attack.root.setOnMouseClicked(c -> {
            enterAttackMode(selectedModel.get());
        });
        sidePanel.getChildren().addAll(name, ap, move.root, attack.root);
        for (int i = 0; i < selectedModel.get().getCharacter().discipline.abilities.size(); i++) {
            Ability current = selectedModel.get().getCharacter().discipline.abilities.get(i);
            AbilityBox box = new AbilityBox(current.name, current.abilityTimer, i+3, remainder, current.imageURL);
            if (i == 0)
                box.root.setOnMouseClicked(c -> enterAbility1(selectedModel.get()));
            if (i == 1)
                box.root.setOnMouseClicked(c -> enterAbility2(selectedModel.get()));
            if (i == 2)
                box.root.setOnMouseClicked(c -> enterAbility3(selectedModel.get()));
            sidePanel.getChildren().add(box.root);
        }
        Rectangle divider = new Rectangle(remainder, 10);
        Label desc = new Label();
        sidePanel.getChildren().addAll(divider, desc);
        for (Effect effect : selectedModel.get().getEffects()){
            StatusBox status = new StatusBox(effect, desc, remainder);
            sidePanel.getChildren().add(status.root);
        }
    }

}

class AbilityBox{

    VBox root;
    ImageView image;
    StackPane imageStack;
    ImageView coolDownImage;

    AbilityBox(String name, int coolDown, int number, int width, String imageURL){
        root = new VBox();
        Label text = new Label(name + "[" + number + "]");
        image = new ImageView(imageURL);
        imageStack = new StackPane();
        coolDownImage = new ImageView();
        if (coolDown == 1)
            coolDownImage.setImage(new Image("ability/cooldown1.png"));
        else if (coolDown == 2)
            coolDownImage.setImage(new Image("ability/cooldown2.png"));
        else if (coolDown == 3)
            coolDownImage.setImage(new Image("ability/cooldown3.png"));
        else if (coolDown == 4)
            coolDownImage.setImage(new Image("ability/cooldown4.png"));
        imageStack.getChildren().addAll(image, coolDownImage);
        image.setFitWidth(width-2);
        image.setFitHeight(width-2);
        root.getChildren().addAll(text, imageStack);
        root.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }
}

class StatusBox{
    VBox root;

    StatusBox(Effect effect, Label effectDes, int width){
        root = new VBox();
        ImageView image = new ImageView(effect.imageURL);
        image.setFitWidth(width-2);
        image.setFitHeight(width-2);
        Label text = new Label(effect.name + "\n turns: " + effect.timer);
        text.setWrapText(true);
        root.getChildren().addAll(image, text);
        root.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        if (effect.description != null){
            root.hoverProperty().addListener(c -> {
                if (root.isHover())
                    effectDes.setText(effect.description);
                else
                    effectDes.setText("");
            });
        }
    }
}

