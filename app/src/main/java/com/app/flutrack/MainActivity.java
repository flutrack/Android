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
import com.app.flutrack.API.Flutrack.FlutrackClient;
import com.app.flutrack.API.OpenWeather.OpenWeatherClient;
import com.app.flutrack.Models.CurrentWeather;
import com.app.flutrack.Models.Flutrack;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

import io.nlopez.smartlocation.SmartLocation;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String numberOfDays = "7";
    private Location currentLocation;

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

        if (savedInstanceState == null) {
            mapFragment.setRetainInstance(true);
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Specify the system action that the BroadcastReceiver will be associated with.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister the receiver when the user pauses the app to avoid unnecessary workload.
        unregisterReceiver(networkStateReceiver);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final FloatingActionButton fluReport = (FloatingActionButton) findViewById(R.id.flu_report);
        fluReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentLocation = getUserLocation();
                if (currentLocation != null) {
                    FluSymptomsDialog(currentLocation);
                } else {
                    showToast("Location not available");
                }
            }
        });
        final FloatingActionButton weatherForecast = (FloatingActionButton) findViewById(R.id.weather_forecast);
        weatherForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentLocation = getUserLocation();
                if (currentLocation != null) {
                    downloadOpenWeatherData(currentLocation);
                } else {
                    showToast("Location not available");
                }
            }
        });
    }

    private Location getUserLocation() {
        Location lastLocation = SmartLocation.with(getApplication()).location().getLastLocation();
        return lastLocation;
    }

    private void downloadOpenWeatherData(final Location currentLocation) {
        OpenWeatherClient.
                getOpenWeatherApiClient().
                getOpenWeatherData(currentLocation.getLatitude(), currentLocation.getLongitude(),
                        new Callback<CurrentWeather>() {
                            @Override
                            public void success(CurrentWeather result, Response response) {

                                String currentForecast = String.format("%s\n%s\n%.2f ℃\n",
                                        result.getLocationName(),
                                        WordUtils.capitalize(result.getWeather().get(0).getDescription()),
                                        result.getMain().getTemperature());
                                WeatherForecastDialog(currentForecast);
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                                showToast(retrofitError.getMessage());
                            }
                        });
    }

    private void WeatherForecastDialog(String currentForecast) {
        new MaterialDialog.Builder(this)
                .title(R.string.weatherForecastDialogTitle)
                .content(currentForecast)
                .show();
    }

    private void FluSymptomsDialog(Location currentLocation) {
        new MaterialDialog.Builder(this)
                .title(R.string.fluSymptomsDialogTitle)
                .items(R.array.fluSymptoms)
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

}