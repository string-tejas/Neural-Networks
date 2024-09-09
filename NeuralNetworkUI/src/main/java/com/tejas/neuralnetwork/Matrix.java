package com.tejas.neuralnetwork;

import java.util.function.UnaryOperator;

public class Matrix {

    public float[][] values;
    final public int row;
    final public int col;

    public Matrix(int row, int col) {
        this.row = row;
        this.col = col;
        values = new float[row][col];
    }

    public Matrix(float[][] values) {
        this.row = values.length;
        this.col = values[0].length;
        this.values = new float[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                this.values[i][j] = values[i][j];
            }
        }
    }

    public Matrix(Matrix other) {
        this.row = other.row;
        this.col = other.col;
        values = new float[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                values[i][j] = other.values[i][j];
            }
        }

    }

    public Matrix reshape(int newRow, int newCol) throws IllegalArgumentException {
        if (newRow * newCol != this.row * this.col) {
            throw new IllegalArgumentException("Total number of elements must be the same for reshape.");
        }

        Matrix reshapedMatrix = new Matrix(newRow, newCol);

        float[] flattened = new float[this.row * this.col];
        int index = 0;
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                flattened[index++] = this.values[i][j];
            }
        }

        index = 0;
        for (int i = 0; i < newRow; i++) {
            for (int j = 0; j < newCol; j++) {
                reshapedMatrix.values[i][j] = flattened[index++];
            }
        }

        return reshapedMatrix;
    }

    public Matrix addColWise(Matrix other) throws DimensionMismatchException {
        if (this.row != other.row || other.col != 1) {
            throw new DimensionMismatchException(this, other);
        }
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                values[i][j] += other.values[i][0];
            }
        }
        return this;
    }

    public Matrix add(Matrix other) throws DimensionMismatchException {
        if (!Matrix.haveSameDimensions(this, other)) {
            throw new DimensionMismatchException(this, other);
        }
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                values[i][j] += other.values[i][j];
            }
        }
        return this;
    }

    public Matrix add(double constant) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                values[i][j] += (float) constant;
            }
        }
       return this;
    }


    public Matrix div(double constant) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                values[i][j] /= (float) constant;
            }
        }
        return this;
    }

    public Matrix mul(double constant) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                values[i][j] *= (float) constant;
            }
        }
        return this;
    }


    public Matrix transpose() {
        Matrix t = new Matrix(col, row);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                t.values[j][i] = this.values[i][j];
            }
        }
        return t;
    }

//    apply and exec are same but apply mutates while exec doesn't
    public Matrix apply(UnaryOperator<Float> function) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                values[i][j] = function.apply(values[i][j]);
            }
        }
        return this;
    }

    public Matrix exec(UnaryOperator<Float> function) {
        Matrix temp = new Matrix(this.row, this.col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                temp.values[i][j] = function.apply(values[i][j]);
            }
        }
        return temp;
    }

    public Matrix mul(Matrix other) throws DimensionMismatchException {
        if (this.col != other.row) {
            throw new DimensionMismatchException(this, other);
        }

        Matrix result = new Matrix(this.row, other.col);

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < other.col; j++) {
                float sum = 0;
                for (int k = 0; k < this.col; k++) {
                    sum += this.values[i][k] * other.values[k][j];
                }
                result.values[i][j] = sum;
            }
        }

        return result;
    }


//    static

    public static Matrix ones(int row, int col) {
        Matrix matrix = new Matrix(row, col);
        matrix.apply(_ -> 1f);
        return matrix;
    }

    public static Matrix zeros(int row, int col) {
        Matrix matrix = new Matrix(row, col);
        matrix.apply(_ -> 0f);
        return matrix;
    }

    public static Matrix random(int row, int col) {
        return random(row, col, 0, 1);
    }

    public static Matrix random(int row, int col, float lowerLimit, float upperLimit) {
        Matrix matrix = new Matrix(row, col);
        matrix.apply(_ -> lowerLimit + (float) Math.random() * (upperLimit - lowerLimit));
        return matrix;
    }

    public static float sum(Matrix matrix) {
        float sum = 0;
        for (int i = 0; i < matrix.row; i++) {
            for (int j = 0; j < matrix.col; j++) {
                sum += matrix.values[i][j];
            }
        }
        return sum;
    }

    public static Matrix exp(Matrix matrix) {
        return matrix.exec(x -> (float) Math.exp(x));
    }

    public static boolean haveSameDimensions(Matrix a, Matrix b) {
        return a.row == b.row && a.col == b.col;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Matrix: " + row + "x" + col + "\n");
        for (int i = 0; i < row; i++) {
            str.append("[ ");
            for (int j = 0; j < col; j++) {
                if (values[i][j] >= 0f) str.append(" ");
                str.append(String.format("%.2f, ", values[i][j]));

                if (j < col - 1) {
                    str.append(" ");
                }
            }
            str.append("]");

            if (i < row - 1) {
                str.append("\n");
            }
        }

        return str.toString();
    }



    public static class DimensionMismatchException extends Exception {
        DimensionMismatchException() {
            super("Incompatible dimensions");
        }

        DimensionMismatchException(Matrix a, Matrix b) {
            super("Incompatible dimensions: (" + a.row + ", " + a.col + ") and (" + b.row + ", " + b.col + ")");
        }
    }
}
