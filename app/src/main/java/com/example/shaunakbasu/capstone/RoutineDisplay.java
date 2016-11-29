package com.example.shaunakbasu.capstone;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shaunakbasu.capstone.adapters.RoutineChildAdapter;
import com.example.shaunakbasu.capstone.data.RoutineDetailsColumns;
import com.example.shaunakbasu.capstone.data.CalorieIntakeProvider;
import com.example.shaunakbasu.capstone.data.RoutineDetailsProvider;
import com.example.shaunakbasu.capstone.services.FoodItemServices;

import java.util.ArrayList;

/**
 * Created by shaunak basu on 11-11-2016.
 */
public class RoutineDisplay extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ITEM_DISPLAY_LOADER=1;
    String routine_header;
    RoutineChildAdapter routineChildAdapter;
    ListView list_routine_display;
    TextView routine_display_header;
    ImageButton permanent_delete,edit_routine,uncheck_all;
    View root;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        routine_header=getIntent().getStringExtra("routine_header");
        setContentView(R.layout.routine_display);
        root=(View)findViewById(R.id.routine_display_root);
        Toolbar toolbar=(Toolbar)findViewById(R.id.routine_display_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportLoaderManager().initLoader(ITEM_DISPLAY_LOADER,null,this);
        routine_display_header=(TextView)toolbar.findViewById(R.id.routine_display_header);
        routine_display_header.setText(routine_header);
        permanent_delete=(ImageButton)toolbar.findViewById(R.id.routine_permanent_delete);
        edit_routine=(ImageButton)toolbar.findViewById(R.id.routine_edit);
        uncheck_all=(ImageButton)toolbar.findViewById(R.id.routine_uncheck_all);

        uncheck_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheck();
            }
        });


        permanent_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRoutine();
            }
        });


        edit_routine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),RoutineDetailActivity.class);
                intent.putExtra("key",routine_header);
                startActivity(intent);
            }
        });
        routineChildAdapter=new RoutineChildAdapter(getApplicationContext(), null, 0, new RoutineChildAdapter.deleteItemListener() {
            @Override
            public void onDeleteClick(View view, String workout_details) {
                deleteItem(workout_details);
            }
        }, new RoutineChildAdapter.checkedListener() {
            @Override
            public void onCheck(View view, String routine_item) {
                updateChecker(routine_item);
            }
        });
        list_routine_display=(ListView)findViewById(R.id.list_routine_display);
        list_routine_display.setAdapter(routineChildAdapter);

    }


    public void uncheck(){

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.confirmation_dialogue, null);

        TextView message_text=(TextView)promptsView.findViewById(R.id.calorie_confirmation);

        message_text.setText(getString(R.string.routine_uncheck_items));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RoutineDisplay.this,R.style.MyAlertDialogStyle);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Cursor cursor = getContentResolver().query(RoutineDetailsProvider.Routine.CONTENT_URI,
                                        new String[]{RoutineDetailsColumns.WORK_OUT_DETAILS,RoutineDetailsColumns.CHECKED},
                                        RoutineDetailsColumns.ROUTINE_HEADER + " =?",
                                        new String[]{routine_header},null);

                                if(cursor!=null)
                                { int rows=0;
                                    cursor.moveToFirst();
                                    do{
                                        String work_out = cursor.getString(cursor.getColumnIndex(RoutineDetailsColumns.WORK_OUT_DETAILS));
                                        String check = cursor.getString(cursor.getColumnIndex(RoutineDetailsColumns.CHECKED));
                                        if(check.equals("true")){
                                            check="false";
                                            ContentValues values = new ContentValues();
                                            values.put(RoutineDetailsColumns.ROUTINE_HEADER, routine_display_header.getText().toString());
                                            values.put(RoutineDetailsColumns.WORK_OUT_DETAILS, work_out);
                                            values.put(RoutineDetailsColumns.CHECKED, check);


                                            rows = getContentResolver().update(RoutineDetailsProvider.Routine.CONTENT_URI,
                                                    values,
                                                    RoutineDetailsColumns.ROUTINE_HEADER + " =? AND "+
                                                            RoutineDetailsColumns.WORK_OUT_DETAILS + " =?",
                                                    new String[]{routine_display_header.getText().toString(),work_out}) + rows;

                                        }

                                    }while(cursor.moveToNext());

                                    cursor.close();
                                    if(rows>0){
                                        Snackbar.make(root, getResources().getString(R.string.routine_unchecked), Snackbar.LENGTH_LONG).show();
                                        routineChildAdapter.notifyDataSetChanged();
                                    }

                                    else
                                        Snackbar.make(root, getResources().getString(R.string.routine_uncheck_unable), Snackbar.LENGTH_LONG).show();
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


    public void updateChecker(String routine_item){

        String checked_update;

        Cursor cursor=getContentResolver().query(RoutineDetailsProvider.Routine.CONTENT_URI,
                new String[]{RoutineDetailsColumns.CHECKED},
                RoutineDetailsColumns.ROUTINE_HEADER+" =? AND "+
                RoutineDetailsColumns.WORK_OUT_DETAILS+" =?",
                new String[]{routine_header,routine_item},null);
        cursor.moveToFirst();

        String checked=cursor.getString(cursor.getColumnIndex(RoutineDetailsColumns.CHECKED));

        if(checked.equals("true"))
            checked_update="false";

        else
            checked_update="true";


        ContentValues values=new ContentValues();
        values.put(RoutineDetailsColumns.WORK_OUT_DETAILS,routine_item);
        values.put(RoutineDetailsColumns.ROUTINE_HEADER,routine_header);
        values.put(RoutineDetailsColumns.CHECKED,checked_update);

        int row=getContentResolver().update(RoutineDetailsProvider.Routine.CONTENT_URI,
                values,RoutineDetailsColumns.ROUTINE_HEADER+" =? AND "+
                        RoutineDetailsColumns.WORK_OUT_DETAILS+" =?",
                new String[]{routine_header,routine_item});

        cursor.close();
    }


    public void deleteRoutine(){

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.confirmation_dialogue, null);

        TextView message_text=(TextView)promptsView.findViewById(R.id.calorie_confirmation);

        message_text.setText(getString(R.string.routine_confirmation));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RoutineDisplay.this,R.style.MyAlertDialogStyle);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                int delete = getContentResolver().delete(RoutineDetailsProvider.Routine.CONTENT_URI,
                                        RoutineDetailsColumns.ROUTINE_HEADER + " =?",
                                        new String[]{routine_header});
                                if (delete > 0) {
                                    Snackbar.make(root, getResources().getString(R.string.routine_deleted), Snackbar.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Snackbar.make(root, getResources().getString(R.string.routine_unable_delete), Snackbar.LENGTH_LONG).show();
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


    public void deleteItem(String work_info){

        int delete=getContentResolver().delete(RoutineDetailsProvider.Routine.CONTENT_URI,
                RoutineDetailsColumns.ROUTINE_HEADER+" =? AND "+
                RoutineDetailsColumns.WORK_OUT_DETAILS+" =?",
                new String[]{routine_header,work_info});
        if(delete>0){
            Snackbar.make(root,getResources().getString(R.string.routine_item_deleted),Snackbar.LENGTH_SHORT).show();
            routineChildAdapter.notifyDataSetChanged();
        }

        else
            Snackbar.make(root,getResources().getString(R.string.routine_item_not_deleted),Snackbar.LENGTH_SHORT);

    }

    @Override
    public void onBackPressed(){
        Intent intent=new Intent(this,MainDisplayActivity.class);
        intent.putExtra("tab_number",2);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(), RoutineDetailsProvider.Routine.CONTENT_URI,
                new String[]{RoutineDetailsColumns._ID,RoutineDetailsColumns.WORK_OUT_DETAILS, RoutineDetailsColumns.CHECKED},
                RoutineDetailsColumns.ROUTINE_HEADER + " =?",
                new String[]{routine_header},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        routineChildAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        routineChildAdapter.swapCursor(null);
    }
}
