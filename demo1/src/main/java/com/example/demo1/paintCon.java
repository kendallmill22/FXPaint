package com.example.demo1;

import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.shape.*;
import javafx.scene.paint.Color;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class paintCon implements Initializable {
    GraphicsContext toolPicker;
    @FXML
    private Canvas canvas;
    @FXML
    private TabPane tabP;

    //---------------------------------------------------------------------------- Tools
    @FXML
    private ToggleGroup tools;
    @FXML
    private RadioMenuItem pointerTool;
    @FXML
    private RadioMenuItem brushTool;
    @FXML
    private RadioMenuItem lineTool;
    @FXML
    private RadioMenuItem dlineTool;
    @FXML
    private RadioMenuItem circleTool;
    @FXML
    private RadioMenuItem elpsTool;
    @FXML
    private RadioMenuItem rectTool;
    @FXML
    private RadioMenuItem roundRectTool;
    @FXML
    private RadioMenuItem poliTool;
    @FXML
    private RadioMenuItem eraserTool;

    //---------------------------------------------------------------------------- Menu Items
    @FXML
    private RadioMenuItem copy;
    @FXML
    private RadioMenuItem paste;
    @FXML
    private RadioMenuItem copyCut;
    @FXML
    private Button undo;
    @FXML
    private Button redo;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private RadioButton eyeDropper;
    @FXML
    private TextField fontSize;
    //---------------------------------------------------------------------------- Need
    private Timer autosaveTimer = new Timer();
    private TimerTask autoSave;
    @FXML
    private CheckBox auto;
    private Timer loggerTimer;
    private TimerTask logger;
    public final static String logFile = "logs.txt";
    public RadioMenuItem tool;
    public String saveLoc;
    private PixelReader pixel;
    private double[] xPoint, yPoint;    //variables for poli
    private int points;
    private boolean complete;
    private Image selChunck;
    public ImageView imgV;

    //---------------------------------------------------------------------------- Open
    @FXML
    public void openFile(ActionEvent event) {
        paintMenuFunctions.openopen(event, canvas, toolPicker, saveLoc, pixel, tabP);}

    //---------------------------------------------------------------------------- Close
    @FXML
    public void closeP(ActionEvent e) {
        paintMenuFunctions.closeclose(e, canvas, saveLoc);}

    //---------------------------------------------------------------------------- Save
    @FXML
    public void save(ActionEvent event) {
        paintMenuFunctions.savesave(canvas, saveLoc);}

    //---------------------------------------------------------------------------- Save As
    @FXML
    public void saveAs(ActionEvent event) {
        paintMenuFunctions.saveasas(canvas);}

    //---------------------------------------------------------------------------- Auto save
    @FXML
    public void autoSave(ActionEvent event) {
        if(auto.isSelected()) {
            autosaveTimer = new Timer();
            autoSave = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (saveLoc == null) {
                                paintMenuFunctions.saveasas(canvas);
                            } else {
                                paintMenuFunctions.savesave(canvas, saveLoc);
                            }
                            autosaveTimer.schedule(autoSave, 0, 30000);
                        }
                    });
                }
            };
            autosaveTimer.schedule(autoSave, 30000, 30000);
        }
    }

    //---------------------------------------------------------------------------- Helpfile
    public void helpFile(ActionEvent event) {
        paintMenuFunctions.helphelp(event, canvas, toolPicker);}

    //---------------------------------------------------------------------------- Tab :(
    public void newTab(ActionEvent event) {
        paintMenuFunctions.newTab();}

    //---------------------------------------------------------------------------- Clear Canvas
    public void clearCan(ActionEvent event) {
        paintMenuFunctions.clearclear(canvas, toolPicker);}

    //---------------------------------------------------------------------------- Clear Canvas
    public void roCan(ActionEvent event) {canvas.setRotate(canvas.getRotate() + 90);}

    //---------------------------------------------------------------------------- flip Canvas vertically
    public void flipCanY(ActionEvent event) {flipflorpY(canvas);}

    //---------------------------------------------------------------------------- flip Canvas horiz
    public void flipCanX(ActionEvent event) {flipflorpX(canvas);}

    //---------------------------------------------------------------------------- Tools

    /**
     * holds code for tools and undo redo (functions that needed initialization)
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        toolPicker = canvas.getGraphicsContext2D();

        // sets starting values
        colorPicker.setValue(Color.GREEN);
        fontSize.setText("10");
        tools.selectToggle(pointerTool);
        tool = pointerTool;
        loggylog(tool);

        // Stacks
        Stack<Shape> undoHistory = new Stack();
        Stack<Shape> redoHistory = new Stack();

        // creates new shapes for tools
        Line line = new Line();
        Rectangle rect = new Rectangle();
        Rectangle selRect = new Rectangle();
        Rectangle cutRect = new Rectangle();
        Rectangle roundRect = new Rectangle();
        Circle circ = new Circle();
        Ellipse elps = new Ellipse();

        //For poli
        xPoint = new double[500];  // create arrays to hold the polygon's points
        yPoint = new double[500];
        points = 0;

        //---------------------------------------------------------------------------- Start of Actions
        canvas.setOnMousePressed(e -> {

            // Line Tool
            if (lineTool.isSelected()) {
                paintTools.basicStart(e, toolPicker, colorPicker, fontSize);
                line.setStartX(e.getX());
                line.setStartY(e.getY());

                tool = lineTool;
                loggylog(tool);

            // Dotted Line Tool
            } else if (dlineTool.isSelected()) {
                paintTools.dlineStart(e, toolPicker, colorPicker, fontSize);
                line.setStartX(e.getX());
                line.setStartY(e.getY());

                tool = dlineTool;
                loggylog(tool);

            // Rectangle Tool
            } else if (rectTool.isSelected()) {
                paintTools.basicStart(e, toolPicker, colorPicker, fontSize);
                rect.setLocation((int) e.getX(), (int) e.getY());

                tool = rectTool;
                loggylog(tool);

            // Rounded Rectangle Tool
            } else if (roundRectTool.isSelected()) {
                paintTools.basicStart(e, toolPicker, colorPicker, fontSize);
                roundRect.setLocation((int) e.getX(), (int) e.getY());

                tool = roundRectTool;
                loggylog(tool);

            // Circle Tool
            } else if (circleTool.isSelected()) {
                paintTools.basicStart(e, toolPicker, colorPicker, fontSize);
                circ.setCenterX(e.getX());
                circ.setCenterY(e.getY());

                tool = circleTool;
                loggylog(tool);

            // Ellipse Tool
            } else if (elpsTool.isSelected()) {
                paintTools.basicStart(e, toolPicker, colorPicker, fontSize);
                elps.setCenterX(e.getX());
                elps.setCenterY(e.getY());

                tool = elpsTool;
                loggylog(tool);

            // Polygon Tool
            } else if (poliTool.isSelected()) {
                if (complete) {
                    complete = false;
                    xPoint[0] = e.getX();
                    yPoint[0] = e.getY();
                    points = 1;
                } else if (points > 0 && points > 0 && (Math.abs(xPoint[0] - e.getX()) <= 3) && (Math.abs(yPoint[0] - e.getY()) <= 3)) {
                    complete = true;
                } else if (e.getButton() == MouseButton.SECONDARY || points == 500) {
                    complete = true;
                } else {
                    toolPicker.setStroke(colorPicker.getValue());
                    xPoint[points] = e.getX();
                    yPoint[points] = e.getY();
                    points++;
                }
                draw();

                tool = poliTool;
                loggylog(tool);

            // Copy Tool
            } else if (copy.isSelected()) {
                toolPicker.setStroke(Color.BLUE);
                toolPicker.setFill(Color.TRANSPARENT);
                toolPicker.setLineDashes(0);
                toolPicker.setLineWidth(10);
                selRect.setLocation((int) e.getY(), (int) e.getY());
            // Copy/Cut Tool
            }else if (copyCut.isSelected()) {
                toolPicker.setStroke(Color.BLUE);
                toolPicker.setFill(Color.TRANSPARENT);
                toolPicker.setLineDashes(0);
                toolPicker.setLineWidth(10);
                cutRect.setLocation((int) e.getY(), (int) e.getY());
            }
        });

        canvas.setOnMouseDragged(e -> {
            // Brush Tool
            if (brushTool.isSelected()) {
                double size = Double.parseDouble(fontSize.getText());
                double x = e.getX() - size / 2;
                double y = e.getY() - size / 2;
                toolPicker.setFill(colorPicker.getValue());
                toolPicker.fillRoundRect(x, y, size, size, size, size);

                tool = brushTool;
                loggylog(tool);

            // Eraser Tool
            } else if (eraserTool.isSelected()) {
                double size = Double.parseDouble(fontSize.getText());
                double x = e.getX() - size / 2;
                double y = e.getY() - size / 2;
                toolPicker.clearRect(x, y, size, size);

                tool = eraserTool;
                loggylog(tool);
            }
        });

        // Second have of all the tool code
        canvas.setOnMouseReleased(e -> {
            if (lineTool.isSelected()) {
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                toolPicker.setLineDashes(0);
                toolPicker.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

                undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));

            } else if (dlineTool.isSelected()) {
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                toolPicker.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));

            } else if (rectTool.isSelected()) {
                rect.setSize((int) Math.abs((e.getX() - rect.getX())), (int) Math.abs((e.getY() - rect.getY())));

                if ((rect.getX() > e.getX()) || (rect.getY() > e.getY())) {
                    rect.setLocation((int) rect.getX(), (int) rect.getY());
                }
                toolPicker.setLineDashes(0);
                toolPicker.setFill(Color.TRANSPARENT);
                toolPicker.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                toolPicker.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                undoHistory.push(new javafx.scene.shape.Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));

            } else if (roundRectTool.isSelected()) {
                roundRect.setSize((int) Math.abs((e.getX() - roundRect.getX())), (int) Math.abs((e.getY() - roundRect.getY())));

                if ((roundRect.getX() > e.getX()) || (roundRect.getY() > e.getY())) {
                    roundRect.setLocation((int) roundRect.getX(), (int) roundRect.getY());

                }
                toolPicker.setLineDashes(0);
                toolPicker.setFill(Color.TRANSPARENT);
                toolPicker.fillRect(roundRect.getX(), roundRect.getY(), roundRect.getWidth(), roundRect.getHeight());
                toolPicker.strokeRoundRect(roundRect.getX(), roundRect.getY(), roundRect.getWidth(), roundRect.getHeight(), 20, 30);

                undoHistory.push(new javafx.scene.shape.Rectangle(roundRect.getX(), roundRect.getY(), roundRect.getWidth(), roundRect.getHeight()));

            } else if (circleTool.isSelected()) {
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                if (circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if (circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }

                toolPicker.setLineDashes(0);
                toolPicker.setFill(Color.TRANSPARENT);
                toolPicker.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                toolPicker.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());

                undoHistory.push(new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius()));
            } else if (elpsTool.isSelected()) {
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));

                if (elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if (elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }

                toolPicker.setLineDashes(0);
                toolPicker.setFill(Color.TRANSPARENT);
                toolPicker.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                toolPicker.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());

                undoHistory.push(new Ellipse(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY()));

            } else if (copy.isSelected()) {
                selRect.setSize((int) Math.abs(e.getX() - selRect.getX()), (int) Math.abs(e.getY() - selRect.getY()));

                if ((selRect.getX() > e.getX()) || (selRect.getY() > e.getY())) {
                    selRect.setLocation((int) selRect.getX(), (int) selRect.getY());
                }

                selChunck = getChunk(selRect.getX(), selRect.getY(), e.getX(), e.getY());
                imgV.setImage(selChunck);

                undoHistory.push(new javafx.scene.shape.Rectangle(selRect.getX(), selRect.getY(), selRect.getWidth(), selRect.getHeight()));

            }else if (copyCut.isSelected()) {
                cutRect.setSize((int) Math.abs(e.getX() - cutRect.getX()), (int) Math.abs(e.getY() - cutRect.getY()));

                if ((cutRect.getX() > e.getX()) || (cutRect.getY() > e.getY())) {
                    cutRect.setLocation((int) cutRect.getX(), (int) cutRect.getY());
                }

                selChunck = getChunk(cutRect.getX(), cutRect.getY(), e.getX(), e.getY());
                toolPicker.clearRect(cutRect.getX(), cutRect.getY(), e.getX(), e.getY());

                undoHistory.push(new javafx.scene.shape.Rectangle(cutRect.getX(), cutRect.getY(), cutRect.getWidth(), cutRect.getHeight()));

            } else if (paste.isSelected()) {
                if (selChunck != null) {
                    pasteHere(selChunck, e.getX(), e.getY());
                }
            }

            redoHistory.clear();
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(toolPicker.getFill());
            lastUndo.setStroke(toolPicker.getStroke());
            lastUndo.setStrokeWidth(toolPicker.getLineWidth());

            // Sets canvas to size of tab pane
            canvas.widthProperty().bind(tabP.widthProperty());
            canvas.heightProperty().bind(tabP.heightProperty());
        });

        // eyedropper
        canvas.setOnMouseClicked(e ->{
            if(eyeDropper.isSelected()){
                Color color = pixel.getColor((int) e.getX(), (int) e.getY());
                colorPicker.setValue(color);
            }
        });

        //---------------------------------------------------------------------------- Undo
        undo.setOnAction(e -> {
            if (!undoHistory.empty()) {
                toolPicker.clearRect(0, 0, 1080, 790);
                Shape removedShape = undoHistory.lastElement();
                if (removedShape.getClass() == Line.class) {
                    Line tempLine = (Line) removedShape;
                    tempLine.setFill(toolPicker.getFill());
                    tempLine.setStroke(toolPicker.getStroke());
                    tempLine.setStrokeWidth(toolPicker.getLineWidth());
                    redoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));

                } else if (removedShape.getClass() == javafx.scene.shape.Rectangle.class) {
                    javafx.scene.shape.Rectangle tempRect = (javafx.scene.shape.Rectangle) removedShape;
                    tempRect.setFill(toolPicker.getFill());
                    tempRect.setStroke(toolPicker.getStroke());
                    tempRect.setStrokeWidth(toolPicker.getLineWidth());
                    redoHistory.push(new javafx.scene.shape.Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                } else if (removedShape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) removedShape;
                    tempCirc.setStrokeWidth(toolPicker.getLineWidth());
                    tempCirc.setFill(toolPicker.getFill());
                    tempCirc.setStroke(toolPicker.getStroke());
                    redoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                } else if (removedShape.getClass() == Ellipse.class) {
                    Ellipse tempElps = (Ellipse) removedShape;
                    tempElps.setFill(toolPicker.getFill());
                    tempElps.setStroke(toolPicker.getStroke());
                    tempElps.setStrokeWidth(toolPicker.getLineWidth());
                    redoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                }
                Shape lastRedo = redoHistory.lastElement();
                lastRedo.setFill(removedShape.getFill());
                lastRedo.setStroke(removedShape.getStroke());
                lastRedo.setStrokeWidth(removedShape.getStrokeWidth());
                undoHistory.pop();

                for (int i = 0; i < undoHistory.size(); i++) {
                    Shape shape = undoHistory.elementAt(i);
                    if (shape.getClass() == Line.class) {
                        Line temp = (Line) shape;
                        toolPicker.setLineWidth(temp.getStrokeWidth());
                        toolPicker.setStroke(temp.getStroke());
                        toolPicker.setFill(temp.getFill());
                        toolPicker.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                    } else if (shape.getClass() == javafx.scene.shape.Rectangle.class) {
                        javafx.scene.shape.Rectangle temp = (javafx.scene.shape.Rectangle) shape;
                        toolPicker.setLineWidth(temp.getStrokeWidth());
                        toolPicker.setStroke(temp.getStroke());
                        toolPicker.setFill(temp.getFill());
                        toolPicker.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                        toolPicker.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    } else if (shape.getClass() == Circle.class) {
                        Circle temp = (Circle) shape;
                        toolPicker.setLineWidth(temp.getStrokeWidth());
                        toolPicker.setStroke(temp.getStroke());
                        toolPicker.setFill(temp.getFill());
                        toolPicker.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                        toolPicker.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                    } else if (shape.getClass() == Ellipse.class) {
                        Ellipse temp = (Ellipse) shape;
                        toolPicker.setLineWidth(temp.getStrokeWidth());
                        toolPicker.setStroke(temp.getStroke());
                        toolPicker.setFill(temp.getFill());
                        toolPicker.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                        toolPicker.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                    }
                }
            } else {
                System.out.println("there is no action to undo");
            }
        });

        //---------------------------------------------------------------------------- Redo
        redo.setOnAction(e -> {
            if (!redoHistory.empty()) {
                Shape shape = redoHistory.lastElement();
                toolPicker.setLineWidth(shape.getStrokeWidth());
                toolPicker.setStroke(shape.getStroke());
                toolPicker.setFill(shape.getFill());

                redoHistory.pop();
                if (shape.getClass() == Line.class) {
                    Line tempLine = (Line) shape;
                    toolPicker.strokeLine(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY());
                    undoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
                } else if (shape.getClass() == javafx.scene.shape.Rectangle.class) {
                    javafx.scene.shape.Rectangle tempRect = (javafx.scene.shape.Rectangle) shape;
                    toolPicker.fillRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
                    toolPicker.strokeRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());

                    undoHistory.push(new javafx.scene.shape.Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                } else if (shape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) shape;
                    toolPicker.fillOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
                    toolPicker.strokeOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());

                    undoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                } else if (shape.getClass() == Ellipse.class) {
                    Ellipse tempElps = (Ellipse) shape;
                    toolPicker.fillOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());
                    toolPicker.strokeOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());

                    undoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                }
                Shape lastUndo = undoHistory.lastElement();
                lastUndo.setFill(toolPicker.getFill());
                lastUndo.setStroke(toolPicker.getStroke());
                lastUndo.setStrokeWidth(toolPicker.getLineWidth());
            } else {
                System.out.println("there is no action to redo");
            }
        });
    }

    /**
     * For drawing a poligon
     */
    private void draw() {
        if (complete) { // draw a polygon
            toolPicker.setFill(colorPicker.getValue());
            toolPicker.fillPolygon(xPoint, yPoint, points);
            toolPicker.strokePolygon(xPoint, yPoint, points);
        } else { // show the lines the user has drawn so far
            toolPicker.setFill(colorPicker.getValue());
            toolPicker.fillRect(xPoint[0] - 2, yPoint[0] - 2, 4, 4);  // small square marks first point
            for (int i = 0; i < points - 1; i++) {
                toolPicker.strokeLine(xPoint[i], yPoint[i], xPoint[i + 1], yPoint[i + 1]);
            }
        }
    }

    /**
     * Used for getting a "chunk" of an image for copy/paste/select
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public Image getChunk(double x1, double y1, double x2, double y2) {
        SnapshotParameters snap = new SnapshotParameters();
        WritableImage write = new WritableImage((int) Math.abs(x1 - x2), (int) Math.abs(y1 - y2));
        snap.setViewport(new Rectangle2D(x1, y1, x2, y2));
        canvas.snapshot(snap, write);
        return write;
    }

    /**
     * For pasting an image
     * @param img
     * @param x
     * @param y
     */
    public void pasteHere(Image img, double x, double y) {toolPicker.drawImage(img, x, y);}

    /**
     * Flipping canvas over Y axis
     * @param canvas
     */
    public void flipflorpY(Canvas canvas) {
        if(canvas.getScaleX() == 1){
            canvas.setTranslateY(0);
            canvas.setScaleX(-1);
            canvas.setScaleY(1);
        }else if(canvas.getScaleX() == -1){
            canvas.setTranslateY(1);
            canvas.setScaleX(1);
            canvas.setScaleY(1);
        }
    }

    /**
     * Flipping canvas over X axis
     * @param canvas
     */
    public void flipflorpX(Canvas canvas) {
        if(canvas.getScaleY() == 1){
            canvas.setTranslateX(0);
            canvas.setScaleX(1);
            canvas.setScaleY(-1);
        }else if(canvas.getScaleY() == -1){
            canvas.setTranslateX(1);
            canvas.setScaleX(1);
            canvas.setScaleY(1);
        }
    }

    /**
     * Logging funcitons
     * @param tool
     */
    public void loggylog(RadioMenuItem tool) {
        this.loggerTimer = new Timer();
        this.logger = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        File loggerFile = new File(logFile);
                        try{
                            loggerFile.createNewFile();
                        } catch(Exception ex){
                            System.out.println(ex);
                        }
                        try{
                            FileWriter fw = new FileWriter(logFile, true);
                            BufferedWriter bw = new BufferedWriter(fw);
                            bw.write(tool.getId() + " | " + LocalDate.now() + " | " + LocalTime.now());
                            bw.newLine();
                            bw.close();
                        }
                        catch(Exception ex){System.out.println(ex);}
                    }
                });
            }
        };
        this.loggerTimer.scheduleAtFixedRate(this.logger, 1000, 50000);
    }
}

// this is so long, so sorry