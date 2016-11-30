package com.example.shaunakbasu.capstone.adapters;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.shaunakbasu.capstone.CalorieActivity;
import com.example.shaunakbasu.capstone.FoodItems;
import com.example.shaunakbasu.capstone.R;

import java.util.ArrayList;

/**
 * Created by shaunak basu on 24-11-2016.
 */
public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.MyViewHolder> {

    private ArrayList<FoodItems> food_list;
    public CalorieAdderListener calorieAdderListener;

    public interface CalorieAdderListener {

        void add_calOnClick(View v, String str);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView item, brand, cal;
        ImageButton add_cal;

        public MyViewHolder(View view) {
            super(view);
            item = (TextView) view.findViewById(R.id.calorie_item_text_desc);
            brand = (TextView) view.findViewById(R.id.calorie_item_brand_text_desc);
            cal = (TextView) view.findViewById(R.id.calorie_item_cal_text);
            add_cal = (ImageButton) view.findViewById(R.id.calorie_image_add_button);

        }
    }

    public FoodItemAdapter(ArrayList<FoodItems> food_list, CalorieAdderListener calorieAdderListener) {
        this.food_list = food_list;
        this.calorieAdderListener = calorieAdderListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calorie_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        FoodItems foodItems = food_list.get(position);
        holder.item.setText(foodItems.getFood_item());
        holder.brand.setText(foodItems.getBrand_name());
        holder.cal.setText(foodItems.getCalorie_count());
        holder.add_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String calories = holder.cal.getText().toString();

                calorieAdderListener.add_calOnClick(view, calories);
            }
        });

    }

    @Override
    public int getItemCount() {
        return food_list.size();
    }

}
