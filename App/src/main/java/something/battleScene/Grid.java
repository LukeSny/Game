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
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
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
import something.disciplines.Ability;
import something.disciplines.effects.Effect;
import something.townScene.ItemCard;
import something.worldScene.EnemyWorldModel;
import something.worldScene.World;

import java.util.ArrayList;

/**
 * main driver class for fights
 * grid and controller merged, so it is super long
 * governs both the visualization of the battleScene and the underlying logic to control the PlayerModels
 * EnemyController handles enemy movement and that is then utilized here
 */
public class Grid {
    /*dimensions of the battleField*/
    public static final int ROWS = 10;
    public static final int COLS = 15;
    /*basically the true root, 90% of the battle will interact with this*/
    public AnchorPane gridView;
    /*exists so the endBattle popUp appears in the middle of the screen*/
    public StackPane root;
    /*Player's party*/
    Party party;
    /*representation of the enemies, houses EnemyModel, lootDrop, xpReward, all that*/
    EnemyWorldModel enemies;

    /**
     * an ObjectProperty allows you to attach "listeners" to the variable
     * listeners run given code whenever the variable is reassigned, see each one's ".addListener" to see what happens
     *
     * selectedModel is the PlayerModel the user is currently interacting with
     */
    public SimpleObjectProperty<PlayerModel> selectedModel;

    /*underlying representation/storage of tiles*/
    public Tile[][] emptyTiles;
    /*underlying representation of both player and enemy models*/
    public CharacterModel[][] modelTiles;
    /*reference to the overWorld*/
    World world;
    /*size of each given tile*/
    int tileSize;
    /*size of the space left over on the sides of the screen after equalizing tile width and height*/
    int remainder;

    /*list of boolean values to denote game states*/
    /*stillFighting is true until either victory or defeat, on false locks most user interaction*/
    boolean stillFighting;
    /*denotes if the user has selected the attack option*/
    boolean attackMode;
    /*denotes if the user has selected any ability option*/
    boolean abilityMode;
    /*ability the user has selected to use*/
    Ability selectedAbility;
    /*list of items that are taken back after user's characters die*/
    ArrayList<ItemCard> reclaimed;

    /*sidePanel that shows character stats, abilities, and status effects*/
    VBox sidePanel;

