package com.example.demo1;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;

import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;

//----------------------Menu Functions------------------------------------------------------
// this class contains all the applications that are in the Menu file drop down

public class paintMenuFunctions {

    //---------------------------------------------------------------------------- Save Image
    /**
     * saves the image on the canvas to computer
     * @param canvas
     * @param saveLoc
     */
    public static void savesave(javafx.scene.canvas.Canvas canvas, String saveLoc) {
        File file = new File(saveLoc);
        if(file!= null)
            try{
                WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, wi);
                RenderedImage ri = SwingFXUtils.fromFXImage(wi, null);
                ImageIO.write(ri,"png", file);
            }
            catch(IOException e){
                System.out.println("No");
            }
    }

    //---------------------------------------------------------------------------- Save As Image
    /**
     * save image on canvas as certain file type
     * @param canvas
     */
    public static void saveasas(javafx.scene.canvas.Canvas canvas) {
        Stage stage = new Stage();
        FileChooser saveAs = new FileChooser();
        saveAs.setTitle("Save As");

        File file = saveAs.showSaveDialog(stage);
        if (file != null)
            try {
                WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, wi);
                RenderedImage ri = SwingFXUtils.fromFXImage(wi, null);
                ImageIO.write(ri, "png", file);
            } catch (IOException e) {
                System.out.println("No");
            }
    }

    //---------------------------------------------------------------------------- Open Image
    /**
     * Opens image from computer onto canvas
     * @param event
     * @param canvas
     * @param toolPicker
     * @param saveLoc
     * @param pixel
     * @param tabP
     */
    public static void openopen(Event event, Canvas canvas, GraphicsContext toolPicker, String saveLoc, PixelReader pixel, TabPane tabP) {
        toolPicker = canvas.getGraphicsContext2D();
        toolPicker.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null)
            try {
                InputStream inputStream = new FileInputStream(file);
                Image image = new Image(inputStream);
                pixel = image.getPixelReader();

                double x = image.getHeight();
                double y = image.getWidth();
                canvas.setHeight(x);
                canvas.setWidth(y);

                toolPicker.drawImage(image, 0, 0);

            } catch (IOException e) {
                System.out.println("no");
            }
        canvas.widthProperty().bind(tabP.widthProperty());
        canvas.heightProperty().bind(tabP.heightProperty());
    }

    //---------------------------------------------------------------------------- Close Program
    /**
     * close the program with a warning message
     * @param event
     * @param canvas
     * @param saveLoc
     */
    public static void closeclose(Event event, Canvas canvas, String saveLoc) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Warning");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to close the application?");

        ButtonType okButton = new ButtonType("Yes, bye bye", OK_DONE);
        ButtonType cancelButton = new ButtonType("No, pls I wanna color", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType sButton = new ButtonType("Save", OK_DONE);
        ButtonType SAButton = new ButtonType("Save As", OK_DONE);

        alert.getButtonTypes().setAll(okButton, cancelButton, sButton, SAButton);
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                Platform.exit();
                System.exit(0);
            } else if (response == sButton) {
                paintMenuFunctions.savesave(canvas, saveLoc);
                Platform.exit();
                System.exit(0);
            } else if (response == SAButton) {
                paintMenuFunctions.saveasas(canvas);
                Platform.exit();
                System.exit(0);
            }

        });
    }

    //---------------------------------------------------------------------------- Clear Canvas

    /**
     * Clears canvas but gives a warning for confirmation
     * @param canvas
     * @param toolPicker
     */
    public static void clearclear(Canvas canvas, GraphicsContext toolPicker) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Warning");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to clear your beautiful work?");

        ButtonType clrButton = new ButtonType("Yes, it's gross", OK_DONE);
        ButtonType cancelButton = new ButtonType("No, pls I worked so hard", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(clrButton, cancelButton);
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.showAndWait().ifPresent(response -> {
            if (response == clrButton) {
                toolPicker.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                alert.close();
            } else if (response == cancelButton) {
                alert.close();
            }
        });
    }

    //---------------------------------------------------------------------------- About Button

    /**
     * Shows the most recent info for the program
     * @param e
     * @param canvas
     * @param toolPicker
     */
    public static void helphelp(Event e, Canvas canvas, GraphicsContext toolPicker) {

        try (BufferedReader reader = new BufferedReader(new FileReader(new File("C:\\Users\\Kendall Miller\\Documents\\Sprint CS\\Sprint5info\\cs250 sprintfive release.txt")))) {

            StringBuilder sb = new StringBuilder();

            int x = 0;
            while(x < 20) {
                sb.append(reader.readLine()).append("\n");
                x++;
            }

            String contents = sb.toString();
            reader.close();

            Alert alert;

            alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Help");
            alert.setHeaderText("Information about pain(t)");
            alert.setContentText(contents);

            ButtonType cancelButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.initModality(Modality.APPLICATION_MODAL);

            Alert finalAlert = alert;
            alert.showAndWait().ifPresent(response -> {
                if (response == cancelButton) {
                    finalAlert.close();
                }
            });

        } catch (IOException ev) {
            throw new RuntimeException(ev);
        }
    }

    //---------------------------------------------------------------------------- New Tab

    /**
     * Creates a new window/tab
     */
    public static void newTab(){
        Stage stage = new Stage();

        try{
            new paintApp().start(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
