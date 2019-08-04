package com.luooqi.ocr;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.luooqi.ocr.controller.ProcessController;
import com.luooqi.ocr.model.CaptureInfo;
import com.luooqi.ocr.model.StageInfo;
import com.luooqi.ocr.snap.ScreenCapture;
import com.luooqi.ocr.utils.CommUtils;
import com.luooqi.ocr.utils.GlobalKeyListener;
import com.luooqi.ocr.utils.OcrUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

public class MainFm extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static StageInfo stageInfo;
    public static Stage stage;
    private static Scene mainScene;
    private static ScreenCapture screenCapture;
    private static ProcessController processController;
    private static TextArea textArea;
    //private static boolean isSegment = true;
    //private static String ocrText = "";

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        screenCapture = new ScreenCapture(stage);
        processController = new ProcessController();
        initKeyHook();

//        ToggleGroup segmentGrp = new ToggleGroup();
//        ToggleButton resetBtn = CommUtils.createToggleButton(segmentGrp, "resetBtn", this::resetText, "重置");
//        ToggleButton segmentBtn = CommUtils.createToggleButton(segmentGrp, "segmentBtn", this::segmentText, "智能分段");
//        resetBtn.setUserData("resetBtn");
//        segmentBtn.setUserData("segmentBtn");
//
//        segmentGrp.selectToggle(segmentBtn);
//        segmentGrp.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
//            isSegment = newValue.getUserData().toString().equals("segmentBtn");
//        });

        // 工具栏
        HBox topBar = new HBox(
                CommUtils.createButton("snapBtn", MainFm::doSnap, "截图"),
                CommUtils.createButton("openImageBtn", MainFm::recImage, "打开"),
                CommUtils.createButton("copyBtn", this::copyText, "复制"),
                CommUtils.createButton("pasteBtn", this::pasteText, "粘贴"),
                CommUtils.createButton("clearBtn", this::clearText, "清空"),
                CommUtils.createButton("wrapBtn", this::wrapText, "换行"),
                CommUtils.createButton("coordinateBtn", MainFm::coordinateSet, "坐标设置"),
                CommUtils.createButton("settingBtn", MainFm::paramSet, "参数设置")
                //CommUtils.SEPARATOR, resetBtn, segmentBtn
        );
        topBar.setId("topBar");
        topBar.setMinHeight(40);
        topBar.setSpacing(8);
        topBar.setPadding(new Insets(6, 8, 6, 8));

        //文字区域
        textArea = new TextArea();
        textArea.setId("ocrTextArea");
        textArea.setWrapText(true);
        textArea.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        textArea.setFont(Font.font("Arial", FontPosture.REGULAR, 14));

        // footerBar 下标
        ToolBar footerBar = new ToolBar();
        footerBar.setId("statsToolbar");
        Label statsLabel = new Label();
        SimpleStringProperty statsProperty = new SimpleStringProperty("总字数：0");
        textArea.textProperty().addListener((observable, oldValue, newValue) -> statsProperty.set("总字数：" + newValue.replaceAll(CommUtils.SPECIAL_CHARS, "").length()));
        statsLabel.textProperty().bind(statsProperty);
        footerBar.getItems().add(statsLabel);

        //三者所在位置之间的关系
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(textArea);
        root.setBottom(footerBar);

        //主题样式
        root.getStylesheets().addAll(
                getClass().getResource("/css/main.css").toExternalForm()
        );

        Tab tab1 = new Tab();
        tab1.setGraphic(root);
        tab1.setId("");


        TabPane tabPane = new TabPane(
                tab1
        );



        CommUtils.initStage(primaryStage);
        mainScene = new Scene(root, 670, 470);
        stage.setScene(mainScene);
        stage.show();
    }

    private void wrapText() {
        textArea.setWrapText(!textArea.isWrapText());
    }

    @Override
    public void stop() throws Exception {
        GlobalScreen.unregisterNativeHook();
    }

    private void clearText(){
        textArea.setText("");
    }

    private void pasteText() {
        String text = Clipboard.getSystemClipboard().getString();
        if (StrUtil.isBlank(text)) {
            return;
        }
        textArea.setText(textArea.getText()
                + (StrUtil.isBlank(textArea.getText()) ? "" : "\n")
                + Clipboard.getSystemClipboard().getString());
    }

    private void copyText(){
        String text = textArea.getSelectedText();
        if (StrUtil.isBlank(text)){
            text = textArea.getText();
        }
        if (StrUtil.isBlank(text)){
            return;
        }
        Map<DataFormat, Object> data = new HashMap<>();
        data.put(DataFormat.PLAIN_TEXT, text);
        Clipboard.getSystemClipboard().setContent(data);
    }

    public static void doSnap() {
        stageInfo = new StageInfo(stage.getX(), stage.getY(),
                stage.getWidth(), stage.getHeight(), stage.isFullScreen());
        System.out.println("---------------->>>>>>>>>>>>第一步："+stage.getX()+", "+stage.getY()+", "+
                stage.getWidth()+", "+stage.getHeight()+", "+stage.isFullScreen());
        runLater(screenCapture::prepareForCapture);
    }

    private static void recImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please Select Image File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile == null || !selectedFile.isFile()) {
            return;
        }
        stageInfo = new StageInfo(stage.getX(), stage.getY(),
                stage.getWidth(), stage.getHeight(), stage.isFullScreen());
        MainFm.stage.close();
        try {
            BufferedImage image = ImageIO.read(selectedFile);
            doOcr(image);
        } catch (IOException e) {
            StaticLog.error(e);
        }
    }

    public static void cancelSnap() {
        runLater(screenCapture::cancelSnap);
    }

    public static void doOcr(BufferedImage image){
        processController.setX(CaptureInfo.ScreenMinX + (CaptureInfo.ScreenWidth - 300)/2 );
        processController.setY(250);
        processController.show();
        Thread ocrThread = new Thread(()->{
            byte[] bytes = CommUtils.imageToBytes(image);
            String text = OcrUtils.ocrImg(bytes);
            Platform.runLater(()-> {
                processController.close();
                textArea.setText(text);
                restore(true);
            });
        });
        ocrThread.setDaemon(false);
        ocrThread.start();
    }

    public static void restore(boolean focus) {
        stage.setAlwaysOnTop(false);
        stage.setScene(mainScene);
        stage.setFullScreen(stageInfo.isFullScreenState());
        stage.setX(stageInfo.getX());
        stage.setY(stageInfo.getY());
        stage.setWidth(stageInfo.getWidth());
        stage.setHeight(stageInfo.getHeight());
        if (focus){
            stage.show();
            stage.requestFocus();
        }
        else{
            stage.close();
        }
    }

    /**
     * 截图坐标设定
     */
    public static void coordinateSet(){
        System.out.println("--------------->>>>>>>>>>>>坐标设置开始");
        stageInfo = new StageInfo(stage.getX(), stage.getY(),
                stage.getWidth(), stage.getHeight(), stage.isFullScreen());

        runLater(screenCapture::prepareForCapture);
    }

    /**
     * 全局参数设置
     *  1.截图频率
     *
     */
    public static void paramSet(){
        System.out.println("--------------->>>>>>>>>>>>全局参数设置开始");

    }
    private static void initKeyHook(){
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.WARNING);
            logger.setUseParentHandlers(false);
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
