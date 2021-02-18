package com.example.menuchapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.example.menuchapp.Activities.AddFoodActivity;
import com.example.menuchapp.Activities.AddIngredientActivity;
import com.example.menuchapp.Adapter.CustomRecyclerViewAdapter;
import com.example.menuchapp.Fragments.ShopListFragment;
import com.example.menuchapp.SQLite.DatabaseAccess;
import com.example.menuchapp.Activities.DatabaseContentActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivityGrid extends AppCompatActivity implements CustomRecyclerViewAdapter.ItemClickListener {
    private final String[] DAYS_OF_WEEK = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
    private static List<Integer> FOOD_CELLS;
    static {
        FOOD_CELLS = new ArrayList<>(14);
        FOOD_CELLS = Arrays.asList(1,2,4,5,7,8,10,11,13,14,16,17,19,20);
    }
    public static Map<Integer, Pair<String, String>> DAYS_CELLS;
    static {
        DAYS_CELLS = new HashMap<>();
        String[] DAYS_OF_WEEK = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
        for (int k = 0; k < DAYS_OF_WEEK.length; k++){
            DAYS_CELLS.put(3*k+1, new Pair<>(DAYS_OF_WEEK[k], "almuerzo"));
            DAYS_CELLS.put(3*k+2, new Pair<>(DAYS_OF_WEEK[k], "cena"));
        }
    }
    public static final int numberOfColumns = 3;
    private String[] data;
    RecyclerView recyclerView;
    MultiSelectDialog multiSelectDialog;
    String[] foodList;
    ArrayList<MultiSelectModel> listModel;
    CustomRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_grid);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.rvNumbers);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        foodList = databaseAccess.getFoods();
        listModel = createSampleData(foodList);

        setRecycler(!databaseAccess.isEmpty("menu"), false);
        databaseAccess.close();
    }

    @Override
    public void onItemClick(View view, int position) {
        if(FOOD_CELLS.contains(position)){

            multiSelectDialog = new MultiSelectDialog()
                    .title(getResources().getString(R.string.add_ingr2)) //setting title for dialog
                    .titleSize(25)
                    .positiveText(getResources().getString(R.string.ok))
                    .negativeText(getResources().getString(R.string.cancel))
                    .setMinSelectionLimit(0)
                    .setMaxSelectionLimit(1)
                    //.preSelectIDsList(alreadySelectedCountries) //List of ids that you need to be selected
                    .multiSelectList(listModel) // the multi select model list with ids and name
                    .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                        @Override
                        public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String dataString) {
                            String filterDay = DAYS_CELLS.get(position).first;
                            String filterTime = DAYS_CELLS.get(position).second;

                            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                            databaseAccess.open();

                            databaseAccess.setValueToMenu(filterDay, filterTime, dataString);
                            setRecycler(true, true);
                            databaseAccess.close();
                            //Log.i("TAG", filterDay + " " + filterTime);
                        }

                        @Override
                        public void onCancel() {
                            Log.i("TAG", "Dialog cancelled");
                        }
                    });

            multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");
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
                intent = new Intent(MainActivityGrid.this, AddFoodActivity.class);
                startActivity(intent);
                return true;
            case R.id.addIngr:
                intent = new Intent(MainActivityGrid.this, AddIngredientActivity.class);
                startActivity(intent);
                return true;
            case R.id.database:
                intent = new Intent(MainActivityGrid.this, DatabaseContentActivity.class);
                startActivity(intent);
                return true;
            case R.id.shop_list:
                intent = new Intent(MainActivityGrid.this, ShopListFragment.class);
                startActivity(intent);
                return true;
            case R.id.menu:
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();

                databaseAccess.deleteTable("menu");
                ArrayList<String> menuLunch = databaseAccess.getLunchFoodRandom();
                ArrayList<String> menuDinner = databaseAccess.getDinnerFoodRandom();
                menuDinner.add("Puchero");
                ArrayList<String> recyclerData = new ArrayList<>();

                for (int i = 0; i < menuLunch.size(); i++) {
                    recyclerData.add(DAYS_OF_WEEK[i]);
                    recyclerData.add(menuLunch.get(i));
                    recyclerData.add(menuDinner.get(i));
                }

                data = recyclerData.toArray(new String[0]);
                // set up the RecyclerView
                recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
                recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                adapter = new CustomRecyclerViewAdapter(this, data);
                adapter.setClickListener(this);
                recyclerView.setAdapter(adapter);

                ArrayList<String> menu = new ArrayList<String>();
                menu.addAll(menuLunch);
                menuDinner.remove("Puchero");
                menu.addAll(menuDinner);

                databaseAccess.getIngredientsRelated(menu);
                databaseAccess.close();
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

    public void setRecycler(boolean menuEmpty, boolean shopList) {
        String[] data;
        ArrayList<String> recyclerData = new ArrayList<>();
        ArrayList<String> menu = new ArrayList<String>();

        if(menuEmpty){
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
            databaseAccess.open();
            ArrayList<String> menuLunch = databaseAccess.getArrayFrom("menu",
                    "time", "almuerzo", 2);
            ArrayList<String> menuDinner = databaseAccess.getArrayFrom("menu",
                    "time", "cena", 2);
            menuDinner.add("Puchero");

            for (int i = 0; i < DAYS_OF_WEEK.length; i++) {
                recyclerData.add(DAYS_OF_WEEK[i]);
                recyclerData.add(menuLunch.get(i));
                recyclerData.add(menuDinner.get(i));
            }
            if(shopList){
                menu.addAll(menuLunch);
                menuDinner.remove("Puchero");
                menu.addAll(menuDinner);
                databaseAccess.getIngredientsRelated(menu);
            }

            databaseAccess.close();
        } else {
            for (int i = 0; i < DAYS_OF_WEEK.length; i++) {
                recyclerData.add(DAYS_OF_WEEK[i]);
                recyclerData.add("");
                recyclerData.add("");
            }
        }

        data = recyclerData.toArray(new String[0]);

        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new CustomRecyclerViewAdapter(this, data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }
}
