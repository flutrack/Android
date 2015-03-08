package com.flutrack.app;

import java.util.ArrayList;
import android.location.Location;
import android.location.LocationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/*
 * 
 * 
 * 
 * 
 * 
 * 
 */

public class fluMap extends Fragment implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

	private String url1 = "http://api.flutrack.org/?time=10";
	private String url2 = ""; //Put here the url for the get_json.php file
	private GoogleMap map;
	private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000; //upper bound for receiving location updates
	private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2; //lower bound
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private Location mCurrentLocation;
	private SharedPreferences.Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map, container, false);
		if (map == null) { // quick fix because of google maps bug on lollipop
			map = ((SupportMapFragment) getChildFragmentManager()
					.findFragmentById(R.id.map)).getMap();
		}
		buildGoogleApiClient();
		new downloadData().execute(url1, url2);
		return view;
	}

	public boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	public boolean isLocEnabled() {
		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
		createLocationRequest();
	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest
				.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest
				.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); // 100m. block accuracy
	}

	protected void startLocationUpdates() {
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}

	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mGoogleApiClient.isConnected()) {
			startLocationUpdates();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		stopLocationUpdates();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mCurrentLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
		if (mCurrentLocation != null) {
			CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
					mCurrentLocation.getLatitude(), mCurrentLocation
							.getLongitude()));
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
			map.moveCamera(center);
			map.animateCamera(zoom);
			SharedPreferences sharedPref = getActivity().getPreferences(
					Context.MODE_PRIVATE);
			editor = sharedPref.edit();
			editor.putString("latitude",
					String.valueOf(mCurrentLocation.getLatitude()));
			editor.putString("longitude",
					String.valueOf(mCurrentLocation.getLongitude()));
			editor.commit();
			/*
				4 debugging
			 map.addMarker(new MarkerOptions()  
			 .position(new LatLng(
			 mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()))
			 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
			 
		   */
		}
		startLocationUpdates();
	}

	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		CameraUpdate center = CameraUpdateFactory
				.newLatLng(new LatLng(mCurrentLocation.getLatitude(),
						mCurrentLocation.getLongitude()));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
		map.moveCamera(center);
		map.animateCamera(zoom);
		SharedPreferences sharedPref = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		editor.putString("latitude",
				String.valueOf(mCurrentLocation.getLatitude()));
		editor.putString("longitude",
				String.valueOf(mCurrentLocation.getLongitude()));
		editor.commit();
		/*
		      4 debugging
		 map.addMarker(new MarkerOptions()
		 .position(new LatLng(
		 mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()))
		 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		 Log.v(String.valueOf(mCurrentLocation.getLatitude()),String.valueOf(mCurrentLocation.getLongitude()));
		 
		*/
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

	}

	class downloadData extends AsyncTask<String, Void, String[]> {
		private ProgressDialog nDialog;
		WifiManager manager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();

		@Override
		protected void onPreExecute() {
			nDialog = new ProgressDialog(getActivity());
			nDialog.setMessage("Please wait");
			nDialog.setTitle("Downloading data...");
			nDialog.setIndeterminate(true);
			nDialog.setCancelable(false);
			nDialog.show();
		}

		protected String[] doInBackground(String... params) {
			String link1 = params[0];
			String link2 = params[1];
			Networking n = new Networking();
			String flutrackDB = n.GET(link1);
			String androidappDB = n.GET(link2);
			String results[] = new String[2];
			results[0] = flutrackDB;
			results[1] = androidappDB;
			return results;
		}

		@Override
		protected void onPostExecute(String results[]) {

			if (results[0].length() != 0 && results[1].length() != 0) {
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonArray Jarray = parser.parse(results[0]).getAsJsonArray();
				ArrayList<Tweet> tweet_arr = new ArrayList<Tweet>();
				JsonArray Jarray2 = parser.parse(results[1]).getAsJsonArray();
				ArrayList<Symptoms> flu_db = new ArrayList<Symptoms>();
				for (JsonElement obj : Jarray) {
					Tweet twitter = gson.fromJson(obj, Tweet.class);
					tweet_arr.add(twitter);
				}

				for (JsonElement obj : Jarray2) {
					Symptoms text2 = gson.fromJson(obj, Symptoms.class);
					flu_db.add(text2);
				}

				double latitude = 0;
				double longitude = 0;
				for (int i = 0; i < tweet_arr.size(); i++) {
					latitude = Double.parseDouble(tweet_arr.get(i).getLat());
					longitude = Double.parseDouble(tweet_arr.get(i).getLon());
					map.addMarker(new MarkerOptions().position(new LatLng(
							latitude, longitude)));
				}
				for (int i = 0; i < flu_db.size(); i++) {
					if (!info.getMacAddress().equals(flu_db.get(i).getUserID())) { //makes sure that we do not display reports 
																				  //that were submitted from out device
						latitude = Double.parseDouble(flu_db.get(i)
								.getLatitude());
						longitude = Double.parseDouble(flu_db.get(i)
								.getLongitude());
						map.addMarker(new MarkerOptions().position(new LatLng(
								latitude, longitude)));
					}
				}
			} else {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getActivity(),
								"Could not download data", Toast.LENGTH_LONG)
								.show();
					}
				});
			}
			if (nDialog != null) {
				if (nDialog.isShowing()) {
					nDialog.dismiss();
				}
			}
		}
	}

}
