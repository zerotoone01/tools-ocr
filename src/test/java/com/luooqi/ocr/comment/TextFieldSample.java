package com.luooqi.ocr.comment;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @version 2.0.0
 * @date 2019年08月06日
 * @description: http://www.javafxchina.net/blog/2015/04/doc03_textfield/
 * @see
 * @since JDK1.8
 **/
public class TextFieldSample extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {

        //创建GridPane容器
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

//定义Name Text Field
        final TextField name = new TextField();
        name.setPromptText("Enter your first name.");
        GridPane.setConstraints(name, 0, 0);
        grid.getChildren().add(name);

//定义Last Name Text Field
        final TextField lastName = new TextField();
        lastName.setPromptText("Enter your last name.");
        GridPane.setConstraints(lastName, 0, 1);
        grid.getChildren().add(lastName);

//定义Comment Text Field
        final TextField comment = new TextField();
        comment.setPromptText("Enter your comment.");
        GridPane.setConstraints(comment, 0, 2);
        grid.getChildren().add(comment);

//定义Submit Button
        Button submit = new Button("Submit");
        GridPane.setConstraints(submit, 1, 0);
        grid.getChildren().add(submit);

//定义Clear Button
        Button clear = new Button("Clear");
        GridPane.setConstraints(clear, 1, 1);
        grid.getChildren().add(clear);



        //添加一个Label
        final Label label = new Label();
        GridPane.setConstraints(label, 0, 3);
        GridPane.setColumnSpan(label, 2);
        grid.getChildren().add(label);

        submit.setOnAction((ActionEvent e) -> {
            if (
                    (comment.getText() != null && !comment.getText().isEmpty())
            ) {
                label.setText(name.getText() + " " +
                        lastName.getText() + ", "
                        + "thank you for your comment!");
            } else {
                label.setText("You have not left a comment.");
            }
        });

        clear.setOnAction((ActionEvent e) -> {
            name.clear();
            lastName.clear();
            comment.clear();
            label.setText(null);
        });

        //Label和Text Field
        Label label1 = new Label("Name:");
        TextField textField = new TextField ();
        HBox hb = new HBox();
        hb.getChildren().addAll(label1, textField);
        hb.setSpacing(10);
        GridPane.setConstraints(hb, 0, 4);
        grid.getChildren().add(hb);

        Scene scene = new Scene(grid);
        primaryStage.setTitle("TextFieldTest");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
