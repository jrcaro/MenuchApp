package com.example.menuchapp.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.menuchapp.Models.ShopModel;

import java.util.ArrayList;
import java.util.Random;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c = null;
    private final String[] DAYS_OF_WEEK = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null) {
            this.db.close();
        }
    }

    public String getDataFrom(String tableName) {
        c = db.rawQuery("select * from " + tableName, new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            int columnCount = c.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String data = c.getString(i);
                buffer.append(data + "\t\t| ");
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public String getDataFromFiltered(String tableName, String columnName, String colFiltered, String filter) {
        c = db.rawQuery("select " + columnName + " from " + tableName + " where " + colFiltered +
                " = " + filter, new String[]{});
        StringBuffer buffer = new StringBuffer();

        while (c.moveToNext()) {
            buffer.append(c.getString(0));
        }

        return buffer.toString();
    }

    public ArrayList<String> getArrayFrom(String tableName, String column, String filter, int columnIndex) {
        c = db.rawQuery("select * from " + tableName + " where "
                + column + "=" + "'" + filter + "'", new String[]{});

        StringBuffer buffer = new StringBuffer();
        ArrayList<String> result = new ArrayList<>();

        while (c.moveToNext()) {
            result.add(c.getString(columnIndex));
        }

        return result;
    }

    public ArrayList<ShopModel> getArrayFrom(String select, String tableName, String column, String filter) {
        c = db.rawQuery("select " + select + " from " + tableName + " where "
                + column + "=" + "'" + filter + "' order by name", new String[]{});

        StringBuffer buffer = new StringBuffer();
        ArrayList<ShopModel> result = new ArrayList<>();

        while (c.moveToNext()) {
            result.add(new ShopModel(c.getString(0),
                    c.getInt(1),
                    c.getString(2)));
        }

        for (ShopModel sh : result) {
            sh.setIngredient(sh.getIngredient());
        }

        return result;
    }

    public boolean isInDatabase(String tableName, String filter) {
        c = db.rawQuery("select * from " + tableName + " where name=" + "'" + filter + "'", new String[]{});
        int count = 0;
        while (c.moveToNext()) {
            count++;
        }

        return count > 0;
    }

    public boolean isEmpty(String tableName) {
        c = db.rawQuery("select * from " + tableName, new String[]{});
        int count = 0;
        while (c.moveToNext()) {
            count++;
        }

        return count == 0;
    }

    public ArrayList<String> getLunchFoodRandom() {
        long foodCount;
        Random rdnId = new Random();
        int selectID;
        int prevID = 1000;
        ArrayList<String> result = new ArrayList<>();
        ContentValues dataInsert = new ContentValues();
        String[] queries = new String[]{
                "type='legumbres' and (time='almuerzo' or time='') and weekend='0'",
                "type='pescado' and (time='almuerzo' or time='') and weekend='0'",
                "(time='almuerzo' or time='')",
        };
        int j = 0;

        for (String query : queries) {
            foodCount = DatabaseUtils.queryNumEntries(db, "food", query);
            c = db.rawQuery("select * from food where " + query + "order by RANDOM()", new String[]{});
            for (int i = 0; i < 2; i++) {
                selectID = rdnId.nextInt((int) foodCount);
                while(selectID == prevID){
                    selectID = rdnId.nextInt((int) foodCount);
                }
                prevID = selectID;

                if (c.moveToPosition(selectID)) {
                    result.add(c.getString(1));
                    dataInsert.put("day", DAYS_OF_WEEK[j]);
                    dataInsert.put("food", c.getString(1));
                    dataInsert.put("time", "almuerzo");
                    db.insert("menu", null, dataInsert);
                }
                j++;
            }
        }

        foodCount = DatabaseUtils.queryNumEntries(db, "food", "type='carne' and (time='almuerzo' or time='') and weekend='0'");
        c = db.rawQuery("select * from food where type='carne' and (time='almuerzo' or time='') and weekend='0' order by RANDOM()", new String[]{});
        selectID = rdnId.nextInt((int) foodCount + 1);
        if (c.moveToPosition(selectID)) {
            result.add(c.getString(1));
            dataInsert.put("day", DAYS_OF_WEEK[6]);
            dataInsert.put("food", c.getString(1));
            dataInsert.put("time", "almuerzo");
            db.insert("menu", null, dataInsert);
        }

        return result;
    }

    public ArrayList<String> getDinnerFoodRandom() {
        long foodCount;
        Random rdnId = new Random();
        int selectID;
        int prevID = 1000;
        ArrayList<String> result = new ArrayList<>();
        ContentValues dataInsert = new ContentValues();

        foodCount = DatabaseUtils.queryNumEntries(db, "food", "(time='cena' or time='') and weekend='0'");
        c = db.rawQuery("select * from food where (time='cena' or time='') and weekend='0' order by RANDOM()", new String[]{});
        for (int i = 0; i < 5; i++) {
            selectID = rdnId.nextInt((int) foodCount);
            while(selectID == prevID){
                selectID = rdnId.nextInt((int) foodCount);
            }
            prevID = selectID;

            if (c.moveToPosition(selectID)) {
                result.add(c.getString(1));
                dataInsert.put("day", DAYS_OF_WEEK[i]);
                dataInsert.put("food", c.getString(1));
                dataInsert.put("time", "cena");
                db.insert("menu", null, dataInsert);
            }
        }

        foodCount = DatabaseUtils.queryNumEntries(db, "food", "time='cena' or time=''");
        c = db.rawQuery("select * from food where time='cena' or time='' order by RANDOM()", new String[]{});
        selectID = rdnId.nextInt((int) foodCount + 1);
        if (c.moveToPosition(selectID)) {
            result.add(c.getString(1));
            dataInsert.put("day", DAYS_OF_WEEK[5]);
            dataInsert.put("food", c.getString(1));
            dataInsert.put("time", "cena");
            db.insert("menu", null, dataInsert);
        }

        return result;
    }

    public void getIngredientsRelated(ArrayList<String> food) {
        String query = "select name,place,count from ingredients inner join " +
                "(select id_ingredient, count(id_ingredient) as count from food_to_ingredients where id_food in (" +
                "select id from food where name in (";

        StringBuilder builder = new StringBuilder();
        for (String s : food) {
            builder.append("'" + s + "',");
        }
        String temp = builder.toString();
        String strQuery = query + temp.substring(0, temp.length() - 1) +
                ")) group by id_ingredient) as icount " +
                "on id=icount.id_ingredient";

        c = db.rawQuery(strQuery, new String[]{});
        ContentValues dataInsert = new ContentValues();
        db.delete("shop_list", null, null);

        while (c.moveToNext()) {
            dataInsert.put("name", c.getString(0));
            dataInsert.put("place", c.getString(1));
            dataInsert.put("count", c.getInt(2));
            db.insert("shop_list", null, dataInsert);
        }
    }

    public void setDataToFood(String dataRaw) {
        String[] data = dataRaw.split(",");
        ContentValues dataInsert = new ContentValues();
        String[] columns = {"name", "time", "type", "weekend"};

        for (int i = 0; i < data.length; i++) {
            dataInsert.put(columns[i], data[i]);
        }
        db.insert("food", null, dataInsert);
    }

    public void setDataToIngr(String dataRaw) {
        String[] data = dataRaw.split(",");
        ContentValues dataInsert = new ContentValues();
        String[] columns = {"name", "place"};

        for (int i = 0; i < data.length; i++) {
            dataInsert.put(columns[i], data[i]);
        }
        db.insert("ingredients", null, dataInsert);
    }

    public String[] getIngredients() {
        c = db.rawQuery("select name from ingredients order by name", new String[]{});
        String[] ingredients = new String[c.getCount()];
        int i = 0;
        while (c.moveToNext()) {
            ingredients[i] = c.getString(0);
            i++;
        }

        return ingredients;
    }

    public String[] getFoods() {
        c = db.rawQuery("select name from food order by name", new String[]{});
        String[] ingredients = new String[c.getCount()];
        int i = 0;
        while (c.moveToNext()) {
            ingredients[i] = c.getString(0);
            i++;
        }

        return ingredients;
    }

    public void setFoodToIngredients(String food, String ingreList) {
        ContentValues foodToIngr = new ContentValues();
        c = db.rawQuery("select * from food", new String[]{});
        c.moveToLast();
        int id_food = c.getInt(0);

        c = db.rawQuery("select * from ingredients where name in " + ingreList, new String[]{});
        while (c.moveToNext()) {
            foodToIngr.put("id_food", id_food);
            foodToIngr.put("id_ingredient", c.getInt(0));
            db.insert("food_to_ingredients", null, foodToIngr);
        }

    }

    public void setIngredientToShop(ArrayList<String> dataRaw) {
        for (String ingr : dataRaw) {
            if (isInDatabase("shop_list", ingr)) {
                c = db.rawQuery("select * from shop_list where name='" + ingr + "'", new String[]{});
                c.moveToFirst();
                int cuantity = c.getInt(3);
                setValueToShopList(ingr, cuantity + 1, "count");
            } else {
                ContentValues ingrToShop = new ContentValues();
                c = db.rawQuery("select * from ingredients where name='" + ingr + "'", new String[]{});
                c.moveToFirst();
                ingrToShop.put("name", c.getString(1));
                ingrToShop.put("place", c.getString(2));
                ingrToShop.put("count", 1);
                db.insert("shop_list", null, ingrToShop);
            }
        }
    }

    public void deleteTable(String tableName) {
        db.delete(tableName, null, null);
    }

    public void setValueToShopList(String filter, String value) {
        ContentValues valores = new ContentValues();
        valores.put("check_buy", value);

        db.update("shop_list", valores, "name='" + filter + "'", null);
    }

    public void setValueToShopList(String filter, int value, String column) {
        ContentValues valores = new ContentValues();
        valores.put(column, value);

        db.update("shop_list", valores, "name='" + filter + "'", null);
    }

    public void setValueToMenu(String filterDay, String filterTime, String value) {
        ;
        ContentValues valores = new ContentValues();
        valores.put("food", value.trim());

        db.update("menu", valores, "day='" + filterDay + "' and time='" + filterTime + "'", null);
    }
}