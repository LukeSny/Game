module MyWork {
    requires java.base;
    requires java.desktop;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    exports something.battleScene;
    exports something.townScene;
    exports something.worldScene;
    exports something;


}