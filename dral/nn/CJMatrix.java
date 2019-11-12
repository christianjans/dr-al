package com.cjkj.dral.nn;

public class CJMatrix {

    private int rows;
    private int columns;
    private int[] size;
    private double[][] elements;


    // region CJMATRIX - CONSTRUCTORS | WORKING

    /**
     * Initializes number of rows, columns, size, and
     * elements (all zero by default) of new instance.
     *
     * @param numRows
     * @param numColumns
     */
    public CJMatrix(int numRows, int numColumns) {
        rows = numRows;
        columns = numColumns;
        size = new int[]{rows, columns};
        elements = new double[rows][columns];
    }

    public CJMatrix(double[] array) {
        rows = 1;
        columns = array.length;
        size = new int[]{rows, columns};
        elements = new double[rows][columns];

        for (int i = 0; i < array.length; i++) {
            elements[0][i] = array[i];
        }
    }

    public CJMatrix(double[][] array) {
        rows = array.length;
        columns = array[0].length;
        size = new int[]{rows, columns};
        elements = array;
    }

    // endregion


    // region CJMATRIX - POPULATION METHODS | WORKING

    /**
     * Populates all elements of current instance with
     * random number between 0 and 1.
     */
    public void populateRandom() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                elements[i][j] = Math.random();
            }
        }
    }

    /**
     * Populates all elements with random numbers between
     * 'min' and 'max'.
     *
     * @param max
     * @param min
     */
    public void populateRandom(int min, int max) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                elements[i][j] = Math.random() * (max - min) + min;
            }
        }
    }

    public void populateRandomInt(int min, int max) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                elements[i][j] = (double)(int)(Math.random() * (max - min + 1) + min);
            }
        }
    }

    /**
     * Populates all elements with zero.
     */
    public void populateZeros() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                elements[i][j] = 0;
            }
        }
    }

    /**
     * Populates all elements with ones.
     */
    public void populateOnes() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                elements[i][j] = 1;
            }
        }
    }

    /**
     * Populates elements with one dimensional array by
     * first populating the columns in a row, then moving
     * on to the next row.
     *
     * First ensures that there is enough elements in
     * 'array' to populate all elements of the matrix.
     *
     * @param array
     */
    public void populateArray(double[] array) {
        if (array.length == rows * columns) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    elements[i][j] = array[i * columns + j];
                }
            }
        } else {
            System.err.println("Cannot populate " + rows + " by " + columns + " matrix with " + array.length + "-element array.");
        }
    }

    /**
     * Populates elements with two dimensional array by
     * assigning each element in 'array' to the same element
     * in the two dimensional element array for this matrix.
     *
     * First ensures that the rows and columns are the same
     * for 'array' and this matrix.
     *
     * @param array
     */
    public void populateArray(double[][] array) {
        int arrayRows = array.length;
        int arrayColumns = array[0].length;

        if (arrayRows == rows && arrayColumns == columns) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    elements[i][j] = array[i][j];
                }
            }
        } else {
            System.err.println("Cannot populate " + rows + " by " + columns + " matrix with " + arrayRows + " by " + arrayColumns + " array.");
        }
    }

    // endregion


    // region CJMATRIX - OPERATION METHODS | WORKING

    /**
     * Creates a new instance of CJMatrix with the same number
     * of rows as there is columns and same number of columns as
     * there is rows of this matrix. Each (row, column) element
     * in the new matrix is the (column, row) element of this
     * matrix.
     *
     * @return new instance of CJMatrix that is the transpose
     * of this matrix.
     */
    public CJMatrix transpose() {
        CJMatrix transposedMatrix = new CJMatrix(columns, rows);

        for (int i = 0; i < transposedMatrix.getRows(); i++) {
            for (int j = 0; j < transposedMatrix.getColumns(); j++) {
                transposedMatrix.set(i, j, this.get(j, i));
            }
        }

        return transposedMatrix;
    }

    public double getMax() {
        double max = elements[0][0];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                double currentElement = elements[i][j];

                if (currentElement > max) {
                    max = elements[i][j];
                }
            }
        }

        return max;
    }

    public CJMatrix maxVector() {
        CJMatrix maxedVector = new CJMatrix(rows, columns);

        if (rows == 1) {

        } else if (columns == 1) {

        } else {

        }

        return maxedVector;
    }

    public CJMatrix fractionalRows() {
        CJMatrix newMatrix = new CJMatrix(rows, columns);

        for (int i = 0; i < rows; i++) {
            double rowSum = sumRow(i);

            for (int j = 0; j < columns; j++) {
                newMatrix.set(i, j, elements[i][j] / rowSum);
            }
        }

        return newMatrix;
    }

    // NOT TESTED
    public CJMatrix fractionalColumns() {
        CJMatrix newMatrix = new CJMatrix(rows, columns);

        for (int i = 0; i < columns; i++) {
            double columnSum = sumColumn(i);

            for (int j = 0; j < rows; j++) {
                newMatrix.set(i, j, elements[j][i] / columnSum);
            }
        }

        return newMatrix;
    }

    public double sumRow(int row) {
        double sum = elements[row][0];

        for (int i = 1; i < columns; i++) {
            sum += elements[row][i];
        }

        return sum;
    }

    public double sumColumn(int column) {
        double sum = elements[0][column];

        for (int i = 1; i < rows; i++) {
            sum += elements[i][column];
        }

        return sum;
    }

    public CJMatrix sigmoid() {
        CJMatrix sigmoidMatrix = new CJMatrix(rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sigmoidMatrix.set(i, j, sigmoidValue(elements[i][j]));
            }
        }

        return sigmoidMatrix;
    }

    public CJMatrix sigmoidDerivative() {
        CJMatrix sigmoidDerivMatrix = new CJMatrix(rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sigmoidDerivMatrix.set(i, j, sigmoidValue(elements[i][j]) * (1 - sigmoidValue(elements[i][j])));
            }
        }

        return sigmoidDerivMatrix;
    }

    private double sigmoidValue(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public CJMatrix softmax1D() {
        double denominator = 0;

        for (int row = 0; row < rows; row++) {
            denominator += Math.exp(elements[row][0]);
        }

        CJMatrix softmaxed = new CJMatrix(rows, 1);

        for (int row = 0; row < rows; row++) {
            softmaxed.set(row, 0, Math.exp(elements[row][0]) / denominator);
        }

        return softmaxed;
    }

    public CJMatrix softmaxDerivative() {
        return null;
    }

    // endregion


    // region CJMATRIX - SET/GET METHODS | WORKING

    public void set(int row, int column, double value) {
        elements[row][column] = value;
    }

    public double get(int row, int column) {
        return elements[row][column];
    }

    public int getRows() {
        return rows;
    }

    public CJMatrix getRow(int row) {
        CJMatrix rowVector = new CJMatrix(1, columns);

        for (int i = 0; i < columns; i++) {
            rowVector.set(0, i, elements[row][i]);
        }

        return rowVector;
    }

    public int getColumns() {
        return columns;
    }

    public CJMatrix getColumn(int column) {
        CJMatrix columnVector = new CJMatrix(rows, 1);

        for (int i = 0; i < rows; i++) {
            columnVector.set(i, 0, elements[i][column]);
        }

        return columnVector;
    }

    public int[] getSize() {
        return size;
    }

    // endregion


    // region CJMATRIX - TO METHODS | WORKING

    public String toString() {
        // METHOD ONE - OKAY
        /*
        String matrixString = "";

        int[] maxLengths = new int[columns];

        for (int i = 0; i < columns; i++) {
            int columnLength = 0;
            for (int j = 0; j < rows; j++) {
                int lineLength = String.valueOf(elements[j][i]).length();
                if (lineLength > columnLength) {
                    columnLength = lineLength;
                }
            }
            maxLengths[i] = columnLength;
        }



        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrixString += String.format("%" + maxLengths[j] + "f", elements[i][j]) + " ";
            }
            matrixString += "\n";
        }
        */

        // METHOD TWO - BETTER
        /*
        String matrixString = "\n\n" + rows + " by " + columns + " matrix: \n\n";

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrixString += String.format("%.5f", elements[i][j]) + "\t\t";
            }
            matrixString += "\n\n";
        }

        return matrixString;
        */

        // METHOD THREE - EVEN BETTER

        String matrixString = "\n\n" + rows + " by " + columns + " matrix: \n\n";

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                String numberString = String.valueOf(elements[i][j]);
                int decimalLocation = numberString.indexOf('.');

                matrixString += String.format("%." + (6 - decimalLocation) + "f", elements[i][j]) + "\t\t";
            }
            matrixString += "\n\n";
        }

        return matrixString;
    }

    public double[] to1DDoubleArray() {
        double[] array = new double[rows * columns];

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                array[row * columns + column] = elements[row][column];
            }
        }

        return array;
    }

    public double[][] to2DDoubleArray() {
        return elements;
    }

    // endregion

}
