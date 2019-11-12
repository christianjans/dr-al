package com.cjkj.dral.nn;

import java.util.ArrayList;

public class CJNeuralNetwork {

    private ArrayList<CJMatrix> weights;
    private ArrayList<CJMatrix> biases;
    private int numLayers;

    public CJNeuralNetwork(ArrayList<double[][]> weights, ArrayList<double[]> biases) {
        int numWeightMatrices = weights.size();

        this.weights = new ArrayList<>();
        this.biases = new ArrayList<>();

        for (int i = 0; i < numWeightMatrices; i++) {
            this.weights.add(new CJMatrix(weights.get(i)));
            this.biases.add(new CJMatrix(biases.get(i)));
        }

        numLayers = numWeightMatrices + 1;
    }

    public double[] feedForward(double[] inputs, double[] categoriesMax) {
        CJMatrix matrixInputs = CJMatrixMath.columnDivide(new CJMatrix(inputs), new CJMatrix(categoriesMax)).transpose();
        int numIterations = numLayers - 1;

        for (int i = 0; i < numIterations; i++) {
            CJMatrix wx = CJMatrixMath.matrixMultiply(weights.get(i), matrixInputs);
            CJMatrix z = CJMatrixMath.elementAdd(wx, biases.get(i).transpose());

            if (i != numIterations - 1) {
                matrixInputs = z.sigmoid();
            } else {
                matrixInputs = z.softmax1D();
            }
        }

        return matrixInputs.to1DDoubleArray();
    }

}
