package com.cjkj.dral;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.cjkj.dral.bts.CJDataManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity {

    private final int BUTTONS_PER_ROW = 2;  // number of displayed buttons per row
    private int SPACING_BETWEEN_ELEMENTS;   // space between elements in activity
    private int BUTTON_LENGTH;              // button side length
    private Button[] buttons;               // array to hold the buttons
    private TableLayout buttonLayout;       // table layout to contain the buttons

    private final String WARNING_KEY = "WARNING_VALUE";     // key for warning value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (warningIsOn()) {
            CJDataManager.saveAssetsAsFiles(this);

            AlertDialog.Builder warningDialog = new AlertDialog.Builder(this);
            warningDialog.setTitle(getResources().getString(R.string.warning_title));
            warningDialog.setMessage(getResources().getString(R.string.warning_message));
            warningDialog.setCancelable(false);

            warningDialog.setPositiveButton(
                    getResources().getString(R.string.warning_dismiss),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            saveWarning(false);
                            dialog.cancel();
                        }
                    });

            warningDialog.setNegativeButton(
                    getResources().getString(R.string.warning_okay),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog warning = warningDialog.create();
            warning.show();
        }

        // link the table layout and set it to hide keyboard if tapped
        buttonLayout = findViewById(R.id.buttonTable);
        buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(MainActivity.this);
            }
        });

        // configure search bar
        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i == 0 && i1 == 1 && i2 == 0) {
                    // no text in edit-text
                    search("");
                } else {
                    search(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // set the spacing between elements
        SPACING_BETWEEN_ELEMENTS = (int) getResources().getDimension(R.dimen.main_space_between_elements);

        // get the display object for the width and height of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        BUTTON_LENGTH = size.x / BUTTONS_PER_ROW - SPACING_BETWEEN_ELEMENTS;

        // create the button layout
        Map<String, Object> displayData = CJDataManager.getDisplayData(this);

        // get the filenames of the files in directory
        String[] filenames = (String[]) displayData.get("filenames");
        // get the titles of the files in directory
        String[] titles = (String[]) displayData.get("titles");

        // create the button layout if able
        if (filenames != null && titles != null) {
            createButtonLayout(filenames, titles);
        }
    }

    // hide the keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    // create the button layout for display
    private void createButtonLayout(final String[] filenames, String[] buttonLabels) {
        // init the buttons array
        buttons = new Button[buttonLabels.length];

        // array of the button colours
        int[] buttonColours = getResources().getIntArray(R.array.buttonColors);
        // button padding
        int buttonPadding = (int) getResources().getDimension(R.dimen.main_button_padding);

        // loop through each row
        for (int row = 0; row < Math.round((double) buttonLabels.length / BUTTONS_PER_ROW); row++) {
            // create the table row for this row
            TableRow currentRow = new TableRow(this);

            // set the layout parameters of the current table row
            TableLayout.LayoutParams tableRowParameters = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
            tableRowParameters.setMargins(0, SPACING_BETWEEN_ELEMENTS / 2, 0, SPACING_BETWEEN_ELEMENTS / 2);
            currentRow.setLayoutParams(tableRowParameters);

            // loop through each button in this row
            for (int column = 0; column < BUTTONS_PER_ROW; column++) {
                // calculate the absolute button index
                final int currentIndex = row * BUTTONS_PER_ROW + column;

                // create the button for this row
                Button currentButton = new Button(this);
                currentButton.setBackgroundColor(buttonColours[new Random().nextInt(buttonColours.length)]);
                currentButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                currentButton.setTextColor(getResources().getColor(R.color.colorSearchText));
                currentButton.setMaxLines(20);
                currentButton.setPadding(buttonPadding, buttonPadding, buttonPadding, buttonPadding);

                // create the layout parameters for the button in the table row
                TableRow.LayoutParams buttonParameters = new TableRow.LayoutParams(BUTTON_LENGTH, BUTTON_LENGTH, 1.0f);
                buttonParameters.gravity = Gravity.CENTER_VERTICAL;
                if ((column + 1) % BUTTONS_PER_ROW != 0) {
                    // add a right margin to every second button (beginning at the first)
                    buttonParameters.setMargins(0, 0, SPACING_BETWEEN_ELEMENTS, 0);
                }
                currentButton.setLayoutParams(buttonParameters);

                // set the last button in a row invisible if it exceeds the number of buttons available
                if (currentIndex < buttonLabels.length) {
                    // current button has a filename and title
                    currentButton.setText(buttonLabels[currentIndex]);
                    currentButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goDiagnose(filenames[currentIndex]);
                        }
                    });

                    buttons[currentIndex] = currentButton;
                } else {
                    // button has no filename or title
                    currentButton.setClickable(false);
                    currentButton.setAlpha(0);
                }

                // add the current button to the row
                currentRow.addView(currentButton);
            }

            // add the current row to the table layout
            buttonLayout.addView(currentRow);
        }
    }

    // refresh button layout for changes in the search query
    private void refreshButtonLayout(Button[] applicableButtons) {
        // remove all rows (and thus, buttons) from the table layout
        buttonLayout.removeAllViews();

        // loop through each row
        for (int row = 0; row < Math.round((double) applicableButtons.length / BUTTONS_PER_ROW); row++) {
            // create the current row
            TableRow currentRow = new TableRow(this);

            // add the layout parameters for the row
            TableLayout.LayoutParams tableRowParameters = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
            tableRowParameters.setMargins(0, SPACING_BETWEEN_ELEMENTS / 2, 0, SPACING_BETWEEN_ELEMENTS / 2);
            currentRow.setLayoutParams(tableRowParameters);

            // loop through for each button in the current row
            for (int column = 0; column < BUTTONS_PER_ROW; column++) {
                // calculate the absolute button index
                int currentIndex = row * BUTTONS_PER_ROW + column;
                // make a new button
                Button currentButton;

                // set the last button in a row invisible if it exceeds the number of buttons available
                if (currentIndex < applicableButtons.length) {
                    // make the button clickable
                    currentButton = applicableButtons[currentIndex];
                    ((ViewGroup) currentButton.getParent()).removeView(currentButton);
                } else {
                    // set the current button to not-clickable and invisible
                    currentButton = new Button(this);
                    currentButton.setClickable(false);
                    currentButton.setAlpha(0);
                }

                // set the button parameters for it in the table row
                TableRow.LayoutParams buttonParameters = new TableRow.LayoutParams(BUTTON_LENGTH, BUTTON_LENGTH, 1.0f);
                buttonParameters.gravity = Gravity.CENTER_VERTICAL;
                if ((column + 1) % BUTTONS_PER_ROW != 0) {
                    // add a right margin to every second button (beginning at the first)
                    buttonParameters.setMargins(0, 0, SPACING_BETWEEN_ELEMENTS, 0);
                }
                currentButton.setLayoutParams(buttonParameters);

                // add a fade-in animation to the button (length of animation depends on button position)
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                alphaAnimation.setDuration(500);
                alphaAnimation.setFillAfter(true);
                alphaAnimation.setStartOffset(100 * currentIndex);
                currentButton.startAnimation(alphaAnimation);

                // add the current button to the current row
                currentRow.addView(currentButton);
            }

            // add the current row to the table layout
            buttonLayout.addView(currentRow);
        }
    }

    // search through the titles of the buttons for a query
    private void search(String searchString) {
        // create a list to hold the indices that match the query
        ArrayList<Integer> applicableIndices = new ArrayList<>();

        // loop through the button titles to see if they match the query
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getText().toString().toLowerCase().contains(searchString.toLowerCase())) {
                // add the matching query to the list
                applicableIndices.add(i);
            }
        }

        // determine whether to update the layout
        if (applicableIndices.size() > 0) {
            // create an array to hold buttons that have titles that match the query
            Button[] applicableButtons = new Button[applicableIndices.size()];

            // add matching buttons to the array
            for (int i = 0; i < applicableIndices.size(); i++) {
                applicableButtons[i] = buttons[applicableIndices.get(i)];
            }

            // refresh the table layout with applicable buttons
            refreshButtonLayout(applicableButtons);
        } else {
            // no titles match the search, refresh the table with no buttons
            refreshButtonLayout(new Button[0]);
        }
    }

    // go to the diagnosing activity
    private void goDiagnose(String type) {
        Intent intent = new Intent(this, DiagnosingActivity.class);
        intent.putExtra("filename", type);
        startActivity(intent);
    }

    private void saveWarning(boolean value) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(WARNING_KEY, value);
        editor.apply();
    }

    private boolean warningIsOn() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        return sharedPreferences.getBoolean(WARNING_KEY, true);
    }

}
