package com.flutrack.app;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */

public class reportSymptoms extends Fragment {

	private Button button;
	private String[] symptoms = { "Headache", "Sore throat", "Sneezing",
			"Chills", "Runny nose", "Fever", "Dry cough" };
	private MultiSelectSpinner spinner;
	private String url = ""; //Put here the url for the submit_flu.php file
	private String TAG_SUCCESS = "success";
	private SharedPreferences sharedPref;
	private RadioGroup radioGroup;
	private RadioButton ch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_report, container, false);
		spinner = (MultiSelectSpinner) view.findViewById(R.id.my_spin);
		spinner.setItems(symptoms);
		radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
		button = (Button) view.findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				List<String> selected = spinner.getSelectedStrings();
				if (!selected.isEmpty()) {
					if (isOnline()) {
						new submitReport().execute();
					} else {
						Toast.makeText(getActivity(), "No Internet Connection",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getActivity(), "Wrong Input",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		return view;
	}

	public boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	class submitReport extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			Networking n = new Networking();
			sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
			WifiManager manager = (WifiManager) getActivity().getSystemService(
					Context.WIFI_SERVICE);
			WifiInfo info = manager.getConnectionInfo();
			int selectedId = radioGroup.getCheckedRadioButtonId();
			ch = (RadioButton) getActivity().findViewById(selectedId);
			List<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("person", ch.getText().toString()));
			args.add(new BasicNameValuePair("symptoms", spinner
					.buildSelectedItemString()));
			args.add(new BasicNameValuePair("userID", info.getMacAddress()));
			args.add(new BasicNameValuePair("latitude", sharedPref.getString(
					"latitude", null)));
			args.add(new BasicNameValuePair("longitude", sharedPref.getString(
					"longitude", null)));
			JSONObject json = n.POST(url, args);
			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getActivity(), "Report Submitted",
									Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getActivity(),
									"Something Went Wrong", Toast.LENGTH_LONG)
									.show();
						}
					});
				}
			} catch (JSONException e) {
			}
			return null;
		}
	}
}
