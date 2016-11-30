package com.example.shaunakbasu.capstone;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.shaunakbasu.capstone.data.CalorieBurntColumns;
import com.example.shaunakbasu.capstone.data.CalorieBurntProvider;
import com.example.shaunakbasu.capstone.widget.MyWidgetProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

/**
 * Created by shaunak basu on 19-11-2016.
 */
public class RunFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<LocationSettingsResult> {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 6000;
    public static final int REQUEST_LOCATION = 100;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected Location mCurrentLocation;
    protected LatLng first_location;
    protected Button mStartUpdatesButton, mStopUpdatesButton;
    protected Boolean mRequestingLocationUpdates;
    TextView mDistance, mTime, mSpeed, mCalorie;

    long start_time, stop_time, min_time, sec_time;
    double distance_checker;

    View rootView, parentLayout;
    GoogleMap googleMap;
    boolean mapReady = false, getPoly = false;

    Double lat_dif = 0.0, lon_dif = 0.0;
    double distance_value;
    int marker_checker;
    private String TAG = RunFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestingLocationUpdates = false;
        marker_checker = 0;

        createLocationRequest();
        buildLocationSettingsRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        parentLayout = rootView.findViewById(R.id.root_view_map);

        mDistance = (TextView) rootView.findViewById(R.id.map_distance_text);
        mTime = (TextView) rootView.findViewById(R.id.map_time_text);
        mSpeed = (TextView) rootView.findViewById(R.id.map_speed_text);
        mCalorie = (TextView) rootView.findViewById(R.id.map_calories_text);

        mStartUpdatesButton = (Button) rootView.findViewById(R.id.map_button_start);
        mStopUpdatesButton = (Button) rootView.findViewById(R.id.map_button_stop);

        mStartUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startUpdatesButtonHandler();
            }
        });

        mStopUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopUpdatesButtonHandler();
            }
        });


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "REQUESTING PERMISIION");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    200);
        } else {
            Log.v(TAG, "PERMISSION GRANTED");
            /*googleMap.setMyLocationEnabled(true);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            start_time = SystemClock.elapsedRealtime();*/
            checkLocationSettings();
        }

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        Log.v(TAG, "IN ON RESULT!!!!!");
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.v(TAG, "IN ONACTIVITYRESULT!!!!!");
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler() {
        checkLocationSettings();
        marker_checker = 1;
        mRequestingLocationUpdates = true;
        googleMap.clear();
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates.
     */
    public void stopUpdatesButtonHandler() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        stopLocationUpdates();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        Log.v(TAG, "IN STARTLOCATIONUPDATSRESULT!!!!!");

        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                googleMap.setMyLocationEnabled(true);

            }

        } catch (SecurityException e) {

        }

    }


    protected void stopLocationUpdates() {
        Log.v(TAG, "IN STOPLOCATIONUPDAT!!!!!");

        if (marker_checker != 5) {
            Snackbar.make(parentLayout, getResources().getString(R.string.map_press_start), Snackbar.LENGTH_SHORT).show();
        } else {
            marker_checker = 2;

            stop_time = SystemClock.elapsedRealtime();

            try {
                Location l_location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mCurrentLocation = l_location;
                updateUI();

            } catch (SecurityException e) {
                e.printStackTrace();
            }

            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    mRequestingLocationUpdates = false;

                }
            });
        }


    }

    private void updateUI() {

        String latitude = String.valueOf(mCurrentLocation.getLatitude());
        String longitude = String.valueOf(mCurrentLocation.getLongitude());

        LatLng marker = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        CameraPosition target = CameraPosition.builder().target(marker).zoom(18).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 2000, null);


        if (marker_checker == 0) {
            first_location = marker;
        }


        if (marker_checker == 1) {
            marker_checker = 5;
            first_location = marker;
            MarkerOptions start = new MarkerOptions().position(marker).title(getString(R.string.marker_start));
            googleMap.addMarker(start);
            distance_value = 0;
            getPoly = true;
            start_time = SystemClock.elapsedRealtime();

        }

        lat_dif = Math.abs(first_location.latitude - marker.latitude);
        lon_dif = Math.abs(first_location.longitude - marker.longitude);


        if (marker_checker == 2) {
            MarkerOptions start = new MarkerOptions().position(marker).title(getString(R.string.marker_stop));
            googleMap.addMarker(start);

            if (lat_dif > 0.000005 || lon_dif > 0.000005) {
                distance_checker = Utility.distance(first_location.latitude, first_location.longitude,
                        marker.latitude, marker.longitude);
                distance_value += distance_checker;
            }

            long f_time = (stop_time - start_time) / 1000;
            min_time = f_time / 60;
            sec_time = f_time % 60;
            Log.v(TAG + "STOP", Long.toString(stop_time));
            Log.v(TAG + "START", Long.toString(start_time));
            Log.v("DISTANCE", Double.toString(distance_value));

            double speed = (distance_value / f_time) * 2.23694;

            double f_speed = Double.parseDouble(Utility.twoPlaceConverter(Double.toString(speed)));

            double distance_miles = (distance_value / 1609.34);

            String str_dist = Utility.twoPlaceConverter(Double.toString(distance_value)) + " " + getResources().getString(R.string.map_meters);
            String str_time = Long.toString(min_time) + getResources().getString(R.string.map_min) + Long.toString(sec_time) + getResources().getString(R.string.map_sec);
            String str_speed = Double.toString(f_speed);
            Double cal_burn;

            if (distance_value > 0.0) {
                cal_burn = caloriesBurnt(distance_miles);
            } else {
                cal_burn = 0.0;
            }

            String cal_burn_two_place = Utility.twoPlaceConverter(Double.toString(cal_burn));
            String cal_message = cal_burn_two_place + " " + getResources().getString(R.string.map_cal_burn);

            mDistance.setText(str_dist);
            mTime.setText(str_time);
            mSpeed.setText(str_speed);
            mCalorie.setText(cal_message);

            if (distance_value > 0.0) {
                calorieBurnDataInput(cal_burn_two_place);
            }

            distance_value = 0;
            marker_checker = 0;

        }

        //Log.v("LAT_DIFF",lat_dif);

        if (lat_dif > 0.000005 || lon_dif > 0.000005) {
            distance_checker = Utility.distance(first_location.latitude, first_location.longitude,
                    marker.latitude, marker.longitude);
            distance_value += distance_checker;
        }

        Log.v("DISTANCE", Double.toString(distance_value));

        Log.v("LATITUDE!!!&LONGITUDE", marker.toString());
        first_location = marker;

    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.i(TAG, "Connected to GoogleApiClient");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        startLocationUpdates();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }

                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateUI();

    }

    public Double caloriesBurnt(double miles) {

        SharedPreferences sharedPreferences;
        sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.user_shared_pref), Context.MODE_PRIVATE);

        String weight = sharedPreferences.getString(getResources().getString(R.string.user_weight), "");

        Double cal = Double.parseDouble(weight) * miles * 0.63;
        return cal;
    }


    public void calorieBurnDataInput(String cal) {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        Cursor cursor = getActivity().getContentResolver().query(CalorieBurntProvider.Calorie_Burnt.CONTENT_URI,
                new String[]{CalorieBurntColumns.AMOUNT},
                CalorieBurntColumns.DATE + " =? AND " +
                        CalorieBurntColumns.MONTH + " =? AND " +
                        CalorieBurntColumns.YEAR + " =? ",
                new String[]{Integer.toString(day), Integer.toString(month), Integer.toString(year)}, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Double cal_exist = Double.parseDouble(cursor.getString(cursor.getColumnIndex(CalorieBurntColumns.AMOUNT)));
            Double cal_new = cal_exist + Double.parseDouble(cal);
            String cal_up = Utility.twoPlaceConverter(Double.toString(cal_new));
            ContentValues values = new ContentValues();
            values.put(CalorieBurntColumns.AMOUNT, cal_up);
            values.put(CalorieBurntColumns.DATE, day);
            values.put(CalorieBurntColumns.MONTH, month);
            values.put(CalorieBurntColumns.YEAR, year);

            int num = getActivity().getContentResolver().update(CalorieBurntProvider.Calorie_Burnt.CONTENT_URI,
                    values, CalorieBurntColumns.DATE + " =? AND " +
                            CalorieBurntColumns.MONTH + " =? AND " +
                            CalorieBurntColumns.YEAR + " =? ",
                    new String[]{Integer.toString(day), Integer.toString(month), Integer.toString(year)});

            if (num > 0) {
                Snackbar.make(parentLayout, getResources().getString(R.string.calorie_logged), Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(parentLayout, getResources().getString(R.string.calorie_db_error), Snackbar.LENGTH_LONG).show();
            }
        } else {
            ContentValues values = new ContentValues();
            values.put(CalorieBurntColumns.AMOUNT, cal);
            values.put(CalorieBurntColumns.DATE, day);
            values.put(CalorieBurntColumns.MONTH, month);
            values.put(CalorieBurntColumns.YEAR, year);

            Uri uri = getActivity().getContentResolver().insert(CalorieBurntProvider.Calorie_Burnt.CONTENT_URI, values);
            long num = ContentUris.parseId(uri);

            if (num != -1) {
                Snackbar.make(parentLayout, getResources().getString(R.string.calorie_logged), Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(parentLayout, getResources().getString(R.string.calorie_db_error), Snackbar.LENGTH_LONG).show();
            }
        }
        cursor.close();

        Intent intent = new Intent(getActivity(), MyWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getActivity().getApplication()).getAppWidgetIds(new ComponentName(getActivity().getApplication(), MyWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getActivity().sendBroadcast(intent);

    }

    /**
     * Disables both buttons when functionality is disabled due to insuffucient location settings.
     * Otherwise ensures that only one button is enabled at any time. The Start Updates button is
     * enabled if the user is not requesting location updates. The Stop Updates button is enabled
     * if the user is requesting location updates.
     */


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
    }


    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    public void onMapReady(GoogleMap map) {
        Log.v("MAP IS READY!", "!!!!!!!!!!");
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mapReady = true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapFragment f = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.map);
        if (f != null)
            getActivity().getFragmentManager().beginTransaction().remove(f).commit();
    }

}