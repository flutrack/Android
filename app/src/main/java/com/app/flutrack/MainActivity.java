package com.app.flutrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.app.flutrack.API.Flutrack.FlutrackClient;
import com.app.flutrack.API.OpenWeather.OpenWeatherClient;
import com.app.flutrack.Models.Flutrack;
import com.app.flutrack.Models.WeeklyWeatherForecast;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.text.WordUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener {

    protected GoogleApiClient googleApiClient;
    protected Location lastLocation;
    private GoogleMap mMap;
    private String numberOfDays = "7";

    /**
     * A  BroadcastReceiver that transmits system wide events concerning changes in the device's
     * connectivity.
     */
    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // We only care for events about connecting to a network.
            if (isOnline()) {
                downloadFlutrackData();
            } else {
                showToast("No available Connection");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        /**
         * Retain the map fragment across activity restarts (e.g., from screen rotations).
         */
        if (savedInstanceState == null) {

            // First incarnation of this activity.
            mapFragment.setRetainInstance(true);
        }

        mapFragment.getMapAsync(this);
        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Specify the system action that the BroadcastReceiver will be associated with.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        // Start the BroadcastReceiver.
        registerReceiver(networkStateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the BroadcastReceiver when the user pauses the app, in order to avoid
        // unnecessary workload.
        unregisterReceiver(networkStateReceiver);
    }

    /**
     * Builds a GoogleApiClient.
     */
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    /**
     * Callback that informs that the GoogleMap is ready for use.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final FloatingActionButton fluReport = (FloatingActionButton) findViewById(R.id.flu_report);
        final FloatingActionButton weatherForecast = (FloatingActionButton) findViewById(R.id.weather_forecast);

        fluReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastLocation != null) {
                    FluSymptomsDialog(lastLocation);
                } else {
                    showToast("Location not available");
                }
            }
        });

        weatherForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastLocation != null) {
                    downloadOpenWeatherData(lastLocation);
                } else {
                    showToast("Location not available");
                }
            }
        });
    }

    /**
     * Downloads weather information regarding the user's current location. Uses the OpenWeather API
     * to gather the necessary data.
     */
    private void downloadOpenWeatherData(final Location currentLocation) {
        OpenWeatherClient.
                getOpenWeatherApiClient().
                getOpenWeatherData(currentLocation.getLatitude(), currentLocation.getLongitude(),
                        new Callback<WeeklyWeatherForecast>() {
                            @Override
                            public void success(WeeklyWeatherForecast result, Response response) {
                                showWeeklyWeatherForecast(result);
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                                showToast(retrofitError.getMessage());
                            }
                        });
    }

    /**
     * Uses the Flutrack API to download and add to the GoogleMap tweets associated with the
     * influenza virus.
     */
    private void downloadFlutrackData() {
        FlutrackClient.
                getFlutrackApiClient().
                getTimeData(numberOfDays, new Callback<ArrayList<Flutrack>>() {
                    @Override
                    public void success(ArrayList<Flutrack> result, Response response) {
                        double latitude;
                        double longitude;

                        for (int i = 0; i < result.size(); i++) {
                            latitude = Double.parseDouble(result.get(i).getLatitude());
                            longitude = Double.parseDouble(result.get(i).getLongitude());
                            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,
                                    longitude)));
                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        showToast(retrofitError.getMessage());
                    }
                });
    }

    /**
     * Displays the weather information using a simple list dialog layout.
     */
    private void showWeeklyWeatherForecast(WeeklyWeatherForecast forecast) {
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(this);
        WeeklyWeatherForecast.City city = forecast.getCity();
        ArrayList<WeeklyWeatherForecast.List> list = forecast.getList();

        for (int i = 0; i < list.size(); i++) {
            adapter.add(new MaterialSimpleListItem.Builder(this)
                    .content(formatWeatherOutput(list.get(i)))
                    .build());
        }

        new MaterialDialog.Builder(this)
                .title("Forecast for " + city.getName() + ", " + city.getCountry())
                .adapter(adapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                    }
                })
                .show();
    }

    /**
     * Multi-choice dialog for reporting flu symptoms.
     */
    private void FluSymptomsDialog(Location currentLocation) {
        new MaterialDialog.Builder(this)
                .title(R.string.fluSymptomsDialogTitle)
                .items(R.array.fluSymptomsArray)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        StringBuilder str = new StringBuilder();

                        for (int i = 0; i < which.length; i++) {
                            if (i > 0) {
                                str.append(',');
                                str.append(' ');
                            }
                            str.append(text[i]);
                        }
                        // postFluReport((str.toString()),currentLocation.getLatitude(),
                        //  currentLocation.getLongitude());
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }

    /**
     * Converts a UNIX timestamp to a proper Date format and then finds the corresponding day of
     * the week for that date.
     */
    private String timestampToDay(int timestamp) {
        Timestamp stamp = new Timestamp((long) timestamp * 1000);
        Date date = new Date(stamp.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E");

        return simpleDateFormat.format(date);
    }

    /**
     * Formats the Weather output that will be displayed in the MaterialDialog List.
     */
    private String formatWeatherOutput(WeeklyWeatherForecast.List list) {
        String weather = String.format("%s - %s, %.2f℃",
                timestampToDay(list.getDt()), WordUtils.capitalize(list.getWeather().get(0).getDescription()),
                list.getTemp().getDay());

        return weather;
    }

    /**
     * Displays various messages to the user.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks for a connection to any network. Doesn't ensure that the device is connected
     * to an active internet connection.
     */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        showToast("Connection failed");
    }

    @Override
    public void onConnectionSuspended(int cause) {

        showToast("Connection suspended");

        // Attempt to re-establish the connection.
        googleApiClient.connect();
    }

}