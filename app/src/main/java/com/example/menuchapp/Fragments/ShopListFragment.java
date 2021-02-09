package com.example.menuchapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.example.menuchapp.Activities.AddFoodActivity;
import com.example.menuchapp.Adapter.CustomPagerAdapter;
import com.example.menuchapp.MainActivity;
import com.example.menuchapp.R;
import com.example.menuchapp.SQLite.DatabaseAccess;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ShopListFragment extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,ViewPager.OnPageChangeListener {

    ViewPager vp;
    TabLayout tabLayout;
    MultiSelectDialog multiSelectDialog;
    private String foodIngredients = "(";
    private String TAG = "Cancel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.shop_list);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        String[] ingrList = databaseAccess.getIngredients();

        ArrayList<MultiSelectModel> listModel = createSampleData(ingrList);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                        foodIngredients = foodIngredients + "'" + selectedNames.get(i).toLowerCase() + "')";
                                    } else {
                                        foodIngredients = foodIngredients + "'" + selectedNames.get(i).toLowerCase() + "',";
                                    }
                                }
                                databaseAccess.setIngredientToShop(foodIngredients);
                                foodIngredients = "(";
                                addPages();
                            }

                            @Override
                            public void onCancel() {
                                Log.d(TAG, "Dialog cancelled");

                            }
                        });

                multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");
            }
        });

        //VIEWPAGER
        vp = (ViewPager) findViewById(R.id.mViewpager_ID);
        this.addPages();

        //TABLAYOUT
        tabLayout = (TabLayout) findViewById(R.id.mTab_ID);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(vp);
        tabLayout.setOnTabSelectedListener(this);

    }

    private void addPages()
    {
        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(this.getSupportFragmentManager());
        pagerAdapter.addFragment(new MercadonaFragment());
        pagerAdapter.addFragment(new FruteriaFragment());
        pagerAdapter.addFragment(new CashFragment());

        //SET ADAPTER TO VP
        vp.setAdapter(pagerAdapter);
    }

    public void onTabSelected(TabLayout.Tab tab) {
        vp.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
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
