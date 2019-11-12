package com.cjkj.dral;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cjkj.dral.bts.CJDataManager;
import com.cjkj.dral.views.DiagnosisView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class ResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(layoutParams);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.75));

        int percentageResult = (int) Math.floor(100 * getIntent().getDoubleExtra("diagnosis_fraction", 0.0));

        DiagnosisView diagnosisView = findViewById(R.id.resultsView);
        diagnosisView.setPercentage(percentageResult);
        diagnosisView.setLabelColor(getResources().getColor(R.color.colorSearchText));
        diagnosisView.setCircleColor(Color.RED);
        diagnosisView.startAnimation();

        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation1.setDuration(1000);
        alphaAnimation1.setStartOffset(1000);
        alphaAnimation1.setFillAfter(true);

        TextView diagnosisTextView = findViewById(R.id.diagnosisTextView);
        diagnosisTextView.setText(String.format(getResources().getString(R.string.results_percentage), (percentageResult >= 99.5) ? percentageResult - 1 : percentageResult));
        diagnosisTextView.startAnimation(alphaAnimation1);

        AlphaAnimation alphaAnimation2 = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation2.setDuration(1000);
        alphaAnimation2.setStartOffset(2000);
        alphaAnimation2.setFillAfter(true);

        TextView olyTextView = findViewById(R.id.olyTextView);
        olyTextView.startAnimation(alphaAnimation2);

        createOthersLayout();
    }

    private void createOthersLayout() {
        TableLayout othersTable = findViewById(R.id.othersTable);
        double[] patientInputs = getIntent().getDoubleArrayExtra("patient_inputs");
        String[] otherPeople = getSimilarPeople(patientInputs);

        TableLayout.LayoutParams tableRowParameters = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        tableRowParameters.setMargins(0, 8, 0, 8);

        TableRow.LayoutParams linearLayoutParameters = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams textViewParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);

        for (int i = 0; i < otherPeople.length; i++) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setStartOffset(2500 + 100 * i);
            alphaAnimation.setFillAfter(true);

            TableRow currentRow = new TableRow(this);
            currentRow.setLayoutParams(tableRowParameters);
            currentRow.startAnimation(alphaAnimation);

            LinearLayout currentLayout = new LinearLayout(this);
            currentLayout.setOrientation(LinearLayout.VERTICAL);
            currentLayout.setLayoutParams(linearLayoutParameters);

            TextView titleText = new TextView(this);
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            titleText.setTextColor(getResources().getColor(R.color.colorSearchText));
            titleText.setText(getRandomName());
            titleText.setTypeface(null, Typeface.BOLD);
            titleText.setMaxLines(20);
            titleText.setLayoutParams(textViewParameters);
            currentLayout.addView(titleText);

            TextView subtitleText = new TextView(this);
            subtitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            subtitleText.setTextColor(getResources().getColor(R.color.colorSearchText));
            subtitleText.setText(Html.fromHtml(otherPeople[i]));
            subtitleText.setMaxLines(20);
            subtitleText.setLayoutParams(textViewParameters);
            currentLayout.addView(subtitleText);

            currentRow.addView(currentLayout);
            othersTable.addView(currentRow);
        }
    }

    private String getRandomName() {
        String[] names = new String[]{"Adam", "Beatrice", "Christopher", "Danielle", "Eric", "Felicity", "Greg", "Hannah", "Ivan",
                "Juliet", "Klaus", "Lemony", "Matthew", "Natalie", "Olaf", "Patricia", "Quigley",
                "Rachel", "Sunny", "Tracy", "Udo", "Violet", "William", "Xena", "Yasmin", "Zach"};

        return names[(int) (Math.random() * names.length)];
    }

    private String[] getSimilarPeople(double[] patientInputs) {
        final int NUM_IN_PODIUM = 10;
        final int THRESHOLD = 2;
        double[][] othersInputs = CJDataManager.getOthersData(this, getIntent().getStringExtra("filename"));

        int[] similarOthersIndices = new int[NUM_IN_PODIUM * patientInputs.length];
        String[] othersInputsString;

        if (othersInputs != null) {
            for (int category = 0; category < patientInputs.length; category++) {
                double currentPatientInput = patientInputs[category];
                double[] differences = new double[othersInputs.length];

                for (int other = 0; other < othersInputs.length; other++) {
                    differences[other] = Math.abs(currentPatientInput - othersInputs[other][category]);
                }

                int[] lowestIndices = getLowestIndices(differences, NUM_IN_PODIUM);

                if (lowestIndices != null) {
                    System.arraycopy(lowestIndices, 0, similarOthersIndices, category * NUM_IN_PODIUM, lowestIndices.length);
                }
            }

            int[] condensedSimilarIndices = getMostOccurringNumbers(similarOthersIndices, THRESHOLD);
            othersInputsString = new String[condensedSimilarIndices.length];

            for (int similarPersonIndex = 0; similarPersonIndex < condensedSimilarIndices.length; similarPersonIndex++) {
                CJDataManager.CategoryInputType[] categoryInputTypes = (CJDataManager.CategoryInputType[]) getIntent().getSerializableExtra("category_types");
                String[][] categoryLabels = (String[][]) getIntent().getSerializableExtra("category_labels");
                double[][] categoryValues = (double[][]) getIntent().getSerializableExtra("category_values");

                StringBuilder personString = new StringBuilder();

                //System.out.println("others inputs: " + Arrays.deepToString(othersInputs));

                for (int category = 0; category < patientInputs.length; category++) {
                    String[] currentCategoryLabels = categoryLabels[category];
                    double[] currentCategoryValues = categoryValues[category];
                    double othersInput = othersInputs[condensedSimilarIndices[similarPersonIndex]][category];

                    //System.out.println("current others input: " + othersInput);

                    switch (categoryInputTypes[category]) {
                        case NUMBERICAL:
                            //System.out.println("numerical input");
                            //System.out.println("output string is: " + othersInput + " " + currentCategoryLabels[0]);

                            personString.append(othersInput);
                            personString.append(" ");
                            personString.append(currentCategoryLabels[0]);
                            break;
                        case RADIO:
                            personString.append(currentCategoryLabels[indexOfValueInArray(othersInput, currentCategoryValues)]);
                            break;
                        case DROPDOWN:
                            personString.append(currentCategoryLabels[indexOfValueInArray(othersInput, currentCategoryValues)]);
                            break;
                    }

                    if (category != patientInputs.length - 1) {
                        personString.append(", ");
                    }
                }

                if (othersInputs[condensedSimilarIndices[similarPersonIndex]][patientInputs.length] == 1) {
                    personString.append(" - <b>has this illness</b>");
                } else {
                    personString.append(" - <b>does not have this illness</b>");
                }

                othersInputsString[similarPersonIndex] = personString.toString().trim();
            }

            return othersInputsString;
        }

        return new String[0];
    }

    private int[] getLowestIndices(final double[] inputs, int numIndices) {
        Integer[] indices = new Integer[inputs.length];
        int[] returnIndices = new int[numIndices];

        for (int i = 0; i < inputs.length; i++){
            indices[i] = i;
        }

        Arrays.sort(indices, new Comparator<Integer>() {
            @Override public int compare(final Integer o1, final Integer o2) {
                return Double.compare(inputs[o1], inputs[o2]);
            }
        });

        for (int i = 0; i < numIndices; i++) {
            returnIndices[i] = indices[i];
        }

        return returnIndices;
    }

    private int[] getMostOccurringNumbers(int[] inputs, int threshold) {
        SparseIntArray sparseIntArray = new SparseIntArray();
        ArrayList<Integer> returnNumbersList = new ArrayList<>();

        for (int input : inputs) {
            int frequency = sparseIntArray.get(input);
            sparseIntArray.put(input, frequency + 1);
        }

        for (int i = 0; i < sparseIntArray.size(); i++) {
            if (sparseIntArray.valueAt(i) >= threshold) {
                returnNumbersList.add(sparseIntArray.keyAt(i));
            }
        }

        int[] returnNumbersArray = new int[returnNumbersList.size()];

        for (int i = 0; i < returnNumbersList.size(); i++) {
            returnNumbersArray[i] = returnNumbersList.get(i);
        }

        return returnNumbersArray;
    }

    private int indexOfValueInArray(double value, double[] array) {
        for (int index = 0; index < array.length; index++) {
            if (array[index] == value) {
                return index;
            }
        }

        return -1;
    }

}
