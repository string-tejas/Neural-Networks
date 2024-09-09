package com.tejas.model;

import com.tejas.neuralnetwork.Layer;
import com.tejas.neuralnetwork.Matrix;

import javax.swing.*;
import java.io.File;

public class DigitModel implements Model {
    Layer model;

    public DigitModel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select .wab file for digits model");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            model = Layer.loadFrom(selectedFile.getAbsolutePath());
        } else {
            System.out.println("File selection was canceled.");
        }
    }

    public Matrix predict(float[][] vals) throws Matrix.DimensionMismatchException {
        Matrix m = new Matrix(vals);
        m = m.reshape(m.row * m.col, 1);
        return model.predict(m);
    }
}
