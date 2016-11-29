package com.example.shaunakbasu.capstone.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.shaunakbasu.capstone.R;
import com.example.shaunakbasu.capstone.data.CalorieBurntColumns;
import com.example.shaunakbasu.capstone.data.CalorieBurntProvider;
import com.example.shaunakbasu.capstone.data.CalorieIntakeColumns;
import com.example.shaunakbasu.capstone.data.CalorieIntakeProvider;

import java.util.Calendar;

/**
 * Created by shaunak basu on 27-11-2016.
 */
public class MyWidgetProvider extends AppWidgetProvider {

    int day,month,year;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;
        String cal="0.0";
        String burn="0.0";
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DATE);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);

        Cursor cal_cursor = context.getContentResolver().query(CalorieIntakeProvider.Calorie_Intake.CONTENT_URI,
                new String[]{CalorieIntakeColumns.AMOUNT,CalorieIntakeColumns.MONTH,CalorieIntakeColumns.YEAR,CalorieIntakeColumns.DATE},
                        CalorieIntakeColumns.DATE + " =? AND " +
                        CalorieIntakeColumns.MONTH + " =? AND " +
                        CalorieIntakeColumns.YEAR + " =?",
                new String[]{Integer.toString(day), Integer.toString(month), Integer.toString(year)}, null);

        if(cal_cursor.getCount()>0) {
            cal_cursor.moveToFirst();
            do {
                String cal_month = Integer.toString(cal_cursor.getInt(cal_cursor.getColumnIndex(CalorieIntakeColumns.MONTH)));
                String cal_date = Integer.toString(cal_cursor.getInt(cal_cursor.getColumnIndex(CalorieIntakeColumns.DATE)));
                String cal_year = Integer.toString(cal_cursor.getInt(cal_cursor.getColumnIndex(CalorieIntakeColumns.YEAR)));
                cal = cal_cursor.getString(cal_cursor.getColumnIndex(CalorieIntakeColumns.AMOUNT));

                Log.v(cal + " " + cal_date, "," + cal_month + "," + cal_year);
            }while(cal_cursor.moveToNext());
        }


        Cursor burn_cursor = context.getContentResolver().query(CalorieBurntProvider.Calorie_Burnt.CONTENT_URI,
                new String[]{CalorieBurntColumns.AMOUNT},
                CalorieBurntColumns.DATE + " =? AND " +
                        CalorieBurntColumns.MONTH + " =? AND " +
                        CalorieBurntColumns.YEAR + " =?",
                new String[]{Integer.toString(day), Integer.toString(month), Integer.toString(year)}, null);

        if(burn_cursor.getCount()>0){
            burn_cursor.moveToFirst();

            burn = burn_cursor.getString(burn_cursor.getColumnIndex(CalorieBurntColumns.AMOUNT));
            Log.v("CHECK BuRN:",burn);
        }


        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];


            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout_info);

            String cal_label=cal+context.getResources().getString(R.string.cal_unit);
            String burn_label=burn+context.getResources().getString(R.string.cal_unit);

            remoteViews.setTextViewText(R.id.widget_calorie_consumed, cal_label);
            remoteViews.setTextViewText(R.id.widget_calorie_burnt, burn_label);


            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
