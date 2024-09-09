package com.tejas.neuralnetwork;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Layer {
    final private int numLayers;
    private int[] nodesPerLayer;

    private List<Matrix> weights;
    private List<Matrix> biases;

    private Layer(int numLayers) {
        this.numLayers = numLayers;
        nodesPerLayer = new int[numLayers];
    }

    public Layer(int numLayers, int... nodesPerLayer) {
        this.numLayers = numLayers;
        if (nodesPerLayer.length != numLayers) {
            throw new IllegalArgumentException("Mismatch between number of layers and nodes per layer specified");
        }
        this.nodesPerLayer = new int[numLayers];
        System.arraycopy(this.nodesPerLayer, 0, nodesPerLayer, 0, nodesPerLayer.length);

        weights = new ArrayList<>();
        weights.add(null);

        for (int i = 1; i <= numLayers; i++) {
            weights.add(Matrix.random(nodesPerLayer[i], nodesPerLayer[i - 1], -0.5f, 0.5f));
        }

        biases = new ArrayList<>();
        biases.add(null);

        for (int i = 1; i <= numLayers; i++) {
            biases.add(Matrix.random(nodesPerLayer[i], 1, -0.5f, 0.5f));
        }
    }

    public Matrix W(int i) {
        return weights.get(i);
    }

    public Matrix B(int i) {
        return biases.get(i);
    }

    public List<Matrix> getWeights() {
        return weights;
    }

    public void setWeights(List<Matrix> weights) {
        this.weights = weights;
    }

    public List<Matrix> getBiases() {
        return biases;
    }

    public void setBiases(List<Matrix> biases) {
        this.biases = biases;
    }


    public Matrix predict(Matrix X) throws Matrix.DimensionMismatchException {
        if (X.col != 1 || X.row != nodesPerLayer[0]) {
            throw new IllegalArgumentException("Incorrect input matrix");
        }

        Matrix AL = null, AL_1 = X;

        for (int i = 1; i < numLayers; i++) {
            AL = W(i).mul(AL_1).addColWise(B(i));
            if (i < numLayers - 1) {
                AL = Activations.reLu(AL);
            } else {
                AL = Activations.sigmoid(AL);
            }
            AL_1 = new Matrix(AL);
        }

        return AL;
    }


    public static Layer loadFrom(String path) {
        List<Matrix> weightsList = new ArrayList<>();
        List<Matrix> biasesList = new ArrayList<>();

        Layer wab = null;
        weightsList.add(null);
        biasesList.add(null);

        try (FileInputStream fis = new FileInputStream(path);
             DataInputStream dis = new DataInputStream(fis)
        ) {
            byte[] bytes = dis.readAllBytes();

            // Create a ByteBuffer from the byte array
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            buffer.order(ByteOrder.LITTLE_ENDIAN);


            int numLayers = buffer.getInt();
            wab = new Layer(numLayers);

            for (int i = 0; i < numLayers; i++) {
                wab.nodesPerLayer[i] = buffer.getInt();
            }

            for (int i = 1; i < numLayers; i++) {
                int numRows = wab.nodesPerLayer[i];
                int numCols = wab.nodesPerLayer[i - 1];
                float[][] weights = new float[numRows][numCols];
                for (int row = 0; row < numRows; row++) {
                    for (int col = 0; col < numCols; col++) {
                        weights[row][col] = buffer.getFloat();
                    }
                }
                Matrix weightMatrix = new Matrix(numRows, numCols);
                weightMatrix.values = weights;
                weightsList.add(weightMatrix);

                float[] biases = new float[numRows];
                for (int j = 0; j < numRows; j++) {
                    biases[j] = buffer.getFloat();
                }
                Matrix biasMatrix = new Matrix(numRows, 1);
                for (int j = 0; j < numRows; j++) {
                    biasMatrix.values[j][0] = biases[j];
                }
                biasesList.add(biasMatrix);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        wab.setBiases(biasesList);
        wab.setWeights(weightsList);

        return wab;
    }
}
