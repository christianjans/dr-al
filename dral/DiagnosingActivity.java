package com.cjkj.dral;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cjkj.dral.bts.CJDataManager;
import com.cjkj.dral.nn.CJNeuralNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class DiagnosingActivity extends AppCompatActivity {

    private View[] inputViews;
    private int numCategories;
    private double[] categoriesMax;
    private double[][] categoryValues;
    private String[][] categoryLabels;
    private CJDataManager.CategoryInputType[] categoryInputTypes;
    private CJNeuralNetwork cjNeuralNetwork;
    private boolean resultsAreShowing;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosing);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button cornerButton = new Button(this);
        cornerButton.setBackgroundColor(Color.WHITE);
        cornerButton.setText(R.string.diagnose_button);
        cornerButton.setTextColor(ContextCompat.getColor(this, R.color.colorSearchText));
        cornerButton.setAlpha(1);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 24, 24, 24);
        params.gravity = Gravity.END;
        cornerButton.setLayoutParams(params);
        cornerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCornerButtonAction();
            }
        });
        toolbar.addView(cornerButton);

        resultsAreShowing = false;

        filename = getIntent().getStringExtra("filename");

        createWithNewFile(filename);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resultsAreShowing = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // can be put into a switch statement if needed
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void createWithNewFile(String filename) {
        Map<String, Object> diagnosingData = CJDataManager.getDiagnosingData(this, filename);

        String title = (String) diagnosingData.get("title");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        String[] categories = (String[]) diagnosingData.get("categories");
        numCategories = (categories == null) ? 0 : categories.length;

        categoryInputTypes = (CJDataManager.CategoryInputType[]) diagnosingData.get("category_input_types");

        categoryLabels = (String[][]) diagnosingData.get("category_labels");

        categoryValues = (double[][]) diagnosingData.get("category_values");

        inputViews = new View[numCategories];

        createDiagnosticLayout(categories, categoryLabels, categoryInputTypes);

        @SuppressWarnings("unchecked")
        ArrayList<double[][]> weights = (ArrayList<double[][]>) diagnosingData.get("weights");
        @SuppressWarnings("unchecked")
        ArrayList<double[]> biases = (ArrayList<double[]>) diagnosingData.get("biases");

        if (weights != null && biases != null) {
            cjNeuralNetwork = new CJNeuralNetwork(weights, biases);
        }

        categoriesMax = (double[]) diagnosingData.get("categories_max");
    }

    private void createDiagnosticLayout(String[] categories, String[][] categoryLabels, CJDataManager.CategoryInputType[] categoryInputTypes) {
        TableLayout diagnosticLayout = findViewById(R.id.diagnosticTable);

        final int TABLEROW_HORIZONTAL_MARGINS = 16;
        final int TABLEROW_VERTICAL_MARGINS = 40;
        final int INPUTVIEW_VERTICAL_MARGINS = 16;

        TableLayout.LayoutParams tableRowParameters = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
        tableRowParameters.setMargins(TABLEROW_HORIZONTAL_MARGINS, TABLEROW_VERTICAL_MARGINS, TABLEROW_HORIZONTAL_MARGINS, TABLEROW_VERTICAL_MARGINS);

        LinearLayout.LayoutParams inputViewParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        inputViewParameters.setMargins(0, INPUTVIEW_VERTICAL_MARGINS, 0, INPUTVIEW_VERTICAL_MARGINS);

        for (int i = 0; i < numCategories; i++) {
            TableRow currentRow = new TableRow(this);
            currentRow.setLayoutParams(tableRowParameters);

            LinearLayout currentLayout = new LinearLayout(this);
            currentLayout.setOrientation(LinearLayout.VERTICAL);
            currentLayout.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView currentLabel = new TextView(this);
            currentLabel.setGravity(Gravity.START);
            currentLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            currentLabel.setText(categories[i]);
            currentLabel.setMaxLines(20);
            currentLabel.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            currentLayout.addView(currentLabel);

            switch (categoryInputTypes[i]) {
                case NUMBERICAL:
                    LinearLayout numericalLayout = new LinearLayout(this);
                    numericalLayout.setOrientation(LinearLayout.HORIZONTAL);
                    numericalLayout.setLayoutParams(inputViewParameters);

                    EditText numericalInput = new EditText(this);
                    numericalInput.setGravity(Gravity.END);
                    numericalInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    numericalInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    numericalInput.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.75f));
                    inputViews[i] = numericalInput;
                    numericalLayout.addView(numericalInput);

                    TextView currentUnits = new TextView(this);
                    currentUnits.setGravity(Gravity.CENTER | Gravity.START);
                    currentUnits.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    currentUnits.setText(categoryLabels[i][0]);
                    currentUnits.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.25f));
                    numericalLayout.addView(currentUnits);

                    currentLayout.addView(numericalLayout);
                    break;
                case RADIO:
                    RadioGroup radioGroup = new RadioGroup(this);
                    radioGroup.setLayoutParams(inputViewParameters);
                    radioGroup.setOrientation((categoryLabels[i].length > 2) ? RadioGroup.VERTICAL : RadioGroup.HORIZONTAL);

                    for (int j = 0; j < categoryLabels[i].length; j++) {
                        RadioButton currentRadioButton = new RadioButton(this);
                        currentRadioButton.setId(j);
                        currentRadioButton.setText(categoryLabels[i][j]);
                        currentRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        currentRadioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT, 1.0f));
                        radioGroup.addView(currentRadioButton);
                    }

                    inputViews[i] = radioGroup;
                    currentLayout.addView(radioGroup);
                    break;
                case DROPDOWN:
                    ArrayAdapter<String> spinnerContent = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryLabels[i]);

                    Spinner spinner = new Spinner(this);
                    spinner.setAdapter(spinnerContent);
                    spinner.setLayoutParams(inputViewParameters);

                    inputViews[i] = spinner;
                    currentLayout.addView(spinner);
                    break;
            }

            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setStartOffset(150 * i);

            currentRow.addView(currentLayout);
            currentRow.startAnimation(alphaAnimation);

            diagnosticLayout.addView(currentRow);
        }
    }

    private void handleCornerButtonAction() {
        if (allInputsRegistered() && !resultsAreShowing) {
            resultsAreShowing = true;

            hideSoftKeyboard(DiagnosingActivity.this);
            double[] patientInputs = getInputsFromViews();
            double[] outputArray = cjNeuralNetwork.feedForward(patientInputs, categoriesMax);

            Bundle bundle = new Bundle();
            bundle.putSerializable("category_types", categoryInputTypes);
            bundle.putSerializable("category_labels", categoryLabels);
            bundle.putSerializable("category_values", categoryValues);

            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtras(bundle);
            intent.putExtra("diagnosis_fraction", outputArray[0]);
            intent.putExtra("patient_inputs", patientInputs);
            intent.putExtra("filename", filename);
            startActivity(intent);
        } else {
            Snackbar output = Snackbar.make(findViewById(R.id.diagnosticTable), "Please answer all questions.", Snackbar.LENGTH_SHORT);
            output.show();
        }
    }

    private boolean allInputsRegistered() {
        for (View inputView : inputViews) {
            if (inputView instanceof EditText) {
                if (((EditText) inputView).getText().toString().equals("")) {
                    return false;
                }
            } else if (inputView instanceof RadioGroup) {
                if (((RadioGroup) inputView).getCheckedRadioButtonId() == -1) {
                    return false;
                }
            } else if (inputView instanceof Spinner) {
                if (((Spinner) inputView).getSelectedItemPosition() == -1) {
                    return false;
                }
            }
        }

        return true;
    }

    private double[] getInputsFromViews() {
        double[] inputs = new double[numCategories];

        for (int i = 0; i < numCategories; i++) {
            View inputView = inputViews[i];

            if (inputView instanceof EditText) {
                inputs[i] = Double.valueOf(((EditText) inputView).getText().toString());
            } else if (inputView instanceof RadioGroup) {
                inputs[i] = categoryValues[i][((RadioGroup) inputView).getCheckedRadioButtonId()];
            } else if (inputView instanceof Spinner) {
                inputs[i] = categoryValues[i][((Spinner) inputView).getSelectedItemPosition()];
            }
        }

        return inputs;
    }

    private double getMax(double[] array) {
        double max = array[0];

        for (double element : array) {
            if (element > max) {
                max = element;
            }
        }

        return max;
    }

    private int getIndexOfMax(double[] array) {
        double max = getMax(array);

        for (int i = 0; i < array.length; i++) {
            if (array[i] == max) {
                return i;
            }
        }

        return 0;
    }

}
