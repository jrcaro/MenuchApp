package com.example.menuchapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.example.menuchapp.MainActivityGrid;
import com.example.menuchapp.R;
import com.example.menuchapp.SQLite.DatabaseAccess;

import java.util.ArrayList;

public class AddFoodActivity extends AppCompatActivity {

    private Button btnAdd, btnCancel, btnIngrs;
    private EditText etName;
    private RadioButton rbLunch, rbDinner, rbLeg, rbFish, rbMet, rbWeek, rbWeekend;
    private String strType, strTime, strWeek;
    private String foodIngredients = "(";
    MultiSelectDialog multiSelectDialog;
    private String TAG = "Cancel";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_food);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.add_food2);

        etName = findViewById(R.id.etName);
        btnAdd = findViewById(R.id.btnAddData);
        btnCancel = findViewById(R.id.btnCancel);
        rbLunch = findViewById(R.id.rbAlmuerzo);
        rbDinner = findViewById(R.id.rbCena);
        rbLeg = findViewById(R.id.rbLegumbres);
        rbFish = findViewById(R.id.rbPescado);
        rbMet = findViewById(R.id.rbCarne);
        rbWeek = findViewById(R.id.rbWeek);
        rbWeekend = findViewById(R.id.rbWeekend);
        btnIngrs = findViewById(R.id.btIngr);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        String[] ingrList = databaseAccess.getIngredients();
        databaseAccess.close();

        ArrayList<MultiSelectModel> listModel = createSampleData(ingrList);

        btnIngrs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiSelectDialog = new MultiSelectDialog()
                        .title(getResources().getString(R.string.add_ingr2)) //setting title for dialog
                        .titleSize(25)
                        .positiveText(getResources().getString(R.string.ok))
                        .negativeText(getResources().getString(R.string.cancel))
                        .setMinSelectionLimit(0)
                        .setMaxSelectionLimit(listModel.size())
                        //.preSelectIDsList(alreadySelectedCountries) //List of ids that you need to be selected
                        .multiSelectList(listModel) // the multi select model list with ids and name
                        .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                            @Override
                            public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String dataString) {
                                //will return list of selected IDS
                                for (int i = 0; i < selectedIds.size(); i++) {
                                    if (i == selectedIds.size() - 1) {
                                        foodIngredients = foodIngredients + "'" + selectedNames.get(i) + "')";
                                    } else {
                                        foodIngredients = foodIngredients + "'" + selectedNames.get(i) + "',";
                                    }
                                }
                            }

                            @Override
                            public void onCancel() {
                                Log.d(TAG, "Dialog cancelled");

                            }
                        });

                multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setError(null);

                String name = etName.getText().toString();

                if ("".equals(name)) {
                    etName.setError("Escribe el nombre de la comida");
                    etName.requestFocus();
                    return;
                }

                if (validate()) {
                    if (rbDinner.isChecked()) {
                        strTime = "cena";
                    } else {
                        strTime = "almuerzo";
                    }

                    if (rbLeg.isChecked()) {
                        strType = "legumbres";
                    } else if (rbFish.isChecked()) {
                        strType = "pescado";
                    } else if (rbMet.isChecked()) {
                        strType = "carne";
                    }

                    if (rbWeek.isChecked()) {
                        strWeek = "0";
                    } else {
                        strWeek = "1";
                    }

                    if (foodIngredients.split(",").length == 1) {
                        btnIngrs.setError("Añade los ingredientes");
                        btnIngrs.requestFocus();
                        return;
                    }

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();

                    if(databaseAccess.isInDatabase("food", name)){
                        etName.setError("Ya existe");
                        etName.requestFocus();
                        databaseAccess.close();
                    } else {
                        databaseAccess.setDataToFood(name + "," + strTime + "," + strType + "," + strWeek);
                        databaseAccess.setFoodToIngredients(name, foodIngredients);
                        foodIngredients = "(";
                        databaseAccess.close();
                        finish();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private boolean validate() {
        boolean validType, validTime, validWeek;

        if (!rbDinner.isChecked() && !rbLunch.isChecked()) {
            rbDinner.setError("Selec Item");
            validTime = false;
        } else {
            rbDinner.setError(null);
            validTime = true;
        }
        clearFocus();

        if (!rbLeg.isChecked() && !rbMet.isChecked() && !rbFish.isChecked()) {
            rbLeg.setError("Selec Item");
            validType = false;
        } else {
            rbMet.setError(null);
            rbFish.setError(null);
            validType = true;
        }
        clearFocus();

        if (!rbWeek.isChecked() && !rbWeekend.isChecked()) {
            rbWeek.setError("Selec Item");
            validWeek = false;
        } else {
            rbWeek.setError(null);
            validWeek = true;
        }
        clearFocus();

        if (validType && validTime && validWeek) {
            return true;
        } else {
            return false;
        }
    }

    private void clearFocus() {
        View view = this.getCurrentFocus();
        if (view != null && view instanceof EditText) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(AddFoodActivity.this, MainActivityGrid.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<MultiSelectModel> createSampleData(String[] list) {
        ArrayList<MultiSelectModel> ingrModel = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            ingrModel.add(new MultiSelectModel(i, list[i]));
        }
        return ingrModel;
    }
}
