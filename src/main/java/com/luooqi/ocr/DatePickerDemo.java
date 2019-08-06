package com.luooqi.ocr;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DatePickerDemo extends Application {


    @Override
    public void start(Stage stage) {

        FlowPane main = new FlowPane();
        main.setVgap(20);
        main.setHgap(20);


        DatePicker datePicker = new DatePicker();

        main.getChildren().add(datePicker);
        JFXDatePicker datePickerFX = new JFXDatePicker();

        main.getChildren().add(datePickerFX);
        datePickerFX.setPromptText("pick a date");
        JFXTimePicker blueDatePicker = new JFXTimePicker();
        blueDatePicker.setDefaultColor(Color.valueOf("#3f51b5"));
        blueDatePicker.setOverLay(true);
        main.getChildren().add(blueDatePicker);



        StackPane pane = new StackPane();
        pane.getChildren().add(main);
        StackPane.setMargin(main, new Insets(100));
        pane.setStyle("-fx-background-color:WHITE");

        final Scene scene = new Scene(pane, 400, 700);
        final ObservableList<String> stylesheets = scene.getStylesheets();
//        stylesheets.addAll(MainDemo.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
//                MainDemo.class.getResource("/css/jfoenix-design.css").toExternalForm());
        stage.setTitle("JFX Date Picker Demo");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
