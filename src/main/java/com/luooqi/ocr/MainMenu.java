package com.luooqi.ocr;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author huangluojun
 * @version 2.0.0
 * @date 2019年08月05日
 * @description: https://www.yiibai.com/javafx/javafx_menu.html
 * @see
 * @since JDK1.8
 **/
public class MainMenu extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {


        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 300, 250, Color.WHITE);

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);

        //
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #f5f5dc");
        tabPane.setPrefHeight(300);
        tabPane.setPrefWidth(300);

        Tab tab1 = new Tab("tab1");
        Tab tab2 = new Tab("tab2");
        Tab tab3 = new Tab("tab3");

        HBox hBox = new HBox(10);
        hBox.setStyle("-fx-background-color: #7fff00");
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(new Button("button1"), new Button("button2"));

        tab1.setContent(hBox);

        VBox vBox = new VBox(10);
        vBox.setStyle("-fx-background-color: #7f6984");
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(new Button("button3"), new Button("button4"));

        tab2.setContent(vBox);

        tabPane.getTabs().addAll(tab1,tab2,tab3);
        root.getChildren().addAll(tabPane);


        // File menu - new, save, exit
        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("New");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());


        fileMenu.getItems().addAll(newMenuItem, saveMenuItem,
                new SeparatorMenuItem(), exitMenuItem);

        Menu webMenu = new Menu("Web");
        CheckMenuItem htmlMenuItem = new CheckMenuItem("HTML");
        htmlMenuItem.setSelected(true);
        webMenu.getItems().add(htmlMenuItem);

        CheckMenuItem cssMenuItem = new CheckMenuItem("CSS");
        cssMenuItem.setSelected(true);
        webMenu.getItems().add(cssMenuItem);

        Menu sqlMenu = new Menu("SQL");
        ToggleGroup tGroup = new ToggleGroup();
        RadioMenuItem mysqlItem = new RadioMenuItem("MySQL");
        mysqlItem.setToggleGroup(tGroup);

        RadioMenuItem oracleItem = new RadioMenuItem("Oracle");
        oracleItem.setToggleGroup(tGroup);
        oracleItem.setSelected(true);

        sqlMenu.getItems().addAll(mysqlItem, oracleItem,
                new SeparatorMenuItem());

        Menu tutorialManeu = new Menu("Tutorial");
        tutorialManeu.getItems().addAll(
                new CheckMenuItem("Java"),
                new CheckMenuItem("JavaFX"),
                new CheckMenuItem("Swing"));

        sqlMenu.getItems().add(tutorialManeu);

        menuBar.getMenus().addAll(fileMenu, webMenu, sqlMenu);









        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
