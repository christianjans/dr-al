package com.cjkj.dral.nn;

import java.util.Arrays;

public class CJMatrixMath {

    public static CJMatrix matrixMultiply(CJMatrix m1, CJMatrix m2) {
        CJMatrix product = new CJMatrix(m1.getRows(), m2.getColumns());

        if (m1.getColumns() == m2.getRows()) {
            for (int i = 0; i < m1.getRows(); i++) {
                for (int j = 0; j < m2.getColumns(); j++) {
                    double elementSum = 0;

                    for (int k = 0; k < m1.getColumns(); k++) {
                        elementSum += m1.get(i, k) * m2.get(k, j);
                    }

                    product.set(i, j, elementSum);
                }
            }
        } else {
            System.err.println("Cannot matrix multiply a " + m1.getRows() + " by " + m1.getColumns() + " matrix"
                    + " with a " + m2.getRows() + " by " + m2.getColumns() + " matrix.");
        }

        return product;
    }

    public static CJMatrix elementMultiply(CJMatrix m1, CJMatrix m2) {
        CJMatrix product = new CJMatrix(m1.getRows(), m1.getColumns());

        if (Arrays.equals(m1.getSize(), m2.getSize())) {
            for (int i = 0; i < m1.getRows(); i++) {
                for (int j = 0; j < m1.getColumns(); j++) {
                    double currentProduct = m1.get(i, j) * m2.get(i, j);
                    product.set(i, j, currentProduct);
                }
            }
        } else {
            System.err.println("Cannot element multiply a " + m1.getRows() + " by " + m1.getColumns() + " matrix"
                    + " with a " + m2.getRows() + " by " + m2.getColumns() + " matrix.");
        }

        return product;
    }

    public static CJMatrix scalarMultiply(double scalar, CJMatrix m) {
        CJMatrix multipliedMatrix = new CJMatrix(m.getRows(), m.getColumns());

        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getColumns(); j++) {
                double currentProduct = scalar * m.get(i, j);
                multipliedMatrix.set(i, j, currentProduct);
            }
        }

        return multipliedMatrix;
    }

    public static CJMatrix columnDivide(CJMatrix m, CJMatrix v) {
        CJMatrix dividedMatrix = new CJMatrix(m.getRows(), m.getColumns());

        for (int column = 0; column < m.getColumns(); column++) {
            double denominator = v.get(0, column);

            for (int row = 0; row < m.getRows(); row++) {
                dividedMatrix.set(row, column, m.get(row, column) / denominator);
            }
        }

        return dividedMatrix;
    }

    public static CJMatrix elementAdd(CJMatrix m1, CJMatrix m2) {
        CJMatrix sum = new CJMatrix(m1.getRows(), m1.getColumns());

        if (Arrays.equals(m1.getSize(), m2.getSize())) {
            for (int i = 0; i < m1.getRows(); i++) {
                for (int j = 0; j < m1.getColumns(); j++) {
                    double currentSum = m1.get(i, j) + m2.get(i, j);
                    sum.set(i, j, currentSum);
                }
            }
        } else {
            System.err.println("Cannot element add a " + m1.getRows() + " by " + m1.getColumns() + " matrix"
                    + " with a " + m2.getRows() + " by " + m2.getColumns() + " matrix.");
        }

        return sum;
    }

    public static CJMatrix elementSubtract(CJMatrix m1, CJMatrix m2) {
        CJMatrix difference = new CJMatrix(m1.getRows(), m1.getColumns());

        if (Arrays.equals(m1.getSize(), m2.getSize())) {
            for (int i = 0; i < m1.getRows(); i++) {
                for (int j = 0; j < m1.getColumns(); j++) {
                    double currentSum = m1.get(i, j) - m2.get(i, j);
                    difference.set(i, j, currentSum);
                }
            }
        } else {
            System.err.println("Cannot element subtract a " + m1.getRows() + " by " + m1.getColumns() + " matrix"
                    + " with a " + m2.getRows() + " by " + m2.getColumns() + " matrix.");
        }

        return difference;
    }

    // TODO: Finish this if needed
    public double dot(CJMatrix v1, CJMatrix v2) {
        double product = 0;

        for (int i = 0; i < v1.getRows(); i++) {

        }

        return product;
    }

}
