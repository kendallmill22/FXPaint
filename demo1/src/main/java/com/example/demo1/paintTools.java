package com.example.demo1;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.*;

//----------------------Tool Parameters------------------------------------------------------
// this class contains basic parameter tools

public class paintTools extends paintCon {

    /**
     * Sets all the basic parameters for the drawing tools
     * @param e
     * @param toolPicker
     * @param colorPicker
     * @param fontSize
     */
    public static void basicStart(MouseEvent e, GraphicsContext toolPicker, ColorPicker colorPicker, TextField fontSize) {
        toolPicker.setStroke(colorPicker.getValue());
        toolPicker.setFill(colorPicker.getValue());
        toolPicker.setLineDashes(0);
        toolPicker.setLineWidth(Double.parseDouble(fontSize.getText()));
    }

    /**
     * Dashed line has different basics from other tools
     * @param e
     * @param toolPicker
     * @param colorPicker
     * @param fontSize
     */
    public static void dlineStart(MouseEvent e, GraphicsContext toolPicker, ColorPicker colorPicker, TextField fontSize) {
        toolPicker.setStroke(colorPicker.getValue());
        toolPicker.setLineWidth(Double.parseDouble(fontSize.getText()));
        toolPicker.setLineDashes(Double.parseDouble(fontSize.getText())*2);
    }

}
