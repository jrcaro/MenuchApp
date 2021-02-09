package com.example.menuchapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.menuchapp.Activities.AddFoodActivity;
import com.example.menuchapp.Activities.AddIngredientActivity;
import com.example.menuchapp.Fragments.ShopListFragment;
import com.example.menuchapp.SQLite.DatabaseAccess;
import com.example.menuchapp.SQLite.DatabaseContentActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvLAl, tvMAl, tvXAl, tvJAl, tvVAl, tvSAl, tvDAl,
            tvLCe, tvMCe, tvXCe, tvJCe, tvVCe, tvSCe, tvDCe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_toolbar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvLAl = findViewById(R.id.tvLunesLunch);
        tvMAl = findViewById(R.id.tvMartesLunch);
        tvXAl = findViewById(R.id.tvMiercolesLunch);
        tvJAl = findViewById(R.id.tvJuevesLunch);
        tvVAl = findViewById(R.id.tvViernesLunch);
        tvSAl = findViewById(R.id.tvSabadoLunch);
        tvDAl = findViewById(R.id.tvDomingoLunch);
        tvLCe = findViewById(R.id.tvLunesDinner);
        tvMCe = findViewById(R.id.tvMartesDinner);
        tvXCe = findViewById(R.id.tvMiercolesDinner);
        tvJCe = findViewById(R.id.tvJuevesDinner);
        tvVCe = findViewById(R.id.tvViernesDinner);
        tvSCe = findViewById(R.id.tvSabadoDinner);
        tvDCe = findViewById(R.id.tvDomingoDinner);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        if (!databaseAccess.isEmpty("menu")){
            ArrayList<String> menuLunch = databaseAccess.getArrayFrom("menu",
                    "time", "almuerzo", 2);
            tvLAl.setText(menuLunch.get(0).toUpperCase());
            tvMAl.setText(menuLunch.get(1).toUpperCase());
            tvXAl.setText(menuLunch.get(2).toUpperCase());
            tvJAl.setText(menuLunch.get(3).toUpperCase());
            tvVAl.setText(menuLunch.get(4).toUpperCase());
            tvSAl.setText(menuLunch.get(5).toUpperCase());
            tvDAl.setText(menuLunch.get(6).toUpperCase());

            ArrayList<String> menuDinner = databaseAccess.getArrayFrom("menu",
                    "time", "cena", 2);
            tvLCe.setText(menuDinner.get(0).toUpperCase());
            tvMCe.setText(menuDinner.get(1).toUpperCase());
            tvXCe.setText(menuDinner.get(2).toUpperCase());
            tvJCe.setText(menuDinner.get(3).toUpperCase());
            tvVCe.setText(menuDinner.get(4).toUpperCase());
            tvSCe.setText(menuDinner.get(5).toUpperCase());
            tvDCe.setText("PUCHERO");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.addFood:
                intent = new Intent(MainActivity.this, AddFoodActivity.class);
                startActivity(intent);
                return true;
            case R.id.addIngr:
                intent = new Intent(MainActivity.this, AddIngredientActivity.class);
                startActivity(intent);
                return true;
            case R.id.database:
                intent = new Intent(MainActivity.this, DatabaseContentActivity.class);
                startActivity(intent);
                return true;
            case R.id.shop_list:
                intent = new Intent(MainActivity.this, ShopListFragment.class);
                startActivity(intent);
                return true;
            case R.id.menu:
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();

                databaseAccess.deleteTable("menu");
                ArrayList<String> menuLunch = databaseAccess.getLunchFoodRandom();
                tvLAl.setText(menuLunch.get(0).toUpperCase());
                tvMAl.setText(menuLunch.get(1).toUpperCase());
                tvXAl.setText(menuLunch.get(2).toUpperCase());
                tvJAl.setText(menuLunch.get(3).toUpperCase());
                tvVAl.setText(menuLunch.get(4).toUpperCase());
                tvSAl.setText(menuLunch.get(5).toUpperCase());
                tvDAl.setText(menuLunch.get(6).toUpperCase());

                ArrayList<String> menuDinner = databaseAccess.getDinnerFoodRandom();
                tvLCe.setText(menuDinner.get(0).toUpperCase());
                tvMCe.setText(menuDinner.get(1).toUpperCase());
                tvXCe.setText(menuDinner.get(2).toUpperCase());
                tvJCe.setText(menuDinner.get(3).toUpperCase());
                tvVCe.setText(menuDinner.get(4).toUpperCase());
                tvSCe.setText(menuDinner.get(5).toUpperCase());
                tvDCe.setText("PUCHERO");

                ArrayList<String> menu= new ArrayList<String>();
                menu.addAll(menuLunch);
                menu.addAll(menuDinner);

                databaseAccess.getIngredientsRelated(menu);
                databaseAccess.close();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void action(int resid) {
        Toast.makeText(this, getText(resid), Toast.LENGTH_SHORT).show();
    }
}
