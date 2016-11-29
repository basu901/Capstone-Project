package com.example.shaunakbasu.capstone;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.shaunakbasu.capstone.adapters.FoodItemAdapter;
import com.example.shaunakbasu.capstone.data.CalorieBurntColumns;
import com.example.shaunakbasu.capstone.data.CalorieBurntProvider;
import com.example.shaunakbasu.capstone.data.CalorieIntakeColumns;
import com.example.shaunakbasu.capstone.data.CalorieIntakeProvider;
import com.example.shaunakbasu.capstone.services.FoodItemServices;
import com.example.shaunakbasu.capstone.widget.MyWidgetProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by shaunak basu on 23-11-2016.
 */
public class CalorieActivity extends AppCompatActivity {

    TextView today_calorie;
    Button add_calorie;
    Calendar calendar;
    int day,month,year;
    ArrayList<FoodItems> food_container=new ArrayList<>();
    RecyclerView recyclerView;
    FoodItemAdapter foodItemAdapter;
    FoodReceiver receiver;
    SmoothProgressBar progressBar;
    View view_root;

    public static String LOG=CalorieActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie);
        view_root=findViewById(R.id.root_view_calorie);
        today_calorie=(TextView)findViewById(R.id.calorie_today_value);
        Toolbar toolbar=(Toolbar)findViewById(R.id.calorie_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.calorie_title));

        TextView api=(TextView)findViewById(R.id.nutrition_api);
        api.setClickable(true);
        api.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='https://www.nutritionix.com/business/api'> Nutritionix API </a>";
        api.setText(Html.fromHtml(text));

        ImageButton calorie_burner_button=(ImageButton)toolbar.findViewById(R.id.calorie_burn_add);
        calorie_burner_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCalorieBurnt();
            }
        });
        progressBar=(SmoothProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar=Calendar.getInstance();
        day=calendar.get(calendar.DATE);
        month=calendar.get(calendar.MONTH)+1;
        year=calendar.get(calendar.YEAR);

        view_root = (ViewGroup) ((ViewGroup) CalorieActivity.this
                .findViewById(android.R.id.content)).getChildAt(0);

        IntentFilter filter = new IntentFilter(FoodReceiver.RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new FoodReceiver();
        registerReceiver(receiver, filter);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_calorie);

        foodItemAdapter = new FoodItemAdapter(food_container, new FoodItemAdapter.CalorieAdderListener() {
            @Override
            public void add_calOnClick(View v, String str) {
                addFood(str);

            }
        });

        getTodayCalories();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new FoodDivider(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(foodItemAdapter);



        add_calorie=(Button)findViewById(R.id.calorie_add_button);
        add_calorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG,"IN ONCLICK!!!");
                    foodItem();
            }
        });


    }

    public void foodItem(){
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.prompt_dialogue, null);

        TextView message_text=(TextView)promptsView.findViewById(R.id.prompt_message);

        message_text.setText(getString(R.string.calorie_food_input));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CalorieActivity.this,R.style.MyAlertDialogStyle);

        alertDialogBuilder.setView(promptsView);

        final EditText user_input = (EditText) promptsView
                .findViewById(R.id.prompt_message_edit_text);

        user_input.setHint(getString(R.string.food_hint));

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String food;
                                ArrayList<Integer> input=Utility.foodItem(user_input.getText().toString());

                                if(input.size()>0){
                                    if(input.size()==1){
                                        food=user_input.getText().toString();
                                    }
                                    else{
                                        input.remove(input.size()-1);
                                        food=Utility.noSpacesFood(user_input.getText().toString(),input);
                                    }

                                    Intent food_service=new Intent(CalorieActivity.this, FoodItemServices.class);
                                    food_service.putExtra(FoodItemServices.REQUEST_ITEM,food);
                                    startService(food_service);
                                    progressBar.setVisibility(ProgressBar.VISIBLE);
                                }
                                else{

                                    Snackbar.make(view_root,getResources().getString(R.string.invalid_food),Snackbar.LENGTH_LONG).show();
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public class FoodReceiver extends BroadcastReceiver {

        public static final String RESPONSE = Constants.PACKAGE_NAME+"PROCESS";

        @Override
        public void onReceive(Context context, Intent intent) {

            String responseString = intent.getStringExtra(FoodItemServices.FOOD_RESULT);
            if(responseString.equals("No network")){
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Snackbar.make(view_root,getResources().getString(R.string.no_network),Snackbar.LENGTH_LONG).show();

            }
            else{
                try{
                    getFoodInfoFromJSON(responseString);
                }
                catch (Exception e){
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    Snackbar.make(view_root,getResources().getString(R.string.food_not_found),Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }


    public void getFoodInfoFromJSON(String input) throws JSONException{

        String HITS="hits";
        String FIELDS="fields";
        String ITEM="item_name";
        String BRAND="brand_name";
        String CAL="nf_calories";

       food_container.clear();

        JSONObject full= new JSONObject(input);
        JSONArray hits=full.getJSONArray(HITS);
        String total_hits=full.getString("total_hits");
       if(total_hits.equals("0")){
           Snackbar.make(view_root,getResources().getString(R.string.food_not_found),Snackbar.LENGTH_LONG).show();
        }

        else{
            for(int i=0;i<hits.length();i++){
                JSONObject fields=hits.getJSONObject(i).getJSONObject(FIELDS);
                String item=fields.getString(ITEM);
                String brand=fields.getString(BRAND);
                String cal=fields.getString(CAL);
                FoodItems foodItems=new FoodItems(item,brand,cal);

                food_container.add(foodItems);
            }


            foodItemAdapter.notifyDataSetChanged();
       }

        progressBar.setVisibility(ProgressBar.INVISIBLE);

    }

    public void addFood(final String cal){
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.confirmation_dialogue, null);

        TextView message_text=(TextView)promptsView.findViewById(R.id.calorie_confirmation);

        message_text.setText(getString(R.string.calorie_add_confirmation));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CalorieActivity.this,R.style.MyAlertDialogStyle);

        alertDialogBuilder.setView(promptsView);


        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                addCaloriesToDatabase(cal);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }



    public void addCaloriesToDatabase(String cal_value){
        String current_cal=getCurrentData(CalorieIntakeProvider.Calorie_Intake.CONTENT_URI,
                new String[]{CalorieIntakeColumns.AMOUNT},
                CalorieIntakeColumns.DATE+" =? AND "+
                        CalorieIntakeColumns.MONTH+" =? AND "+
                        CalorieIntakeColumns.YEAR+" =? ",
                new String[]{Integer.toString(day),Integer.toString(month),Integer.toString(year)},
                CalorieIntakeColumns.AMOUNT);

        if(current_cal!=null){

            String cal_final=Utility.twoPlaceConverter(Float.toString((Float.parseFloat(current_cal)+Float.parseFloat(cal_value))));
            ContentValues values=new ContentValues();
            values.put(CalorieIntakeColumns.AMOUNT,cal_final);
            values.put(CalorieIntakeColumns.DATE,day);
            values.put(CalorieIntakeColumns.MONTH,month);
            values.put(CalorieIntakeColumns.YEAR,year);

            int num=getContentResolver().update(CalorieIntakeProvider.Calorie_Intake.CONTENT_URI,
                    values,CalorieIntakeColumns.DATE+" =? AND "+
                            CalorieIntakeColumns.MONTH+" =? AND "+
                            CalorieIntakeColumns.YEAR+" =? ",
                    new String[]{Integer.toString(day),Integer.toString(month),Integer.toString(year)});


            if(num>0){
                Snackbar.make(view_root,getResources().getString(R.string.calorie_logged),Snackbar.LENGTH_LONG).show();
            }
            else{
                Snackbar.make(view_root,getResources().getString(R.string.calorie_db_error),Snackbar.LENGTH_LONG).show();
            }

        }
        else
        {
            ContentValues contentValues=new ContentValues();
            Uri id;
            long confirm;
            cal_value=Utility.twoPlaceConverter(cal_value);
            contentValues.put(CalorieIntakeColumns.AMOUNT,cal_value);
            contentValues.put(CalorieIntakeColumns.DATE,day);
            contentValues.put(CalorieIntakeColumns.MONTH,month);
            contentValues.put(CalorieIntakeColumns.YEAR,year);

            id=getContentResolver().insert(CalorieIntakeProvider.Calorie_Intake.CONTENT_URI,contentValues);
            confirm= ContentUris.parseId(id);
            if(confirm!=-1){
                Snackbar.make(view_root,getResources().getString(R.string.calorie_logged),Snackbar.LENGTH_SHORT).show();
            }
            else
                Snackbar.make(view_root,getResources().getString(R.string.calorie_db_error),Snackbar.LENGTH_SHORT).show();

        }

        Intent intent = new Intent(getApplicationContext(),MyWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MyWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);

       getTodayCalories();

    }

    public void addCalorieBurnt(){

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.prompt_calorie_burn, null);

        TextView message_text=(TextView)promptsView.findViewById(R.id.prompt_calorie_burn_message);

        message_text.setText(getString(R.string.calorie_burner_add_text));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CalorieActivity.this,R.style.MyAlertDialogStyle);

        alertDialogBuilder.setView(promptsView);

        final EditText user_input_heart_rate = (EditText) promptsView
                .findViewById(R.id.heart_rate_edit_text);
        user_input_heart_rate.setNextFocusForwardId(R.id.workout_time_edit_text);

        final EditText user_input_workout_time=(EditText)promptsView.findViewById(R.id.workout_time_edit_text);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Double cal=0.0;
                                try{
                                    cal=caloriesBurnt(Long.parseLong(user_input_workout_time.getText().toString()),Double.parseDouble(user_input_heart_rate.getText().toString()));
                                }catch (Exception e){
                                    Snackbar.make(view_root,getResources().getString(R.string.invalid),Snackbar.LENGTH_LONG).show();
                                }

                                String cal_two=Utility.twoPlaceConverter(Double.toString(cal));
                                //Log.v("CAL_TWO",cal_two);
                                String cal_burnt,cal_final;
                                cal_burnt=getCurrentData(CalorieBurntProvider.Calorie_Burnt.CONTENT_URI,
                                        new String[]{CalorieBurntColumns.AMOUNT},
                                                    CalorieBurntColumns.DATE+" =? AND "+
                                                    CalorieBurntColumns.MONTH+" =? AND "+
                                                    CalorieBurntColumns.YEAR+" =? ",
                                                    new String[]{Integer.toString(day),Integer.toString(month),Integer.toString(year)},
                                                    CalorieBurntColumns.AMOUNT);
                                //Log.v("CAL_BURNT",cal_burnt);
                                if(cal_burnt!=null){
                                    cal_final=Utility.twoPlaceConverter(Float.toString((Float.parseFloat(cal_burnt)+Float.parseFloat(cal_two))));
                                    //Log.v("CAL_FINAL",cal_final);
                                    ContentValues values=new ContentValues();
                                    values.put(CalorieBurntColumns.AMOUNT,cal_final);
                                    values.put(CalorieBurntColumns.DATE,day);
                                    values.put(CalorieBurntColumns.MONTH,month);
                                    values.put(CalorieBurntColumns.YEAR,year);

                                    int num=getContentResolver().update(CalorieBurntProvider.Calorie_Burnt.CONTENT_URI,
                                            values,CalorieBurntColumns.DATE+" =? AND "+
                                                    CalorieBurntColumns.MONTH+" =? AND "+
                                                    CalorieBurntColumns.YEAR+" =? ",
                                            new String[]{Integer.toString(day),Integer.toString(month),Integer.toString(year)});


                                    if(num>0){
                                        Snackbar.make(view_root,cal_two+getResources().getString(R.string.calorie_title)+"("+getResources().getString(R.string.calorie_logged)+")",Snackbar.LENGTH_LONG).show();
                                    }
                                    else{
                                        Snackbar.make(view_root,getResources().getString(R.string.calorie_db_error),Snackbar.LENGTH_LONG).show();
                                    }
                                }
                                else{
                                    cal_final=cal_two;

                                    ContentValues values=new ContentValues();
                                    values.put(CalorieBurntColumns.AMOUNT,cal_final);
                                    values.put(CalorieBurntColumns.DATE,day);
                                    values.put(CalorieBurntColumns.MONTH,month);
                                    values.put(CalorieBurntColumns.YEAR,year);

                                    Uri uri=getContentResolver().insert(CalorieBurntProvider.Calorie_Burnt.CONTENT_URI, values);
                                    long num=ContentUris.parseId(uri);

                                    if(num!=-1){
                                        Snackbar.make(view_root,cal_final+getResources().getString(R.string.calorie_title)+"("+getResources().getString(R.string.calorie_logged)+")",Snackbar.LENGTH_LONG).show();
                                    }
                                    else{
                                        Snackbar.make(view_root,getResources().getString(R.string.calorie_db_error),Snackbar.LENGTH_LONG).show();
                                    }
                                }

                                Intent intent = new Intent(getApplicationContext(),MyWidgetProvider.class);
                                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                                int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MyWidgetProvider.class));
                                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
                                sendBroadcast(intent);



                            }

                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void getTodayCalories(){

        String cal_value=getCurrentData(CalorieIntakeProvider.Calorie_Intake.CONTENT_URI,
                new String[]{CalorieIntakeColumns.AMOUNT},
                CalorieIntakeColumns.DATE+" =? AND "+
                        CalorieIntakeColumns.MONTH+" =? AND "+
                        CalorieIntakeColumns.YEAR+" =? ",
                new String[]{Integer.toString(day),Integer.toString(month),Integer.toString(year)},
                CalorieIntakeColumns.AMOUNT);
        if(cal_value!=null)
            today_calorie.setText(cal_value);
        else
            today_calorie.setText("");

    }


    public String getCurrentData(Uri uri,String[] projection,String selection,String[] selection_args,String desired){
        Cursor cursor=getContentResolver().query(uri,projection,selection,selection_args,null);
        String amount;
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            amount=cursor.getString(cursor.getColumnIndex(desired));
            cursor.close();
            return amount;
        }
        else{
            cursor.close();
            return null;
        }

    }

    public Double caloriesBurnt(long mTime,Double heart_rate){

        Double calorie_burn,max_heart_rate;
        String gender,weight;
        int age;

        long time=mTime;
        long min_time=time/60;
        long sec_time=time%60;

        long f_time=min_time+(sec_time/100);


        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);

        SharedPreferences sharedPreferences;
        sharedPreferences=getSharedPreferences(getResources().getString(R.string.user_shared_pref), Context.MODE_PRIVATE);
        gender=sharedPreferences.getString(getResources().getString(R.string.user_gender),"");
        weight=sharedPreferences.getString(getResources().getString(R.string.user_weight),"");

        Double f_weight=Double.parseDouble(weight);

        String dob=sharedPreferences.getString(getResources().getString(R.string.user_dob),"");
        int dob_year=Integer.parseInt(dob.substring(dob.length()-4,dob.length()));
        age=year-dob_year;
        Log.v("YEAR DOB",Integer.toString(dob_year));

        if(heart_rate==0.0){
            max_heart_rate=208 - (0.7*age);
        }
        else{
            max_heart_rate=heart_rate;
        }



        if(gender.equalsIgnoreCase("male")){
            calorie_burn= (((age* 0.2017)-(f_weight*2.2*0.09036) + (max_heart_rate* 0.6309)- 55.0969)* (f_time / 4.184));
        }

        else if(gender.equalsIgnoreCase("female")){
            calorie_burn=(((age*.074)-(f_weight*2.2*0.05741) + (max_heart_rate*0.4472)-20.4022)*(f_time / 4.184));
        }

        else{
            calorie_burn= (((age* 0.2017)-(f_weight*2.2*0.09036) + (max_heart_rate* 0.6309)- 55.0969)* (f_time / 4.184));
        }


        return calorie_burn;
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

}