    /**
     * initializes the fight by creating both 2d arrays and then setting the translations of all tiles and CharacterModels
     * @param pa user's party
     * @param en EnemyWorldModel that represents the enemy
     * @param world reference to the over-world
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
        emptyTiles = new Tile[ROWS][COLS];
        modelTiles = new CharacterModel[ROWS][COLS];

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
            updateSidePanel(selectedModel.get());
        });
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
    public boolean tileIsFree(Tile tile){
        return modelTiles[tile.x][tile.y] == null;
    }

    /**
     * update thing.x and thing.y to match the tile's x and y
     * @param thing CharacterModel to be updated
     * @param newSpot Tile that thing is moving to
     */
    public void swapSpot(CharacterModel thing, Tile newSpot){
        modelTiles[thing.getX()][thing.getY()] = null;
        modelTiles[newSpot.x][newSpot.y] = thing;
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
     * sets their translations with setInitialTranslate()
     * if the tiles x and y are occupied by a CharacterModel in the modelTiles[][], then dont add it to the scene
     */
    public void buildMappy(){
        for (int j = 0; j < COLS; j++) {
            for (int i = 0; i < ROWS; i++) {
                Tile tile = new Tile(i, j, tileSize, tileSize);
                tile.back.setFill(Color.BLACK);
                tile.back.setId("empty");
                setInitialTranslate(tile);
                System.out.println("tile coord | tile trans: " + tile + ", (" + tile.getBack().getTranslateX() + ", " + tile.getBack().getTranslateY() + ")");
                if (tileIsFree(tile)){
                    gridView.getChildren().add(tile.back);
                }
                emptyTiles[i][j] = tile;
            }
        }
        System.out.println("empty grid");
        emptyTiles[ROWS -1][COLS -1].back.setFill(Color.RED);
        emptyTiles[0][6].back.setFill(Color.PINK);
        emptyTiles[6][0].back.setFill(Color.ORANGE);
        printEmptyGrid();

        sidePanel.setPrefSize(remainder, world.height);

    }

    /**
     * initialize the grid's modelTiles to reflect the Players and Enemies of the grid
     * creates the tileSize and remainder that will be used to construct the rest of the screen
     */
    public void initModelGraph(){
        int tileWidth = world.width / COLS;
        int tileHeight = world.height / ROWS;
        System.out.println("width/tile: " + world.width + " | " + tileWidth);
        System.out.println("height/tile: " + world.height + " | " + tileHeight);
        remainder = 0;
        if (tileWidth > tileHeight){
            remainder = (tileWidth - tileHeight) * COLS;
            //noinspection SuspiciousNameCombination
            tileWidth = tileHeight;
        }
        tileSize = tileWidth;
        System.out.println("tileSize: " + tileSize);
        System.out.println("remainder: " + remainder);
        for (PlayerModel model : party.getModels()){
            setInitialTranslate(model);
            gridView.getChildren().add(model.getRoot());
            if (!world.justLoaded) {
                model.moveToSaved();
                model.getCharacter().actionPoints = 5;
            }
            modelTiles[model.getX()][model.getY()] = model;
            updateSidePanel(selectedModel.get());
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
    public void moveSelected(Tile empty){
        PlayerModel model = selectedModel.get();
        if (model == null) return;
        if (empty == null) return;

        long actionNeeded = Math.max(Math.round(getDistance(model, empty) / model.getCharacter().moveDist),1 );
        if (actionNeeded > model.getCharacter().actionPoints) return;
        model.getCharacter().actionPoints -= actionNeeded;
        updateSidePanel(selectedModel.get());
        int tempX = model.getX();
        int tempY = model.getY();

        //update modelTiles
        swapSpot(model, empty);


        TranslateTransition movement = new TranslateTransition();
        movement.setOnFinished(c -> gridView.getChildren().add(emptyTiles[tempX][tempY].back));
        movement.setInterpolator(Interpolator.LINEAR);
        movement.setToX(empty.getBack().getTranslateX());
        movement.setToY(empty.getBack().getTranslateY());
        movement.setNode(selectedModel.get().getRoot());
        movement.play();
        gridView.getChildren().remove(empty.back);
        model.setX(empty.x);
        model.setY(empty.y);

        //remove blue highlights
        removeHighlight();
        highLightActionMove();

        System.out.println("action points after move: " + model.getCharacter().actionPoints);
    }

    /**
     * sets all the empty tiles to be black
     */
    public void removeHighlight(){
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                emptyTiles[i][j].back.setFill(Color.BLACK);
            }
        }
    }

    /**
     * highlight each tile depending on how many action points it would take the current player to get there
     */
    public void highLightActionMove(){
        PlayerModel model = selectedModel.get();
        if (model == null) return;
        int actionCount = model.getCharacter().actionPoints;
        int movePerAction = model.getCharacter().moveDist;
        for (int count = actionCount; count > 0; count--) {
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
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

    /**
     * highlight around the selectedModel the range of the current selectedAbility is
     */
    public void highLightAbility(){
        PlayerModel model = selectedModel.get();
        if(model == null) return;
        //TODO: add a check to see if they can use the ability
        removeHighlight();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (getDistance(model, emptyTiles[i][j]) <= selectedAbility.abilityRange)
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
        if(!model.getCharacter().canAttack())return;
        removeHighlight();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (inAttackRange(model, emptyTiles[i][j]))
                    emptyTiles[i][j].back.setFill(Color.RED);
            }
        }
    }

    /**
     * massively important method, establishes most user interactions
     * first section binds each PlayerModel
     *          if in abilityMode, use the ability on the clicked playerModel
     *          else make them the selectedModel
     * second is enemyBind
     *          if clciked and an attack is valid, selectedModel attacks clicked enemy
     *          else if there is a valid ability, use ability on clicked enemy
     * third is each tile, if a valid move can be made, move selectedModel to the clicked tile
     * last section is for keyBinds
     */
    public void initBindings(){
        //all bindings that should happen for player
        party.getModels().forEach(c -> System.out.println("name: " + c.getCharacter().name));
        for (PlayerModel model : party.getModels()){
            model.getRoot().setOnMouseClicked(c -> {
                if(!stillFighting) return;
                if (abilityMode && selectedAbility.isReady()) {
                        selectedAbility.abilityAction(selectedModel.get(), model, party);
                        if (model.getCharacter().hp.getValue() <= 0)
                            killEntity(model);
                        abilityMode = false;
                        removeHighlight();
                        System.out.println("ability name: " + selectedAbility.name);
                        System.out.println("reduced " + selectedModel.get().getName() + " ap by " + selectedAbility.apCost);
                        updateSidePanel(selectedModel.get());
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
                enemy.getCharacter().strength = 0;
                System.out.println("clicked enemy ap: " + enemy.getCharacter().actionPoints);
                if (attackMode && selectedModel.get().getCharacter().canAttack() && stillFighting) {
                    attack(selectedModel.get(), enemy);
                    attackMode = false;
                    removeHighlight();
                }
                else if (abilityMode && stillFighting && selectedAbility.isReady()){
                    selectedAbility.abilityAction(selectedModel.get(), enemy, party);
                    if (enemy.getCharacter().hp.getValue() <= 0)
                        killEntity(enemy);
                    removeHighlight();
                    updateSidePanel(selectedModel.get());
                }
                else {
                    updateSidePanel(enemy);
                }
            });
        }

        //all bindings for empty tiles
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Tile tile = emptyTiles[i][j];
                tile.back.setOnMouseClicked(c-> {
                    if(!stillFighting) return;
                    moveSelected(tile);
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
                enterAttackMode();
            }
            else if(c.getCode() == KeyCode.DIGIT3){
                if (selectedModel.get().getCharacter().discipline.abilities.size() > 0)
                    enterAbility(selected, 0);
            }
            else if(c.getCode() == KeyCode.DIGIT4){
                if (selectedModel.get().getCharacter().discipline.abilities.size() > 1)
                    enterAbility(selected, 1);
            }
            else if(c.getCode() == KeyCode.DIGIT5){
                if (selectedModel.get().getCharacter().discipline.abilities.size() > 2)
                    enterAbility(selected, 2);
            }
            else if (c.getCode() == KeyCode.DIGIT0)
                victory();
            else if (c.getCode() == KeyCode.ESCAPE){
                openMenu();
            }
        });
    }

    /**
     * each of these is the same but for different highLights
     * @param selected the PlayerModel currently selected
     */
    private void enterAbility(PlayerModel selected, int abilityNumber) {
        if (!stillFighting) return;
        if (selected == null) return;
        Ability temp = selected.getCharacter().discipline.abilities.get(abilityNumber);
        if (temp.abilityTimer != 0) return;
        if (temp.apCost > selectedModel.get().getCharacter().actionPoints) return;
        if (temp.apCost > selectedModel.get().getCharacter().actionPoints) return;
        selectedAbility = temp;
        //if its a self buff, just do the thing
        abilityMode = true;
        attackMode = false;
        highLightAbility();
//                   if (selectedModel.get().getCharacter().discipline.targetGrid)
//                       selected.getCharacter().discipline.ability(this);
        updateSidePanel(selectedModel.get());
    }

    /**
     * sets the grid to be ready for the user to make an attack, highLights attack range for selectedModel
     */
    private void enterAttackMode() {
        if (!stillFighting) return;
        if (selectedModel.get().getCharacter().canAttack()) {
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
     * ends the current round, which involves having each enemy move and/or attack
     * enaching each status effect and regening AP
     * clearing all highlights
     */
    public void endRound(){
        if (!stillFighting) return;
        System.out.println("ending turn");
        EnemyController enCon = new EnemyController(this);
        if (world.oldAI){
            HoldenAI holdenAI =new HoldenAI(this);
        }
        for (EnemyModel enemy : enemies.getEnemies()){
            if (!world.oldAI) {
                //try and move
                enCon.enemyMovement(enemy);
                //attack if next to someone
                for (PlayerModel model : party.getModels()) {
                    if (inRange(enemy, model)) {
                        System.out.println("en: " + enemy.getCharacter().name + " model: " + model.getCharacter().name);
                        attack(enemy, model);
                        break;
                    }
                }
            }
            enemy.playEffects();
            killIfDead(enemy);
            enemy.getCharacter().regenAction();
            for (Ability ab : enemy.getCharacter().discipline.abilities){
                ab.reduceTimer();
            }
        }
        //activate status effects for everyone at the end of the round
        for (PlayerModel model : party.getModels()){
            model.playEffects();
            killIfDead(model);
            model.getCharacter().regenAction();
            for (Ability ab : model.getCharacter().discipline.abilities){
                ab.reduceTimer();
            }
        }

        //printModelGrid();
        //printEmptyGrid();
        highLightActionMove();
        updateSidePanel(selectedModel.get());


    }

    /**
     * if the defender's health goes below 1, remove them from the board and modelTiles
     * if there are no more enemies, call victory, basically locks everything up
     * @param attacker the Model that is attacking
     * @param defender the Model that is getting rekt m8
     */
    public TranslateTransition attack(CharacterModel attacker, CharacterModel defender){
        TranslateTransition attackAni = new TranslateTransition();
        if(inRange(attacker, defender)) {
            setUpAttack(attacker, defender, attackAni);
        }
        return attackAni;
    }

    public void justAttack(CharacterModel attacker, CharacterModel defender){
        TranslateTransition attackAni = new TranslateTransition();
        if(inRange(attacker, defender)) {
            setUpAttack(attacker, defender, attackAni);
            attackAni.play();
        }
    }


    private void setUpAttack(CharacterModel attacker, CharacterModel defender, TranslateTransition attackAni) {
        System.out.println("defended " + defender.getDefense() + " amount of damage");
        defender.getCharacter().takeDamage(attacker.getCharacter().attack() - defender.getDefense());

        attacker.getCharacter().actionPoints -= attacker.getCharacter().discipline.attackActionCost;
        System.out.println("attacker action after defense: " + attacker.getName() + " | " + attacker.getCharacter().actionPoints);
        updateSidePanel(selectedModel.get());

        attackAni.setFromX(attacker.getRoot().getTranslateX());
        attackAni.setFromY(attacker.getRoot().getTranslateY());
        attackAni.setToX(defender.getRoot().getTranslateX());
        attackAni.setToY(defender.getRoot().getTranslateY());
        attackAni.setCycleCount(2);
        attackAni.setInterpolator(Interpolator.LINEAR);
        attackAni.setAutoReverse(true);
        attackAni.setNode(attacker.getRoot());


        System.out.println("defender's health after hit: " + defender.getCharacter().hp.getValue());
        killIfDead(defender);
    }

    private void killIfDead(CharacterModel entity){
        if(entity.getCharacter().hp.getValue() <= 0 ) {
            killEntity(entity);
        }
    }

    /**
     * plays when all PlayerModels are killed, needs to be made better but... meh just dont lose lol
     */
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
     * locks everything up and prevents user from playing after the battle
     */
    public void victory(){

        stillFighting = false;
        world.currentPanel.enemies.remove(enemies);
        world.currentPanel.root.getChildren().remove(enemies.root);
        afterBattleUpdate();
    }

    /**
     * adds a screen that displays loot and a button to go back to over world
     * gives each character the xp reward, adds gold and item loot to party
     */
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
        world.stylePopUp(back);

        back.getChildren().addAll(title, xpGain, itemTitle, itemReward);
        gridView.getChildren().add(back);
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

        exit.setOnAction(c -> world.enterWorld());
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
     * takes it out of the GridPane and then adds the empty tile to replace it
     * @param thing the model to be removed
     */
    @SuppressWarnings("SuspiciousMethodCalls")
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
    /**past this point is mostly just helper methods for the ones above*/


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
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                System.out.print(emptyTiles[i][j].x + ":" + emptyTiles[i][j].y + " | ");
            }
            System.out.println();
        }
    }

