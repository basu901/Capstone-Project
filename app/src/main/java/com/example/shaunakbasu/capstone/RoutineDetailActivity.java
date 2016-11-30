package com.example.shaunakbasu.capstone;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaunakbasu.capstone.data.RoutineDetailsColumns;
import com.example.shaunakbasu.capstone.data.RoutineDetailsProvider;

/**
 * Created by shaunak basu on 11-11-2016.
 */
public class RoutineDetailActivity extends AppCompatActivity {

    EditText work_out_text;
    EditText routine_details_header;
    ImageButton done_button;
    ImageButton add_button;
    LinearLayout check_box_container;
    String key;
    TextView textView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_detail);
        key = getIntent().getStringExtra("key");
        work_out_text = (EditText) findViewById(R.id.work_out_text);
        done_button = (ImageButton) findViewById(R.id.routine_done_button);
        add_button = (ImageButton) findViewById(R.id.routine_item_add_button);
        routine_details_header = (EditText) findViewById(R.id.routine_details_header);
        routine_details_header.setNextFocusForwardId(R.id.work_out_text);

        check_box_container = (LinearLayout) findViewById(R.id.linear_checkbox_container);
        if (!key.equals("new")) {
            routine_details_header.setText(key);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri id;
                long confirm;
                if ((work_out_text.getText().toString()).length() > 0) {

                    //check_box_container.setLayoutParams(new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if ((routine_details_header.getText().toString()).length() > 0) {

                        textView = new TextView(getApplicationContext());
                        textView.setText(work_out_text.getText().toString());
                        textView.setTextColor(Color.BLACK);
                        //textView.setId(i);
                        //i++;
                        LinearLayout.LayoutParams margins = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        margins.setMargins(8, 8, 8, 8); // llp.setMargins(left, top, right, bottom);
                        textView.setLayoutParams(margins);

                        check_box_container.addView(textView);
                        work_out_text.setNextFocusForwardId(R.id.routine_item_add_button);
                        ContentValues routine_values = new ContentValues();
                        routine_values.put(RoutineDetailsColumns.CHECKED, "false");
                        routine_values.put(RoutineDetailsColumns.ROUTINE_HEADER, routine_details_header.getText().toString());
                        routine_values.put(RoutineDetailsColumns.WORK_OUT_DETAILS, work_out_text.getText().toString());
                        id = getContentResolver().insert(RoutineDetailsProvider.Routine.CONTENT_URI, routine_values);
                        confirm = ContentUris.parseId(id);
                        if (confirm > 0) {
                            Snackbar.make(view, getResources().getString(R.string.routine_inserted), Snackbar.LENGTH_LONG).show();
                            work_out_text.setText("");
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.routine_fill_in_header), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.routine_no_workout_details), Toast.LENGTH_LONG).show();
                }
            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri id;
                long confirm;
                if (!key.equals("new")) {
                    if (!routine_details_header.getText().toString().equals(key)) {
                        Cursor cursor = getContentResolver().query(RoutineDetailsProvider.Routine.CONTENT_URI,
                                new String[]{RoutineDetailsColumns.WORK_OUT_DETAILS,
                                        RoutineDetailsColumns.CHECKED},
                                RoutineDetailsColumns.ROUTINE_HEADER + " =? ",
                                new String[]{key}, null);
                        int rows = 0;
                        cursor.moveToFirst();
                        do {
                            String work_out = cursor.getString(cursor.getColumnIndex(RoutineDetailsColumns.WORK_OUT_DETAILS));
                            String check = cursor.getString(cursor.getColumnIndex(RoutineDetailsColumns.CHECKED));

                            ContentValues values = new ContentValues();
                            values.put(RoutineDetailsColumns.ROUTINE_HEADER, routine_details_header.getText().toString());
                            values.put(RoutineDetailsColumns.WORK_OUT_DETAILS, work_out);
                            values.put(RoutineDetailsColumns.CHECKED, check);

                            rows = getContentResolver().update(RoutineDetailsProvider.Routine.CONTENT_URI,
                                    values,
                                    RoutineDetailsColumns.ROUTINE_HEADER + " =? AND " +
                                            RoutineDetailsColumns.WORK_OUT_DETAILS + " =?",
                                    new String[]{key, work_out}) + rows;

                        } while (cursor.moveToNext());

                        if (rows > 0)
                            Snackbar.make(view, getResources().getString(R.string.routine_update), Snackbar.LENGTH_LONG).show();
                        else
                            Snackbar.make(view, getResources().getString(R.string.routine_update_unable), Snackbar.LENGTH_LONG).show();
                        cursor.close();
                    }

                    Snackbar.make(view, getResources().getString(R.string.routine_update), Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), RoutineDisplay.class);
                    intent.putExtra("routine_header", routine_details_header.getText().toString());
                    startActivity(intent);

                } else {
                    if ((routine_details_header.getText().toString()).length() > 0) {

                        if (textView == null) {
                            Snackbar.make(view, getResources().getString(R.string.routine_cant_create), Snackbar.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), RoutineDisplay.class);
                            intent.putExtra("routine_header", routine_details_header.getText().toString());
                            startActivity(intent);
                        }

                    } else
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.routine_fill_in_header), Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}
