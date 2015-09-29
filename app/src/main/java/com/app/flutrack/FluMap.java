package com.app.flutrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.flutrack.API.Flutrack.FlutrackClient;
import com.app.flutrack.API.OpenWeather.OpenWeatherClient;
import com.app.flutrack.Models.CurrentWeather;
import com.app.flutrack.Models.Flutrack;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;

import io.nlopez.smartlocation.SmartLocation;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * This class oversees any action concerning the GoogleMap and the various information that
 * are being attached to it.
 */
public class FluMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String numberOfDays = "7";

    /**
     * A  BroadcastReceiver that transmits system wide events concerning changes in the device's
     * connectivity.
     */
    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // We only care for events concerning potential connection to a network.
            if (isOnline()){

                // SmartLocation library is being used to simplify the overall code structure and to
                // make the code more maintainable.
                Location lastLocation = SmartLocation.with(getActivity()).location().getLastLocation();
                downloadFlutrackData();
                if (lastLocation != null) {
                    downloadOpenWeatherData(lastLocation.getLatitude(), lastLocation.getLongitude());
                }
            } else {
                showToast("No available Connection");
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Specify the system action that the BroadcastReceiver will start observing.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkStateReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister the receiver when the user pauses the app to avoid unnecessary usage.
        getActivity().unregisterReceiver(networkStateReceiver);
    }

    /**
     * Callback that notifies that the GoogleMap is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /**
     * Downloads weather information for the user's current location. Uses the OpenWeather API
     * to gather the necessary data.
     */
    private void downloadOpenWeatherData(final double lat, final double lon) {
        OpenWeatherClient.
                getOpenWeatherApiClient().
                getOpenWeatherData(lat, lon, new Callback<CurrentWeather>() {
                    @Override
                    public void success(CurrentWeather result, Response response) {
                        IconGenerator iconFactory = new IconGenerator(getActivity());
                        addIcon(iconFactory, "Temperature: " + String.valueOf(result.main.getTemperature())
                                + '\n' + "Pressure: " + String.valueOf(result.main.getPressure())
                                + '\n' + "Humidity: " + String.valueOf(result.main.getHumidity()), new LatLng(lat, lon));
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
     * Attaches a custom-made marker, that contains weather-related information, to the GoogleMap.
     */
    private void addIcon(IconGenerator iconFactory, String text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        mMap.addMarker(markerOptions);
        CameraUpdate center = CameraUpdateFactory.newLatLng(position);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(5);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    /**
     * Displays various messages to the user.
     */
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
    /**
     * Checks for a connection to any network. Doesn't ensure that the device is connected
     * to an active internet connection.
     */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}