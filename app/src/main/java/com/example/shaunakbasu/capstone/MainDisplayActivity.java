package com.example.shaunakbasu.capstone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shaunakbasu.capstone.adapters.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaunak basu on 13-11-2016.
 */
public class MainDisplayActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    SharedPreferences user_preferences;
    ActionBarDrawerToggle drawerToggle;
    PagerAdapter adapter;
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         user_preferences= getSharedPreferences(getResources().getString(R.string.user_shared_pref), Context.MODE_PRIVATE);
        boolean has_logged = user_preferences.getBoolean(getResources().getString(R.string.logged_in), false);

            if (has_logged) {
             int tab_number=getIntent().getIntExtra("tab_number",0);

            setContentView(R.layout.activity_main);
            toolbar = (Toolbar) findViewById(R.id.main_toolbar);

            setSupportActionBar(toolbar);

            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

            drawerToggle=setupDrawerToggle();
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            View header_layout=navigationView.getHeaderView(0);
            TextView user_name=(TextView)header_layout.findViewById(R.id.user_name_text);
            String name=user_preferences.getString(getResources().getString(R.string.user_first_name),"")
                    +" "+user_preferences.getString(getResources().getString(R.string.user_last_name),"");
            user_name.setText(name);
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            selectDrawerItem(menuItem);
                            return true;
                        }
                    });


            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

            tabLayout.addTab(tabLayout.newTab().setText("Run"));
            tabLayout.addTab(tabLayout.newTab().setText("Stopwatch"));
            tabLayout.addTab(tabLayout.newTab().setText("Routine"));


            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            adapter = new PagerAdapter
                    (getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);
            viewPager.setId(R.id.view_pager_id);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

                viewPager.setCurrentItem(tab_number);
        }
        else{
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }

        }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        RunFragment runFragment = adapter.getRunFragment();

        if(runFragment!=null){
            if (requestCode == RunFragment.REQUEST_LOCATION) {
                runFragment.onActivityResult(requestCode, resultCode, data);
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {

        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    public void selectDrawerItem(MenuItem menuItem){

        TextView result,label;
        String message,hint,id;

        switch(menuItem.getItemId()) {
            case R.id.nav_weight:
                View view_weight = MenuItemCompat.getActionView(menuItem);
                label = (TextView) view_weight.findViewById(R.id.nav_text_label_menu);
                result = (TextView) view_weight.findViewById(R.id.nav_text_edit_menu);
                message = getResources().getString(R.string.set)+" " +label.getText().toString();
                id=getResources().getString(R.string.user_weight);
                hint = getResources().getString(R.string.weight_hint);
                setValueMenu(result, message, hint,id);
                break;

            case R.id.nav_height:
                View view_height = MenuItemCompat.getActionView(menuItem);
                label = (TextView) view_height.findViewById(R.id.nav_text_label_menu);
                result = (TextView) view_height.findViewById(R.id.nav_text_edit_menu);
                message = getResources().getString(R.string.set) +" "+ label.getText().toString();
                id=getResources().getString(R.string.user_height);
                hint = getResources().getString(R.string.height_hint);
                setValueMenu(result, message, hint,id);
                break;

            case R.id.nav_dob:
                View view_dob = MenuItemCompat.getActionView(menuItem);
                label = (TextView) view_dob.findViewById(R.id.nav_text_label_menu);
                result = (TextView) view_dob.findViewById(R.id.nav_text_edit_menu);
                message = getResources().getString(R.string.set) +" " + label.getText().toString();
                id=getResources().getString(R.string.user_dob);
                hint = getResources().getString(R.string.date_format);
                setValueMenu(result, message, hint,id);
                break;

            case R.id.nav_bmi:
                ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                        .findViewById(android.R.id.content)).getChildAt(0);
                Snackbar.make(viewGroup,getResources().getString(R.string.bmi_auto),Snackbar.LENGTH_LONG).show();
                break;
            case R.id.nav_calorie:
                Intent intent_calorie=new Intent(getApplicationContext(),CalorieActivity.class);
                startActivity(intent_calorie);
                break;

            case R.id.nav_graphs:
                Intent intent_graph=new Intent(getApplicationContext(),GraphActivity.class);
                startActivity(intent_graph);

        }

    }




    public void setValueMenu(final TextView result, final String message, final String hint, final String res){
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.prompt_dialogue, null);

        TextView message_text=(TextView)promptsView.findViewById(R.id.prompt_message);
        message_text.setText(message);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainDisplayActivity.this,R.style.MyAlertDialogStyle);

        alertDialogBuilder.setView(promptsView);

        final EditText user_input = (EditText) promptsView
                .findViewById(R.id.prompt_message_edit_text);

        user_input.setHint(hint);

        if(hint.equals(getResources().getString(R.string.weight_hint))||hint.equals(getResources().getString(R.string.height_hint)))
            user_input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
        else
            user_input.setInputType(InputType.TYPE_CLASS_DATETIME);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                boolean checker=true;

                                String input=user_input.getText().toString();

                                if(input.equals("")){
                                    ViewGroup viewGroup = (ViewGroup) ((ViewGroup) MainDisplayActivity.this
                                            .findViewById(android.R.id.content)).getChildAt(0);
                                    Snackbar.make(viewGroup,getResources().getString(R.string.invalid),Snackbar.LENGTH_LONG).show();
                                }
                                else{
                                    if(hint.equals(getResources().getString(R.string.date_format))){
                                        checker= Utility.dateChecker(input);
                                    }
                                    if(checker){

                                        String modified;

                                        if(!(res.equals(getResources().getString(R.string.user_dob)))){
                                            modified=Utility.twoPlaceConverter(user_input.getText().toString());
                                        }
                                        else{
                                            modified=user_input.getText().toString();
                                        }
                                        SharedPreferences.Editor user_edit= user_preferences.edit();
                                        user_edit.putString(res,modified);
                                        user_edit.apply();
                                        setBMI();
                                        result.setText(modified);
                                    }
                                    else{
                                        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) MainDisplayActivity.this
                                                .findViewById(android.R.id.content)).getChildAt(0);
                                        Snackbar.make(viewGroup,getResources().getString(R.string.invalid_format),Snackbar.LENGTH_LONG).show();
                                    }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        Menu nav_menu=navigationView.getMenu();

        String weight,height,dob;

        weight=user_preferences.getString(getResources().getString(R.string.user_weight),"");
        height=user_preferences.getString(getResources().getString(R.string.user_height),"");
        dob=user_preferences.getString(getResources().getString(R.string.user_dob),"");

        MenuItem menu_weight=nav_menu.findItem(R.id.nav_weight);
        View view_weight= MenuItemCompat.getActionView(menu_weight);
        ImageView weight_image=(ImageView)view_weight.findViewById(R.id.nav_image_menu);
        weight_image.setImageResource(R.drawable.ic_weight_18dp);
        TextView weight_label=(TextView)view_weight.findViewById(R.id.nav_text_label_menu);
        weight_label.setText(getResources().getString(R.string.weight_small));
        TextView weight_value=(TextView)view_weight.findViewById(R.id.nav_text_edit_menu);
        weight_value.setText(weight);

        MenuItem menu_height=nav_menu.findItem(R.id.nav_height);
        View view_height= MenuItemCompat.getActionView(menu_height);
        ImageView height_image=(ImageView)view_height.findViewById(R.id.nav_image_menu);
        height_image.setImageResource(R.drawable.ic_height_18dp);
        TextView height_label=(TextView)view_height.findViewById(R.id.nav_text_label_menu);
        height_label.setText(getResources().getString(R.string.height_small));
        TextView height_value=(TextView)view_height.findViewById(R.id.nav_text_edit_menu);
        height_value.setText(height);

        MenuItem menu_dob=nav_menu.findItem(R.id.nav_dob);
        View view_dob= MenuItemCompat.getActionView(menu_dob);
        ImageView dob_image=(ImageView)view_dob.findViewById(R.id.nav_image_menu);
        dob_image.setImageResource(R.drawable.ic_date_of_birth_18dp);
        TextView dob_label=(TextView)view_dob.findViewById(R.id.nav_text_label_menu);
        dob_label.setText(getResources().getString(R.string.dob_small));
        TextView dob_value=(TextView)view_dob.findViewById(R.id.nav_text_edit_menu);
        dob_value.setText(dob);

        setBMI();


        return true;
    }

    public void setBMI(){
        Menu nav_menu=navigationView.getMenu();
        String bmi_text,bmi_details;
        float bmi;

        String weight=user_preferences.getString(getResources().getString(R.string.user_weight),"");
        String height=user_preferences.getString(getResources().getString(R.string.user_height),"");

        bmi=((Float.parseFloat(weight)/Float.parseFloat(height))/Float.parseFloat(height));
        bmi_details=Utility.getBMIDetails(bmi);
        bmi_text=Utility.twoPlaceConverter(Float.toString(bmi));

        MenuItem menu_bmi=nav_menu.findItem(R.id.nav_bmi);
        View view_bmi= MenuItemCompat.getActionView(menu_bmi);
        ImageView bmi_image=(ImageView)view_bmi.findViewById(R.id.nav_image_menu);
        bmi_image.setImageResource(R.drawable.ic_bmi_18dp);
        TextView bmi_label=(TextView)view_bmi.findViewById(R.id.nav_text_label_menu);
        bmi_label.setText(getResources().getString(R.string.bmi_small));
        TextView bmi_value=(TextView)view_bmi.findViewById(R.id.nav_text_edit_menu);
        String bmi_all=bmi_text+bmi_details;
        bmi_value.setText(bmi_all);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);

    }

}


