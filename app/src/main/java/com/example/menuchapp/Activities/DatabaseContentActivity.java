package com.example.menuchapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menuchapp.MainActivityGrid;
import com.example.menuchapp.R;
import com.example.menuchapp.SQLite.DatabaseAccess;

public class DatabaseContentActivity extends AppCompatActivity {

    public Spinner spName;
    public EditText etFilter;
    public Button btnQuery;
    public TextView tvResult;
    private String tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_content);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.debug);

        spName = findViewById(R.id.spName);
        String[] items = new String[]{"food", "ingredients", "food_to_ingredients", "menu", "shop_list"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        spName.setAdapter(adapter);

        btnQuery = findViewById(R.id.btnQuery);
        tvResult = findViewById(R.id.tvResult);
        tvResult.setMovementMethod(new ScrollingMovementMethod());
        etFilter = findViewById(R.id.etName);

        spName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                tableName = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();

                if("".equals(etFilter.getText().toString())){
                    String data = databaseAccess.getDataFrom(tableName);
                    tvResult.setText(data);
                } else {
                    String strToast;
                    if (databaseAccess.isInDatabase(tableName, etFilter.getText().toString())){
                        strToast = "Ya existe";
                    } else {
                        strToast = "No existe";
                    }
                    Toast.makeText(getApplicationContext(), strToast, Toast.LENGTH_SHORT).show();
                }

                databaseAccess.close();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(DatabaseContentActivity.this, MainActivityGrid.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}