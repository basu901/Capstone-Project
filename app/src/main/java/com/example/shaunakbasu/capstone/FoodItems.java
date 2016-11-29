package com.example.shaunakbasu.capstone;

/**
 * Created by shaunak basu on 24-11-2016.
 */
public class FoodItems {
    String food_item,brand_name,calorie_count;

    public FoodItems(String food_item,String brand_name,String calorie_count){
        this.food_item=food_item;
        this.brand_name=brand_name;
        this.calorie_count=calorie_count;
    }

    public String getFood_item() {
        return food_item;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setFood_item(String food_item) {
        this.food_item = food_item;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public void setCalorie_count(String calorie_count) {
        this.calorie_count = calorie_count;
    }

    public String getCalorie_count() {
        return calorie_count;
    }
}