    /**
     * prints the current state of the underlying modelTiles
     */
    public void printModelGrid(){
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
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
                updateSidePanel(selectedModel.get());
            }
        }
        if (enemies.getEnemies().isEmpty()) {
            victory();
        }
        if (party.getModels().isEmpty()) {
            defeat();
        }
    }

    public void openMenu(){
        stillFighting = false;
        VBox menu = new VBox();
        world.stylePopUp(menu);
        Label title = new Label("Paused");
        Button close = new Button("close");
        Button saveNquit = new Button("Save and Quit");
        menu.getChildren().addAll(title, saveNquit, close);
        close.setOnAction(c -> {
            stillFighting = true;
            gridView.getChildren().remove(menu);
        });
        saveNquit.setOnAction(c -> {
            world.save();
            System.exit(0);
        });
        gridView.getChildren().add(menu);
    }

    /**
     * exception to the helper methods down here, updates the sidePanel to reflect the status of selectedModel
     * displays selectedModel's name, ap, abilities, and status effects
     */
    private void updateSidePanel(CharacterModel model){
        if (model == null) return;
        sidePanel.getChildren().clear();
        Label name = new Label(model.getName());
        Label ap = new Label("AP: " + model.getCharacter().actionPoints + "/" + Character.MAX_ACTION_POINT);
        sidePanel.getChildren().addAll(name, ap);
        /* if the model is a player, show move, attack, and abilities*/
        if (model instanceof PlayerModel model1) {
            AbilityBox move = new AbilityBox("move", 0, 1, remainder, "ability/move.png");
            move.root.setOnMouseClicked(c -> highLightActionMove());
            AbilityBox attack = new AbilityBox("attack", 0, 2, remainder, "ability/attackImage.png");
            attack.root.setOnMouseClicked(c -> {
                enterAttackMode();
            });
            sidePanel.getChildren().addAll(move.root, attack.root);
            for (int i = 0; i < model.getCharacter().discipline.abilities.size(); i++) {
                Ability current = model.getCharacter().discipline.abilities.get(i);
                AbilityBox box = new AbilityBox(current.name, current.abilityTimer, i + 3, remainder, current.imageURL);
                if (i == 0)
                    box.root.setOnMouseClicked(c -> enterAbility(model1, 0));
                if (i == 1)
                    box.root.setOnMouseClicked(c -> enterAbility(model1, 1));
                if (i == 2)
                    box.root.setOnMouseClicked(c -> enterAbility(model1, 2));
                sidePanel.getChildren().add(box.root);
            }
            Rectangle divider = new Rectangle(remainder, 10);
            sidePanel.getChildren().add(divider);
        }
        Label desc = new Label();
        sidePanel.getChildren().add(desc);
        for (Effect effect : model.getEffects()){
            StatusBox status = new StatusBox(effect, desc, remainder);
            sidePanel.getChildren().add(status.root);
        }
    }

}
/**both of these classes are helper classes for the updateSidePanel()*/
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
        coolDownImage.setFitWidth(width-2);
        coolDownImage.setFitHeight(width-2);
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

