package com.example.menuchapp.Models;

public class ShopModel {

    private String ingredient;
    private int number;
    private String check;

    public ShopModel(String ingredient, int number, String check) {
        this.ingredient = ingredient;
        this.number = number;
        this.check = check;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }
}
