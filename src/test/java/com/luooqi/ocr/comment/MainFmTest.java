package com.luooqi.ocr.comment;

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
import javafx.util.Callback;
import org.jnativehook.GlobalScreen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

/**
 * table分为三个标签：
 *      1.文字识别(开源代码原有功能)
 *      2.参数设置(1.自由截图获取位置坐标，并显示在对应的框中  2.对应框的数据可编辑， 3.保存或者取消参数按钮)
 *      3.历史参数(1.展示历史参数， 2.历史参数可以编辑 3.删除)
 */
public class MainFmTest extends Application {

    private final  static TabPane tabPane = new TabPane();



    private static StageInfo stageInfo;
    public static Stage stage;
    private static Scene mainScene;
    private static ScreenCapture screenCapture;
    private static ProcessController processController;
    private static TextArea textArea;
    //private static boolean isSegment = true;
    //private static String ocrText = "";
    private final static int paramSettingColumnWith = 80;

    //时间设置
    private DatePicker checkInDatePicker;
    private DatePicker checkOutDatePicker;
    //开始时间和结束时间最小时间间隔， 单位分钟
    private final static long minTimeGap = 5;

    public static void main(String[] args) {
        launch(args);
    }

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

        //初始化面板
        tabPane.setStyle("-fx-background-color: #f5f5dc");
        tabPane.setPrefHeight(300);
        tabPane.setPrefWidth(300);

        //ocr tab
        Tab ocrTab = new Tab("ocr操作");
        //参数设置门tab
        Tab paramSettingTab = new Tab("参数设置");
        // 历史数据 tab
        Tab historyDataTab = new Tab("历史参数");

        ocrTab.setClosable(false);
        paramSettingTab.setClosable(false);
        historyDataTab.setClosable(false);

        //tab标签下对应的面板
        BorderPane ocrPane = new BorderPane();




        // 工具栏
        HBox topBar = new HBox(
                CommUtils.createButton("snapBtn", MainFmTest::doSnap, "截图"),
                CommUtils.createButton("openImageBtn", MainFmTest::recImage, "打开"),
                CommUtils.createButton("copyBtn", this::copyText, "复制"),
                CommUtils.createButton("pasteBtn", this::pasteText, "粘贴"),
                CommUtils.createButton("clearBtn", this::clearText, "清空"),
                CommUtils.createButton("wrapBtn", this::wrapText, "换行")
//                CommUtils.createButton("coordinateBtn", MainFmTest::coordinateSet, "坐标设置"),
//                CommUtils.createButton("settingBtn", MainFmTest::paramSet, "参数设置")
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
        ocrPane.setTop(topBar);
        ocrPane.setCenter(textArea);
        ocrPane.setBottom(footerBar);
        //主题样式
        ocrPane.getStylesheets().addAll(
                getClass().getResource("/css/main.css").toExternalForm()
        );

        //ocrPan的内容放在tab中
        ocrTab.setContent(ocrPane);

        paramSettingTab.setContent(paramterSettingPane());



        tabPane.getTabs().addAll(ocrTab,paramSettingTab,historyDataTab);



        CommUtils.initStage(primaryStage);
        mainScene = new Scene(tabPane, 670, 470);
        stage.setScene(mainScene);
        stage.show();
    }

    /**
     *  参数设置布局
     * @return
     */
    private GridPane paramterSettingPane(){

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        //位置参数坐标
        paramSettingLocation(grid);

        //测试版测试数量
        paramSettingTestNum(grid);

        //时间设置
        paramSettingTime(grid);



        return grid;
    }

    private GridPane paramSettingLocation(GridPane grid){

        //位置信息， 占用一列两行
        Label locationName = new Label("位置信息");
        locationName.setPrefWidth(paramSettingColumnWith);
        GridPane.setConstraints(locationName, 0, 0,1,2);
        grid.getChildren().add(locationName);

        //################标签名##################
        //字段框 --
        Label leftTopXName = new Label("左上角X轴");
        GridPane.setConstraints(leftTopXName, 1, 0);
        grid.getChildren().add(leftTopXName);

        Label leftTopYName = new Label("左上角Y轴");
        GridPane.setConstraints(leftTopYName, 2, 0);
        grid.getChildren().add(leftTopYName);

        Label widthName = new Label("宽度");
        GridPane.setConstraints(widthName, 3, 0);
        grid.getChildren().add(widthName);

        Label heightName = new Label("高度");
        GridPane.setConstraints(heightName, 4, 0);
        grid.getChildren().add(heightName);

        ///##################################################
        ///##################具体参数位置##################
        ///##################################################
        //字段框 -- 左上角x
        TextField leftTopX = new TextField();
        leftTopX.setPromptText("leftTopX");
        GridPane.setConstraints(leftTopX, 1, 1);
        grid.getChildren().add(leftTopX);
        //字段框 -- 左上角y
        TextField leftTopY = new TextField();
        leftTopY.setPromptText("leftTopY");
        GridPane.setConstraints(leftTopY, 2, 1);
        grid.getChildren().add(leftTopY);
        //字段框 -- 截图的宽度
        TextField width = new TextField();
        width.setPromptText("width");
        GridPane.setConstraints(width, 3, 1);
        grid.getChildren().add(width);
        //字段框 -- 截图的高度
        TextField height = new TextField();
        height.setPromptText("height");
        GridPane.setConstraints(height, 4, 1);
        grid.getChildren().add(height);

        //############################新建截图获取位置信息的按钮
        Button ocrButton = new Button("新建位置");
        GridPane.setConstraints(ocrButton, 5, 1);
        grid.getChildren().add(ocrButton);

        return grid;
    }

    private GridPane paramSettingTestNum(GridPane grid){

        //测试版当前测试的数量
        Label testNumName = new Label("测试数量");
        testNumName.setPrefWidth(paramSettingColumnWith);
        GridPane.setConstraints(testNumName, 0, 2,1,2);
        grid.getChildren().add(testNumName);

        TextField testNum = new TextField();
        testNum.setPromptText("testNum");
        GridPane.setConstraints(testNum, 1, 2);
        grid.getChildren().add(testNum);

        return grid;
    }

    private GridPane paramSettingTime(GridPane grid){
        //时间设置 http://www.javafxchina.net/blog/2015/04/doc03_datepicker/
        Label timeName = new Label("时间设置");
        timeName.setPrefWidth(paramSettingColumnWith);
        GridPane.setConstraints(timeName, 0, 3,1,2);
        grid.getChildren().add(timeName);

        checkInDatePicker = new DatePicker();
        checkOutDatePicker = new DatePicker();
        checkInDatePicker.setValue(LocalDate.now());
        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isBefore(
                                        checkInDatePicker.getValue().plus(minTimeGap, (TemporalUnit) TimeUnit.MINUTES))
                                ) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                                long p = ChronoUnit.MINUTES.between(
                                        checkInDatePicker.getValue(), item
                                );
                                setTooltip(new Tooltip(
                                        "You're about to stay for " + p + " minutes")
                                );
                            }
                        };
                    }
                };
        checkOutDatePicker.setDayCellFactory(dayCellFactory);
        checkOutDatePicker.setValue(checkInDatePicker.getValue().plusDays(1));
        grid.add(checkInDatePicker, 1, 3);
        grid.add(checkOutDatePicker, 2, 3);
        return grid;

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
        MainFmTest.stage.close();
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
