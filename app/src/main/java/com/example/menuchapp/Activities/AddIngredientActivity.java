package com.example.menuchapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.menuchapp.MainActivity;
import com.example.menuchapp.R;
import com.example.menuchapp.SQLite.DatabaseAccess;

public class AddIngredientActivity extends AppCompatActivity {

    private Button btnAdd, btnCancel;
    private EditText etName;
    private RadioButton rbMercadona, rbFruteria, rbCarniceria;
    private String strPlace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.add_ingr2);

        etName = findViewById(R.id.etName);
        btnAdd = findViewById(R.id.btnAddData);
        btnCancel = findViewById(R.id.btnCancel);
        rbMercadona = findViewById(R.id.rbMercadona);
        rbFruteria = findViewById(R.id.rbFruteria);
        rbCarniceria = findViewById(R.id.rbCash);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setError(null);

                String name = etName.getText().toString();

                if ("".equals(name)) {
                    etName.setError("Escribe el nombre del ingrediente");
                    etName.requestFocus();
                    return;
                }

                if(validate()){
                    if(rbMercadona.isChecked()){
                        strPlace = "Mercadona";
                    } else if(rbFruteria.isChecked()){
                        strPlace = "Fruteria";
                    } else if(rbCarniceria.isChecked()){
                        strPlace = "CashFresh";
                    }

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();

                    if(databaseAccess.isInDatabase("ingredients", name)){
                        etName.setError("Ya existe");
                        etName.requestFocus();
                        databaseAccess.close();
                    } else {
                        databaseAccess.setDataToIngr(name + "," + strPlace);
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
        boolean validPlace;

        if(!rbMercadona.isChecked() && !rbFruteria.isChecked() && !rbCarniceria.isChecked()){
            rbMercadona.setError("Selec Item");
            validPlace = false;
        } else {
            rbMercadona.setError(null);
            rbFruteria.setError(null);
            validPlace = true;
        }
        clearFocus();

        if(validPlace){
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

                Intent intent = new Intent(AddIngredientActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}