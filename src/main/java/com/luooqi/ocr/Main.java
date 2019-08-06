package com.luooqi.ocr;

import com.luooqi.ocr.utils.CommUtils;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

/**
 * 测试用
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane anchorPane = new AnchorPane();

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
        TextArea textArea1 = new TextArea();
        textArea1.setId("ocrTextArea");
        textArea1.setWrapText(true);
        textArea1.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        textArea1.setFont(Font.font("Arial", FontPosture.REGULAR, 14));

        textArea1.setPrefHeight(50.0);
        //tab1.setContent(textArea1);

        AnchorPane anchorPane1 = new AnchorPane();
        anchorPane1.getChildren().addAll(new Button("test"));
        //tab的图标
        //tab1.setGraphic(anchorPane1);
        //tab不允许关闭
        tab1.setClosable(false);

        //三层版
        BorderPane borderPane = new BorderPane();
        //topBar 上标栏
        HBox topBar = new HBox(new Button("button1"), new Button("button2"));
        topBar.setId("topBar");
        topBar.setMinHeight(40);
        topBar.setSpacing(8);
        topBar.setPadding(new Insets(6, 8, 6, 8));

        // footerBar 下标
        ToolBar footerBar = new ToolBar();
        footerBar.setId("statsToolbar");
        Label statsLabel = new Label();
        SimpleStringProperty statsProperty = new SimpleStringProperty("总字数：0");
        textArea1.textProperty().addListener((observable, oldValue, newValue) -> statsProperty.set("总字数：" + newValue.replaceAll(CommUtils.SPECIAL_CHARS, "").length()));
        statsLabel.textProperty().bind(statsProperty);
        footerBar.getItems().add(statsLabel);

        borderPane.setTop(topBar);
        borderPane.setCenter(textArea1);
        borderPane.setBottom(footerBar);

        //tab1中设置面板
        tab1.setContent(borderPane);

        VBox vBox = new VBox(10);
        vBox.setStyle("-fx-background-color: #7f6984");
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(new Button("button3"), new Button("button4"));

        tab2.setContent(vBox);

        tabPane.getTabs().addAll(tab1,tab2,tab3);

        AnchorPane.setTopAnchor(tabPane,100.0);
        AnchorPane.setLeftAnchor(tabPane,100.0);

//        anchorPane.getChildren().addAll(tabPane);
//        Scene scene = new Scene(anchorPane);

        Scene scene = new Scene(tabPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("javaFXTest");
        primaryStage.setWidth(800);
        primaryStage.setHeight(800);

        primaryStage.show();

    }
//    @Override
//    public void start(Stage primaryStage) throws Exception{
//        //程序界面相关定义以及代码关联都在fmxl文件中， fxml可以采用Scene Builder的图形界面来构造
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//
//        //不引入fxml情况下，所有布局也可以通过代码实现
////        Scene scene = new Scene(root, 300, 275);
//        //scene样式
////        //scene.getStylesheets().add(Main.class.getResource("/css/jfoenix-components.css").toExternalForm());
//        //stage名字
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();
//    }



    //
}
