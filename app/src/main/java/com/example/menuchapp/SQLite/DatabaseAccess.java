package com.example.menuchapp.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.menuchapp.Models.ShopModel;

import java.util.ArrayList;

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
        if(db != null) {
            this.db.close();
        }
    }

    public String getDataFrom(String tableName) {
        c = db.rawQuery("select * from " + tableName, new String[]{});
        StringBuffer buffer = new StringBuffer();
        while(c.moveToNext()){
            int columnCount = c.getColumnCount();
            for (int i =0; i<columnCount; i++){
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

        while(c.moveToNext()){
            buffer.append(c.getString(0));
        }

        return buffer.toString();
    }

    public ArrayList<String> getArrayFrom(String tableName, String column, String filter, int columnIndex) {
        c = db.rawQuery("select * from " + tableName + " where "
                + column + "=" + "'"+ filter + "'", new String[]{});

        StringBuffer buffer = new StringBuffer();
        ArrayList<String> result = new ArrayList<>();

        while(c.moveToNext()){
            result.add(c.getString(columnIndex));
        }

        return result;
    }

    public ArrayList<ShopModel> getArrayFrom(String select, String tableName, String column, String filter) {
        c = db.rawQuery("select " + select + " from " + tableName + " where "
                + column + "=" + "'"+ filter + "' group by name order by name", new String[]{});

        StringBuffer buffer = new StringBuffer();
        ArrayList<ShopModel> result = new ArrayList<>();

        while(c.moveToNext()){
            result.add(new ShopModel(c.getString(0),
                    c.getInt(1),
                    c.getString(2)));
        }

        for(ShopModel sh : result){
            sh.setIngredient(sh.getIngredient().substring(0, 1).toUpperCase() + sh.getIngredient().substring(1));
        }

        return result;
    }

    public boolean isInDatabase(String tableName, String filter) {
        c = db.rawQuery("select * from " + tableName + " where name=" + "'"+ filter + "'", new String[]{});
        int count = 0;
        while(c.moveToNext()){
            count++;
        }

        return count > 0;
    }

    public boolean isEmpty(String tableName) {
        c = db.rawQuery("select * from " + tableName, new String[]{});
        int count = 0;
        while(c.moveToNext()){
            count++;
        }

        return count == 0;
    }

    public ArrayList<String> getLunchFoodRandom() {
        String strQuery = "select * from (select * from food" +
                " where type='legumbres'" +
                " and time='almuerzo'" +
                " order by RANDOM() limit 3)" +
                " union select * from (select * from food" +
                " where type='pescado'" +
                " and time='almuerzo'" +
                " order by RANDOM() limit 2)" +
                " union select * from (select * from food" +
                " where type='carne'" +
                " and time='almuerzo'" +
                " order by RANDOM() limit 2)";

        c = db.rawQuery(strQuery, new String[]{});
        ArrayList<String> result = new ArrayList<>();

        ContentValues dataInsert = new ContentValues();
        int i = 0;

        while(c.moveToNext()){
            result.add(c.getString(1));
            dataInsert.put("day", DAYS_OF_WEEK[i]);
            dataInsert.put("food", c.getString(1));
            dataInsert.put("time", "almuerzo");
            db.insert("menu", null, dataInsert);
            i++;
        }

        return result;
    }

    public ArrayList<String> getDinnerFoodRandom() {
        String strQuery = "select * from food" +
                " where time='cena'" +
                " order by RANDOM() limit 6";

        c = db.rawQuery(strQuery, new String[]{});
        ArrayList<String> result = new ArrayList<>();

        ContentValues dataInsert = new ContentValues();
        int i = 0;

        while(c.moveToNext()){
            result.add(c.getString(1));
            dataInsert.put("day", DAYS_OF_WEEK[i]);
            dataInsert.put("food", c.getString(1));
            dataInsert.put("time", "cena");
            db.insert("menu", null, dataInsert);
            i++;
        }

        return result;
    }

    public void getIngredientsRelated(ArrayList<String> food){
        String query = "select * from ingredients where id in " +
                "(select id_ingredient from food_to_ingredients where id_food in " +
                "(select id from food where name in (";

        StringBuilder builder = new StringBuilder();
        for(String s : food) {
            builder.append("'"+s+"',");
        }
        String temp = builder.toString();
        String strQuery = query + temp.substring(0, temp.length()-1) + ")))";

        c = db.rawQuery(strQuery, new String[]{});
        ContentValues dataInsert = new ContentValues();
        db.delete("shop_list", null, null);

        while(c.moveToNext()){
            dataInsert.put("name", c.getString(1));
            dataInsert.put("place", c.getString(2));
            db.insert("shop_list", null, dataInsert);
        }
    }

    public void setDataToFood(String dataRaw) {
        String [] data = dataRaw.split(",");
        ContentValues dataInsert = new ContentValues();
        String[] columns = {"name", "time", "type"};

        for(int i=0; i<data.length;i++){
            dataInsert.put(columns[i],data[i]);
        }
        db.insert("food", null, dataInsert);
    }

    public void setDataToIngr(String dataRaw) {
        String [] data = dataRaw.split(",");
        ContentValues dataInsert = new ContentValues();
        String[] columns = {"name", "place"};

        for(int i=0; i<data.length;i++){
            dataInsert.put(columns[i],data[i]);
        }
        db.insert("ingredients", null, dataInsert);
    }

    public String[] getIngredients() {
        c = db.rawQuery("select name from ingredients order by name", new String[]{});
        String[] ingredients = new String[c.getCount()];
        int i = 0;
        while(c.moveToNext()){
            ingredients[i] = c.getString(0).substring(0, 1).toUpperCase() + c.getString(0).substring(1);
            i++;
        }

        return  ingredients;
    }

    public void setFoodToIngredients(String food, String ingreList){
        ContentValues foodToIngr = new ContentValues();
        c = db.rawQuery("select * from food", new String[]{});
        c.moveToLast();
        int id_food = c.getInt(0);

        c = db.rawQuery("select * from ingredients where name in " + ingreList, new String[]{});
        while(c.moveToNext()){
            foodToIngr.put("id_food", id_food);
            foodToIngr.put("id_ingredient", c.getInt(0));
            db.insert("food_to_ingredients", null, foodToIngr);
        }

    }

    public void setIngredientToShop(String dataRaw){
        ContentValues ingrToShop = new ContentValues();
        c = db.rawQuery("select * from ingredients where name in " + dataRaw , new String[]{});

        while(c.moveToNext()){
            ingrToShop.put("name", c.getString(1));
            ingrToShop.put("place", c.getString(2));
            db.insert("shop_list", null, ingrToShop);
        }

    }

    public void deleteTable(String tableName){
        db.delete(tableName, null, null);
    }

    public void setValueToShopList(String filter, String value){
        String query = "update shop_list set check_buy = '" + value +
                "' where name = '" + filter + "'";

        db.execSQL(query, new String[]{});
    }
}
