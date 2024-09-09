package com.tejas.neuralnetwork;

public class Activations {
    public static Matrix reLu(Matrix m) {
        return m.exec(x -> Math.max(0, (float) x));
    }

    public static Matrix reLuDerivative(Matrix m) {
        return m.exec(x -> x > 0 ? 1f : 0f);
    }

    public static Matrix sigmoid(Matrix m) {
        return m.exec(x -> 1 + (float) Math.exp(-x)).apply(x -> 1 / x);
    }
}
