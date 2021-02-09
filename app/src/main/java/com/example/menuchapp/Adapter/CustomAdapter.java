package com.example.menuchapp.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menuchapp.Models.ShopModel;
import com.example.menuchapp.R;
import com.example.menuchapp.SQLite.DatabaseAccess;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    Context c;
    ArrayList<ShopModel> tvShop;
    LayoutInflater inflater;

    public CustomAdapter(Context c, ArrayList<ShopModel> tvShop) {
        this.c = c;
        this.tvShop = tvShop;
    }

    @Override
    public int getCount() {
        return tvShop.size();
    }

    @Override
    public Object getItem(int position) {
        return tvShop.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null)
        {
            inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.shop_list_model,parent,false);
        }

        TextView ingrTxt = (TextView) convertView.findViewById(R.id.ingrTxt);
        TextView countTxt = (TextView) convertView.findViewById(R.id.foodTxt);

        DatabaseAccess db = DatabaseAccess.getInstance(c.getApplicationContext());

        final int count = tvShop.get(position).getNumber();
        final String ingr = tvShop.get(position).getIngredient();
        boolean check = tvShop.get(position).getCheck().equals("1");
        String foodText = "x" + count;
        ingrTxt.setText(ingr);
        db.open();

        if (check) {
            ingrTxt.setPaintFlags(ingrTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            ingrTxt.setPaintFlags(ingrTxt.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        countTxt.setText(foodText);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = tvShop.get(position).getCheck().equals("1");
                if (check) {
                    ingrTxt.setPaintFlags(ingrTxt.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    db.setValueToShopList(tvShop.get(position).getIngredient(),
                            "0");
                } else {
                    ingrTxt.setPaintFlags(ingrTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    db.setValueToShopList(tvShop.get(position).getIngredient().toLowerCase(),
                            "1");
                }
                //Toast.makeText(c,ingr,Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}