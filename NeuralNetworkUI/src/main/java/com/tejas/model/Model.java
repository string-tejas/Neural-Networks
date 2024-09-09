package com.tejas.model;

import com.tejas.neuralnetwork.Matrix;

public interface Model {
    Matrix predict(float[][] x) throws Matrix.DimensionMismatchException;
}
