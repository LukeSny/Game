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

package something.townScene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import something.*;
import something.Runnable;
import something.battleScene.Tile;
import something.disciplines.Perk;
import something.disciplines.PerkTree;
import something.worldScene.World;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LevelUpScene extends TemplateScene{

    StackPane root;
    HBox backGround;
    VBox leftPanel;
    VBox middlePanel;
    VBox rightPanel;
    Label partyTitle;
    TilePane playerContainer;
    Label invTitle;
    TilePane inventory;
    ArrayList<PlayerCard> cards;
    Label itemDescription;

    Label movableTitle;
    GridPane playerLayout;
    Tile[][] tiles;
    ArrayList<PlayerModel> movables;

    PlayerCard selectedPlayer;
    PlayerModel movablePlayer;

    int movableRectSize;

    public LevelUpScene(TownHub hub, World world){
        super(hub, world);
        movableRectSize = (world.width / 3) / Runnable.NUM_COLS;
        root = new StackPane();
        root.setPrefSize(world.width, world.height);
        backGround = new HBox();
        root.getChildren().add(backGround);
        leftPanel = new VBox();
        leftPanel.setPrefSize(world.width / 3, world.height);
        giveBorder(leftPanel);

        leftPanel.getChildren().add(returnButton);
        middlePanel = new VBox();
        middlePanel.setPrefSize(world.width / 3, world.height);
        giveBorder(middlePanel);

        rightPanel = new VBox();
        rightPanel.setPrefSize(world.width / 3, world.height);
        giveBorder(rightPanel);


        cards = new ArrayList<>();
        partyTitle = new Label("Current Party Members");
        invTitle = new Label("Current Inventory");
        itemDescription = new Label();
        playerContainer = new TilePane();
        playerContainer.setPrefSize(world.width / 3, world.height / 2);
        playerContainer.setPrefColumns(Runnable.NUM_COLS);
        playerContainer.setPrefColumns(Runnable.NUM_ROWS);
        giveBorder(playerContainer);

        inventory = new TilePane();
        playerLayout = new GridPane();
        playerLayout.setPrefSize(world.width / 3, world.height / 2);
        giveBorder(playerLayout);
        movableTitle = new Label("Current Party Layout:");

        backGround.getChildren().addAll(leftPanel, middlePanel, rightPanel);

        leftPanel.getChildren().addAll(partyTitle, playerContainer, movableTitle, playerLayout);

        middlePanel.getChildren().addAll(invTitle, itemDescription, inventory);

        System.out.println("initializing the levelScene");
        initContainer();
        initInventory();
        initLayout();
        initBindings();
    }

    public void initContainer(){
        for(PlayerModel model : party.getModels()){
            model.sizingForMenu(townHub.world.height);
            PlayerCard card = new PlayerCard(model);
            cards.add(card);
            playerContainer.getChildren().add(card.root);
        }
    }

    public void initLayout(){
        tiles = new Tile[Runnable.NUM_ROWS][Runnable.NUM_COLS];
        movables = new ArrayList<>();
        for (int i = 0; i < Runnable.NUM_ROWS; i ++){
            for (int j = 0; j < Runnable.NUM_COLS; j++){
                Rectangle rect = new Rectangle(movableRectSize, movableRectSize);
                //TODO: change this
                tiles[i][j] = new Tile(i,j, movableRectSize, movableRectSize);
                rect.setStyle("-fx-background-color: #ff0000; -fx-border-style: solid; -fx-border-width: 5; -fx-border-color: white;");
            }
        }
        placeLayout();
    }

    public void initInventory(){
        for(ItemCard card : party.getItems()){
            System.out.println(card.item.name);
            inventory.getChildren().add(card.root);
        }
    }

    public void updateScene(){
        drawPlayers();
        drawInventory();
        placeLayout();
    }

    private void drawInventory() {
        inventory.getChildren().clear();
        if (party.getItems().isEmpty()) {
            invTitle.setText("Inventory: None");
            itemDescription.setText("");
        }
        else
            for(ItemCard card : party.getItems()){
                inventory.getChildren().add(card.root);
            }
            bindInventory();
    }

    private void drawPlayers() {
        playerContainer.getChildren().clear();
        cards.clear();
        for (PlayerModel model : party.getModels()) {
            model.sizingForMenu(townHub.world.height);
            PlayerCard card = new PlayerCard(model);
            if (card.canLevel){
                card.root.setBackground(new Background(new BackgroundFill(Color.YELLOW,
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
            }
            cards.add(card);
            playerContainer.getChildren().add(card.root);
        }
        bindTopCards();
    }

    /**
     * ok this is a big one, sets up basically all bindings for clicks and key presses
     */
    private void initBindings(){
        /* press q to go back to town hub */
        backGround.setOnKeyPressed(c -> {
            if (c.getCode() == KeyCode.Q){
                displayTownHub();
            }
            else if (c.getCode() == KeyCode.L && selectedPlayer != null && selectedPlayer.canLevel){
                levelUp(selectedPlayer.model);
            }
            else if (c.getCode() == KeyCode.DIGIT0){
                selectedPlayer.canLevel = true;
                selectedPlayer.model.getCharacter().xp.set(1000);
                drawPlayers();
            }
        });
        bindInventory();
        bindTopCards();
        bindMovables();

    }

    /**
     * bindings for everything within inventory
     * if hover over an item, display its image
     * if clicked and a player is selected, equip it
     */
    private void bindInventory() {
        for (ItemCard itemCard : party.getItems()){
            //when an item is hovered over, display its stats
            itemCard.root.hoverProperty().addListener(c -> {
                //itemDescription.getChildren().clear();
                String text = itemCard.item.description + "\n" + "Price: " + itemCard.item.price;
                if (itemCard.item instanceof Weapon) {
                    Weapon temp = (Weapon) itemCard.item;
                    text += "\nStrength: " + temp.damage;
                }
                if (itemCard.item instanceof Armor){
                    Armor temp = (Armor) itemCard.item;
                    text +="\nDefense: " + temp.def;
                }
                Label lbl = new Label(text);
                lbl.setWrapText(true);
                itemDescription.setText(text);
            });
            //when an item is clicked, and a player is selected, equip it to the selected player
            itemCard.root.setOnMouseClicked(c ->{
                if (selectedPlayer != null) {
                    if (itemCard.item instanceof Weapon){
                        System.out.println("found weapon: " + itemCard.item.name);
                            if(selectedPlayer.model.hasWeapon()){
                                System.out.println("found equiped weapon");
                                ItemCard temp = new ItemCard(selectedPlayer.model.getWeapon().cloneObj());
                                party.getItems().add(temp);
                            }
                            selectedPlayer.model.setWeapon((Weapon) itemCard.item);
                            equipItem(itemCard);
                    }
                    if (itemCard.item instanceof Armor){
                        System.out.println("found armor");
                        Armor armor = (Armor) itemCard.item;
                        if(armor.slot == Slot.Helmet) {
                            if (selectedPlayer.model.hasHelmet()) {
                                System.out.println("found equipped helmet");
                                selectedPlayer.model.setHelmet(armor);
                                ItemCard temp = new ItemCard(selectedPlayer.model.getHelmet().cloneObj());
                                party.getItems().add(temp);
                            }
                            selectedPlayer.model.setHelmet(armor);
                            equipItem(itemCard);
                        }
                        if(armor.slot == Slot.Torso) {
                            if (selectedPlayer.model.hasTorso()) {
                                System.out.println("found equipped Torso");
                                selectedPlayer.model.setTorso(armor);
                                ItemCard temp = new ItemCard(selectedPlayer.model.getTorso().cloneObj());
                                party.getItems().add(temp);
                            }
                            selectedPlayer.model.setTorso(armor);
                            equipItem(itemCard);
                        }
                        if(armor.slot == Slot.Legs) {
                            if (selectedPlayer.model.hasLegs()) {
                                System.out.println("found equipped Legs");
                                selectedPlayer.model.setLegs(armor);
                                ItemCard temp = new ItemCard(selectedPlayer.model.getLegs().cloneObj());
                                party.getItems().add(temp);
                            }
                            selectedPlayer.model.setLegs(armor);
                            equipItem(itemCard);
                        }
                    }
                }
            });
        }
    }

    private void equipItem(ItemCard itemCard) {
        party.getItems().remove(itemCard);
        inventory.getChildren().remove(itemCard);
        detailedCharView(selectedPlayer.model);
        bindInventory();
        drawInventory();
    }

    private void bindTopCards() {
        for (PlayerCard card : cards){
            card.root.setOnMouseClicked(c -> {
                if (c.getButton() == MouseButton.PRIMARY) {
                    selectedPlayer = card;
                    detailedCharView(card.model);
                }
                else if (c.getButton() == MouseButton.SECONDARY){

                }
            });
        }
    }

    /**
     everything to do with party arrangement, the cards down below are duplicates because FX can't have the same child twice
     movable refers to the duplicated card, NOT the original, findMatch is used to translate from dup to original
     */
    private void bindMovables() {
        for (PlayerModel model : movables){
            //if a card is clicked on, set its original to be selected
            model.getRoot().setOnMouseClicked(c -> {
                if (movablePlayer == null)
                    movablePlayer = findMatch(model);
                else{
                    PlayerModel mod = movablePlayer;
                    System.out.println("before move | clicked: " + mod.printCoords() + " | " + model.printCoords());
                    party.printSavedSlots();
                    PlayerModel temp = model.cloneObj();
                    party.getSavedSlots()[mod.getX()][mod.getY()] = model;
                    party.getSavedSlots()[model.getX()][model.getY()] = movablePlayer;

                    model.setCoords(mod.getX(), mod.getY());
                    mod.setCoords(temp.getX(), temp.getY());

                    System.out.println("after move | clicked: " + mod.printCoords() + " | " + model.printCoords());
                    party.printSavedSlots();
                    movablePlayer = null;
                    placeLayout();
                }
            });
        }
        /**
         * if an empty tile is clicked, and there's a selectedMovable, update the savedModel grid and the model's coords
         * then redraw the bottom
         */
        for (int i = 0; i < Runnable.NUM_ROWS; i++) {
            for (int j = 0; j < Runnable.NUM_COLS; j++) {
                Tile tile = tiles[i][j];
                tiles[i][j].getBack().setOnMouseClicked(c -> {
                    System.out.println(tile);
                    if (movablePlayer == null) return;
                    System.out.println("found " + movablePlayer.getName());
                    party.getSavedSlots()[movablePlayer.getX()][movablePlayer.getY()] = null;
                    party.getSavedSlots()[tile.getX()][tile.getY()] = movablePlayer;
                    movablePlayer.setX(tile.getX());
                    movablePlayer.setY(tile.getY());
                    movablePlayer = null;
                    placeLayout();
                });
            }
        }
    }

    private void gridAdd(PlayerModel model){
        playerLayout.add(model.getRoot(), model.getY(), model.getX());
    }
    private void gridAdd(Tile tile){
        playerLayout.add(tile.getBack(), tile.getY(), tile.getX());
    }
    private void gridAdd(Node node, int x, int y){
        playerLayout.add(node, y, x);
    }

    /**
     * redraws the bottom grid, starts by clearing everything
     * then adds all the blank tiles in
     * after that removes the tiles the players are in then adds the card
     */
    private void placeLayout(){
        playerLayout.getChildren().clear();
        for (int i = 0; i < Runnable.NUM_ROWS; i ++){
            for (int j = 0; j < Runnable.NUM_COLS; j++){
                gridAdd(tiles[i][j]);
            }
        }
        movables.clear();
        for (PlayerCard card : cards){
            PlayerModel newOne = card.model.cloneObj();
            newOne.getRoot().getChildren().removeAll(newOne.getHealth());
            movables.add(newOne);
        }
        for (PlayerModel model : movables){
            playerLayout.getChildren().remove(tiles[model.getX()][model.getY()].getBack());
            model.getRoot().setMaxSize(movableRectSize, movableRectSize);
            gridAdd(model);
            sizeForMovable(model);
        }
        bindMovables();
    }

    /**
     *
     * @param base the card that needs to be matched
     * @return the original card that base is a duplicate of
     */
    public PlayerModel findMatch(PlayerModel base){
        for (PlayerCard current : cards){
            if(base.getName().equals(current.model.getName()))
                return current.model;
        }
        //hopefully this never happens
        System.out.println("\n\n\n\n\nDID NOT FIND A MATCHING NAME IN FINDMATCH(levelUpScene)\n\n\n\n\n");
        return cards.get(0).model;
    }

    public void detailedCharView(PlayerModel model){
        rightPanel.getChildren().clear();
        Label name = new Label("Current Character: " + model.getName());
        Label stats = new Label("HP: " + model.getCharacter().hp.get() + "/" + model.getCharacter().maxHp +
                "\nstrength: " + model.getCharacter().strength+
                "\ndodge: " + model.getCharacter().dodge+
                "\nhit: " + model.getCharacter().hit);
        rightPanel.getChildren().addAll(name, stats);
        BorderPane equipment = new BorderPane();
        equipment.setPrefSize(Runnable.SCREEN_SIZE/2, Runnable.SCREEN_SIZE/2);
        equipment.setMaxHeight(Runnable.SCREEN_SIZE/2);

        ImageView head = new ImageView("items/head.jpg");
        if (model.hasHelmet())
            head.setImage(model.getHelmet().image.getImage());
        detailedSizeImage(head);
        head.setOnMouseClicked(c -> {
            if (model.hasHelmet()) {
                party.getItems().add(new ItemCard(model.getHelmet().cloneObj()));
                model.setHelmet(null);
                head.setImage(new Image("items/head.jpg"));
                drawInventory();
            }
        });

        ImageView leftHand = new ImageView("items/leftHand.jpg");
        detailedSizeImage(leftHand);

        ImageView torso = new ImageView("items/torso.png");
        if (model.hasTorso())
            torso.setImage(model.getTorso().image.getImage());
        detailedSizeImage(torso);
        torso.setOnMouseClicked(c -> {
            if (model.hasTorso()) {
                party.getItems().add(new ItemCard(model.getTorso().cloneObj()));
                model.setTorso(null);
                torso.setImage(new Image("items/torso.png"));
                drawInventory();
            }
        });

        ImageView rightHand = new ImageView("items/rightHand.jpg");
        if (model.hasWeapon())
            rightHand.setImage(model.getWeapon().image.getImage());
        detailedSizeImage(rightHand);
        rightHand.setOnMouseClicked(c -> {
            if (model.hasWeapon()) {
                party.getItems().add(new ItemCard(model.getWeapon().cloneObj()));
                model.setWeapon(null);
                rightHand.setImage(new Image("items/rightHand.jpg"));
                drawInventory();
            }
        });

        ImageView legs = new ImageView("items/legs.png");
        if (model.hasLegs())
            legs.setImage(model.getLegs().image.getImage());
        detailedSizeImage(legs);
        legs.setOnMouseClicked(c -> {
            if (model.hasLegs()) {
                party.getItems().add(new ItemCard(model.getLegs().cloneObj()));
                model.setLegs(null);
                legs.setImage(new Image("items/legs.png"));
                drawInventory();
            }
        });

        Button levelUp = new Button("Level Up");
        levelUp.setOnAction(c -> levelUp(model));
        if (model.canLevel())
            rightPanel.getChildren().add(levelUp);

        equipment.setTop(head); equipment.setLeft(leftHand); equipment.setRight(rightHand);
        equipment.setCenter(torso); equipment.setBottom(legs);
        rightPanel.getChildren().add(equipment);

        if (model.getCharacter().discipline.perkTree == null) return;
        System.out.println("adding model perk tree");
        model.getCharacter().discipline.perkTree.constructView((int) rightPanel.getPrefWidth(), (int) rightPanel.getPrefHeight() / 2);
        rightPanel.getChildren().add(model.getCharacter().discipline.perkTree.root);
        PerkTree.traverseTree(model.getCharacter().discipline.perkTree.base, new Consumer<Perk>() {
            @Override
            public void accept(Perk perk) {
                perk.root.setOnMouseClicked(c -> {
                    System.out.println("clicking perk");
                    if (model.getCharacter().skillPoint > 0){
                        System.out.println("unlocked perk");
                        perk.activate(model.getCharacter());
                        detailedCharView(model);
                    }
                });
            }
        });
    }

    public void detailedSizeImage(ImageView image){
        image.setFitHeight(PlayerCard.height);
        image.setFitWidth(PlayerCard.width);
        BorderPane.setAlignment(image, Pos.CENTER);
    }

    public void levelUp(PlayerModel model){
        VBox popUp = new VBox();
        townHub.world.stylePopUp(popUp);
        popUp.setAlignment(Pos.CENTER);
        root.getChildren().add(popUp);

        Label title = new Label(model.getName() + " has leveled up!");
        Label userHelp = new Label("Please select 2 attributes to increase");

        CheckBox hp = new CheckBox("hp + 5");
        CheckBox strength = new CheckBox("strength + 5");
        CheckBox dodge = new CheckBox("dodge + 5");
        CheckBox hit = new CheckBox("hit + 5");
        ArrayList<CheckBox> checkMarks = new ArrayList<>();
        checkMarks.add(hp);checkMarks.add(strength);checkMarks.add(dodge);checkMarks.add(hit);
        checkMarks.forEach(c -> c.setIndeterminate(false));

        Button confirm = new Button("confirm selection");
        confirm.setOnAction(c ->{
            int count = 0;
            for (CheckBox box : checkMarks){
                if (box.isSelected())
                    count++;
            }
            if (count == 2){
                if (hp.isSelected()) {
                    System.out.println("old health: " + model.getCharacter().maxHp);
                    model.getCharacter().maxHp += 5;
                    System.out.println("new health: " + model.getCharacter().maxHp);
                    model.fullHeal();
                }
                if (strength.isSelected()) {
                    model.getCharacter().strength += 5;
                }
                if(dodge.isSelected()) {
                    model.getCharacter().dodge += 5;
                }
                if(hit.isSelected()) {
                    model.getCharacter().hit += 5;
                }
                root.getChildren().remove(popUp);
                model.getCharacter().xp.set(model.getCharacter().xp.getValue() - model.getCharacter().maxXp);
                model.getCharacter().maxXp+=50;
                detailedCharView(model);
                updateScene();
            }
            model.getCharacter().skillPoint++;
        });

        popUp.getChildren().addAll(title, userHelp, hp, strength,dodge,hit, confirm);
    }

    private void giveBorder(Pane pane){
        pane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }
    private void sizeForMovable(PlayerModel model){
        model.getRoot().setMaxSize(movableRectSize, movableRectSize);
        model.getImage().setFitHeight(movableRectSize * .9);
        model.getImage().setFitWidth(movableRectSize);
        model.getNameLabel().setPrefSize(movableRectSize, movableRectSize * .1);
        giveBorder(model.getRoot());
    }

    private void sizeForPlayerContainer(PlayerCard card){
        sizeForMovable(card.model);
        card.model.getHealth().setPrefSize(movableRectSize, movableRectSize * .1);
        card.model.getHpBar().setHeight(movableRectSize * .1);
        card.xpContainer.setPrefSize(movableRectSize, movableRectSize * .1);
        card.xpRectangle.setHeight(movableRectSize * .1);
    }

}