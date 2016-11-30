package com.example.shaunakbasu.capstone;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by shaunak basu on 17-11-2016.
 */
public class Stopwatch extends Fragment {

    ImageButton start, reset;
    TextView time;
    long starttime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedtime = 0L;
    int t = 1;
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    Handler handler = new Handler();
    View rootView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public Runnable updateTimer = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - starttime;
            updatedtime = timeSwapBuff + timeInMilliseconds;
            secs = (int) (updatedtime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            milliseconds = (int) (updatedtime % 1000);
            String colon = getResources().getString(R.string.colon);
            String display = "" + mins + colon + String.format("%02d", secs) + colon
                    + String.format("%03d", milliseconds);
            time.setText(display);
            time.setTextColor(getResources().getColor(R.color.colorAccent));
            handler.postDelayed(this, 0);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.stopwatch, container, false);
        }

        start = (ImageButton) rootView.findViewById(R.id.stopwatch_play);
        reset = (ImageButton) rootView.findViewById(R.id.stopwatch_reset);

        time = (TextView) rootView.findViewById(R.id.stopwatch_text);


        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub

                if (t == 1) {
                    start.setImageResource(R.drawable.ic_pause_circle_filled_black_48dp);
                    starttime = SystemClock.uptimeMillis();
                    handler.postDelayed(updateTimer, 0);
                    t = 0;
                } else {
                    start.setImageResource(R.drawable.ic_play_circle_filled_black_48dp);
                    time.setTextColor(Color.WHITE);
                    timeSwapBuff += timeInMilliseconds;
                    handler.removeCallbacks(updateTimer);
                    t = 1;
                }
            }
        });


        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                starttime = 0L;
                timeInMilliseconds = 0L;
                timeSwapBuff = 0L;
                updatedtime = 0L;
                t = 1;
                secs = 0;
                mins = 0;
                milliseconds = 0;
                start.setImageResource(R.drawable.ic_play_circle_filled_black_48dp);
                handler.removeCallbacks(updateTimer);
                String initial = getResources().getString(R.string.stopwatch_initial);
                time.setText(initial);
            }
        });


        return rootView;

    }
}
