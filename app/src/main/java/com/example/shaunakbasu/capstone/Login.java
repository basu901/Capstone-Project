package com.example.shaunakbasu.capstone;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


public class Login extends AppCompatActivity {

    EditText first_name_text,last_name_text,dob_text,weight_text,height_text,gender_text;
    String first_name,last_name,dob,weight,height,gender;
    Button begin;
    boolean checker;
    StringBuffer faulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        faulty=new StringBuffer();

        first_name_text=(EditText)findViewById(R.id.editText_first_name);
        first_name_text.setNextFocusForwardId(R.id.editText_last_name);


        last_name_text=(EditText)findViewById(R.id.editText_last_name);
        last_name_text.setNextFocusForwardId(R.id.editText_dob);

        dob_text=(EditText)findViewById(R.id.editText_dob);
        dob_text.setNextFocusForwardId(R.id.editText_weight);

        weight_text=(EditText)findViewById(R.id.editText_weight);
        weight_text.setNextFocusForwardId(R.id.editText_height);

        height_text=(EditText)findViewById(R.id.editText_height);
        height_text.setNextFocusForwardId(R.id.editText_gender);

        gender_text=(EditText)findViewById(R.id.editText_gender);

        begin=(Button)findViewById(R.id.login_button);
        begin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                boolean result=validate();

                if(result){
                    SharedPreferences user_preferences = getSharedPreferences(getResources().getString(R.string.user_shared_pref), Context.MODE_PRIVATE);
                    SharedPreferences.Editor user_edit= user_preferences.edit();

                    String f_weight=Utility.twoPlaceConverter(weight);
                    String f_height=Utility.twoPlaceConverter(height);

                    user_edit.putString(getResources().getString(R.string.user_first_name),first_name);
                    user_edit.putString(getResources().getString(R.string.user_last_name),last_name);
                    user_edit.putString(getResources().getString(R.string.user_dob),dob);
                    user_edit.putString(getResources().getString(R.string.user_weight),f_weight);
                    user_edit.putString(getResources().getString(R.string.user_height),f_height);
                    user_edit.putString(getResources().getString(R.string.user_gender),gender);
                    user_edit.putBoolean(getResources().getString(R.string.logged_in),true);
                    user_edit.putInt(getResources().getString(R.string.selected_item),0);

                    user_edit.apply();

                    Intent intent=new Intent(getApplicationContext(),MainDisplayActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }



            }
        });

    }


    public boolean validate(){
        checker=true;
        first_name=first_name_text.getText().toString();
        last_name=last_name_text.getText().toString();
        dob=dob_text.getText().toString();
        weight=weight_text.getText().toString();
        height=height_text.getText().toString();
        gender=gender_text.getText().toString();

        if(!Utility.hasOnlyAlphabets(first_name)){
            faulty.append(getResources().getString(R.string.first_name_caps)+", ");
            checker=false;
        }

        if(!Utility.hasOnlyAlphabets(last_name)){
            faulty.append(getResources().getString(R.string.last_name_caps)+", ");
            checker=false;
        }

        if(!Utility.hasOnlyAlphabets(gender)){
            faulty.append(getResources().getString(R.string.gender_caps)+", ");
            checker=false;
        }

        boolean date_checker=Utility.dateChecker(dob);
        if(!date_checker){
            checker=false;
            faulty.append(getResources().getString(R.string.dob_caps)+", ");
        }


        try{
            float w_checker=Float.parseFloat(weight);
        }catch (Exception e){
            checker=false;
            faulty.append(getResources().getString(R.string.weight_caps)+", ");
        }

        try{
            float h_checker=Float.parseFloat(height);
        }catch (Exception e){
            checker=false;
            faulty.append(getResources().getString(R.string.height_caps)+", ");
        }


        if(!checker){
            faulty.delete(faulty.length()-2,faulty.length());
            Snackbar.make(getWindow().getDecorView().getRootView(),faulty.toString()+" "+getResources().getString(R.string.not_valid),Snackbar.LENGTH_LONG).show();
            faulty.delete(0,faulty.length());

        }

        return checker;
    }

}

