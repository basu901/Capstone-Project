package com.example.shaunakbasu.capstone;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.shaunakbasu.capstone.data.CalorieBurntColumns;
import com.example.shaunakbasu.capstone.data.CalorieBurntProvider;
import com.example.shaunakbasu.capstone.data.CalorieIntakeColumns;
import com.example.shaunakbasu.capstone.data.CalorieIntakeProvider;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by shaunak basu on 25-11-2016.
 */
public class GraphActivity extends AppCompatActivity {

    Calendar calendar;
    int day_max, min, month, year;
    ArrayList<BarEntry> consume_value_first, burn_value_first, consume_value_second, burn_value_second;
    BarChart barChart;
    Spinner spinner;
    int selector;
    String position_string;
    BarDataSet cal_barDataSet1, cal_barDataSet2, burn_barDataSet1, burn_barDataSet2;
    SharedPreferences user_preferences;
    SharedPreferences.Editor user_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        day_max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        user_preferences = getSharedPreferences(getResources().getString(R.string.user_shared_pref), Context.MODE_PRIVATE);
        user_edit = user_preferences.edit();

        selector = user_preferences.getInt(getResources().getString(R.string.selected_item), 0);
        Log.v("GRAPH", Integer.toString(selector));


        Toolbar toolbar = (Toolbar) findViewById(R.id.graph_toolbar);
        toolbar.setTitle(getResources().getString(R.string.calorie_metrics));

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        barChart = (BarChart) findViewById(R.id.bar_chart);
        spinner = (Spinner) findViewById(R.id.graph_spinner);


        consume_value_first = new ArrayList<>();
        burn_value_first = new ArrayList<>();

        consume_value_second = new ArrayList<>();
        burn_value_second = new ArrayList<>();


        if (day_max % 2 == 0) {

            min = day_max / 2;
        } else {
            min = (day_max + 1) / 2;
        }

        String slash = getResources().getString(R.string.slash);
        String dash = getResources().getString(R.string.dash);

        List<String> spinner_items = new ArrayList<>();

        position_string = Integer.toString(month) + slash + getResources().getString(R.string.date_first) + dash + Integer.toString(month) + slash + Integer.toString(min);

