package com.example.menuchapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.menuchapp.Adapter.CustomAdapter;
import com.example.menuchapp.Models.ShopModel;
import com.example.menuchapp.R;
import com.example.menuchapp.SQLite.DatabaseAccess;

import java.util.ArrayList;

public class FruteriaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fruteria_fragment, container, false);

        ListView lv = v.findViewById(R.id.lvFruteria);
        CustomAdapter adapter = new CustomAdapter(this.getActivity(), getShopList());
        lv.setAdapter(adapter);

        return v;
    }

    private ArrayList<ShopModel> getShopList() {

        ArrayList<ShopModel> list = new ArrayList<>();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.getContext());
        databaseAccess.open();

        list = databaseAccess.getArrayFrom("name, count(name), check_buy",
                "shop_list", "place", "fruteria");

        return list;
    }

    @Override
    public String toString() {
        String title="Fruteria";
        return title;
    }
}