package com.example.shaunakbasu.capstone.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.shaunakbasu.capstone.BuildConfig;
import com.example.shaunakbasu.capstone.CalorieActivity;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shaunak basu on 24-11-2016.
 */
public class FoodItemServices extends IntentService {

    private static String LOG=FoodItemServices.class.getSimpleName();
    public static String REQUEST_ITEM="food";
    public static String FOOD_RESULT="food_result";

    String food;
    final String base_url="https://api.nutritionix.com/v1_1/search/";
    final String RESULTS_PARAM="results";
    final String FIELDS_PARAM="fields";
    final String APP_ID_PARAM="appId";
    final String APP_KEY_PARAM="appKey";

    final String results="0:20";
    final String fields="item_name,brand_name,item_id,nf_calories";
    final String appId= BuildConfig.APP_ID;
    final String appKey= BuildConfig.APP_KEY;


    public FoodItemServices(){
        super(LOG);
    }

    @Override
    protected void onHandleIntent(Intent intent){

        food=intent.getStringExtra(REQUEST_ITEM);

        String base_food=base_url+food+"?";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String result_food_json=null;



        Uri builtUri = Uri.parse(base_food).buildUpon()
                .appendQueryParameter(RESULTS_PARAM,results)
                .appendQueryParameter(FIELDS_PARAM, fields)
                .appendQueryParameter(APP_ID_PARAM, appId)
                .appendQueryParameter(APP_KEY_PARAM, appKey)
                .build();

        if(isNetworkAvailable()){
            try{

                URL url = new URL(builtUri.toString());

                // Creating request to nutritionix, and opening the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

           /* if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }*/
                result_food_json = buffer.toString();
                Log.v(LOG,result_food_json);
                //getWeatherDataFromJson(forecastJsonStr, locationQuery);
            } catch (IOException e) {
                Log.e(LOG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG, "Error closing stream", e);
                    }
                }
            }
        }
        else{
            result_food_json="No network";
        }




        Intent broadcast_food=new Intent();
        broadcast_food.setAction(CalorieActivity.FoodReceiver.RESPONSE);
        broadcast_food.addCategory(Intent.CATEGORY_DEFAULT);
        broadcast_food.putExtra(FOOD_RESULT,result_food_json);
        sendBroadcast(broadcast_food);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