        if (selector == 0) {
            spinner_items.add(Integer.toString(month) + slash + getResources().getString(R.string.date_first) + dash + Integer.toString(month) + slash + Integer.toString(min));
            spinner_items.add(Integer.toString(month) + slash + Integer.toString(min + 1) + dash + Integer.toString(month) + slash + Integer.toString(day_max));

        } else {

            spinner_items.add(Integer.toString(month) + slash + Integer.toString(min + 1) + dash + Integer.toString(month) + slash + Integer.toString(day_max));
            spinner_items.add(Integer.toString(month) + slash + getResources().getString(R.string.date_first) + dash + Integer.toString(month) + slash + Integer.toString(min));

        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_format, spinner_items);
        dataAdapter.setDropDownViewResource(R.layout.spinner_format);
        spinner.setAdapter(dataAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        Cursor calorie = null;
        for (int i = 1; i <= min; i++) {
            Float cal_per_day;
            BarEntry cal_bar;
            calorie = getContentResolver().query(CalorieIntakeProvider.Calorie_Intake.CONTENT_URI,
                    new String[]{CalorieIntakeColumns.AMOUNT},
                    CalorieIntakeColumns.DATE + " =? AND " +
                            CalorieIntakeColumns.MONTH + " =? AND " +
                            CalorieIntakeColumns.YEAR + " =?",
                    new String[]{Integer.toString(i), Integer.toString(month), Integer.toString(year)}, null);
            if (calorie.getCount() > 0) {
                calorie.moveToFirst();
                cal_per_day = Float.parseFloat(calorie.getString(calorie.getColumnIndex(CalorieIntakeColumns.AMOUNT)));

            } else {
                cal_per_day = 0f;
            }
            cal_bar = new BarEntry((Float.parseFloat(Integer.toString(i))), cal_per_day);
            consume_value_first.add(cal_bar);

        }


        for (int i = 1; i <= min; i++) {
            Float cal_per_day;
            BarEntry cal_bar;
            calorie = getContentResolver().query(CalorieBurntProvider.Calorie_Burnt.CONTENT_URI,
                    new String[]{CalorieBurntColumns.AMOUNT},
                    CalorieBurntColumns.DATE + " =? AND " +
                            CalorieBurntColumns.MONTH + " =? AND " +
                            CalorieBurntColumns.YEAR + " =?",
                    new String[]{Integer.toString(i), Integer.toString(month), Integer.toString(year)}, null);
            if (calorie.getCount() > 0) {
                calorie.moveToFirst();
                cal_per_day = Float.parseFloat(calorie.getString(calorie.getColumnIndex(CalorieBurntColumns.AMOUNT)));

            } else {
                cal_per_day = 0f;
            }
            cal_bar = new BarEntry((Float.parseFloat(Integer.toString(i))), cal_per_day);
            burn_value_first.add(cal_bar);

        }

        for (int i = min + 1; i <= day_max; i++) {
            Float cal_per_day;
            BarEntry cal_bar;
            calorie = getContentResolver().query(CalorieIntakeProvider.Calorie_Intake.CONTENT_URI,
                    new String[]{CalorieIntakeColumns.AMOUNT},
                    CalorieIntakeColumns.DATE + " =? AND " +
                            CalorieIntakeColumns.MONTH + " =? AND " +
                            CalorieIntakeColumns.YEAR + " =?",
                    new String[]{Integer.toString(i), Integer.toString(month), Integer.toString(year)}, null);
            if (calorie.getCount() > 0) {
                calorie.moveToFirst();
                cal_per_day = Float.parseFloat(calorie.getString(calorie.getColumnIndex(CalorieIntakeColumns.AMOUNT)));

            } else {
                cal_per_day = 0f;
            }
            cal_bar = new BarEntry((Float.parseFloat(Integer.toString(i))), cal_per_day);
            consume_value_second.add(cal_bar);

        }


        for (int i = min + 1; i <= day_max; i++) {
            Float cal_per_day;
            BarEntry cal_bar;
            calorie = getContentResolver().query(CalorieBurntProvider.Calorie_Burnt.CONTENT_URI,
                    new String[]{CalorieBurntColumns.AMOUNT},
                    CalorieBurntColumns.DATE + " =? AND " +
                            CalorieBurntColumns.MONTH + " =? AND " +
                            CalorieBurntColumns.YEAR + " =?",
                    new String[]{Integer.toString(i), Integer.toString(month), Integer.toString(year)}, null);
            if (calorie.getCount() > 0) {
                calorie.moveToFirst();
                cal_per_day = Float.parseFloat(calorie.getString(calorie.getColumnIndex(CalorieBurntColumns.AMOUNT)));

            } else {
                cal_per_day = 0f;
            }
            cal_bar = new BarEntry((Float.parseFloat(Integer.toString(i))), cal_per_day);
            burn_value_second.add(cal_bar);

        }

        if (calorie != null)
            calorie.close();


        cal_barDataSet1 = new BarDataSet(consume_value_first, getResources().getString(R.string.graph_calories_label));
        cal_barDataSet1.setColor(getResources().getColor(R.color.calorie_intake));

        burn_barDataSet1 = new BarDataSet(burn_value_first, getResources().getString(R.string.graph_burn_label));
        burn_barDataSet1.setColors(getResources().getColor(R.color.colorAccent));

        cal_barDataSet2 = new BarDataSet(consume_value_second, getResources().getString(R.string.graph_calories_label));
        cal_barDataSet2.setColor(getResources().getColor(R.color.calorie_intake));

        burn_barDataSet2 = new BarDataSet(burn_value_second, getResources().getString(R.string.graph_burn_label));
        burn_barDataSet2.setColors(getResources().getColor(R.color.colorAccent));

        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }


    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String time_line = parent.getItemAtPosition(pos).toString();
            populateGraph(time_line);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    public void populateGraph(String str) {

        BarData barData;

        if (str.equals(position_string)) {

            barData = new BarData(cal_barDataSet1, burn_barDataSet1);
            selector = 0;

        } else {

            barData = new BarData(cal_barDataSet2, burn_barDataSet2);
            selector = 1;
        }

        Description desc = new Description();
        String calories = getResources().getString(R.string.calories);
        desc.setText(calories);
        barChart.setData(barData);
        barChart.setDescription(desc);
        barChart.animateXY(2000, 2000);
        barChart.invalidate();

        user_edit.putInt(getResources().getString(R.string.selected_item), selector);
        user_edit.apply();

    }


}
