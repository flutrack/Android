package com.flutrack.app;

import android.content.Context;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


/*
 * 
 * 
 * 
 * 
 * 
 * 
 */

public class MainActivity extends FragmentActivity {
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RelativeLayout Layout1 = (RelativeLayout) findViewById(R.id.layout1);
		LinearLayout Layout2 = (LinearLayout) findViewById(R.id.layout2);
		if ((!isOnline() && !isLocEnabled()) || !isOnline() || !isLocEnabled()) {
			Layout1.setVisibility(View.VISIBLE);
			Layout2.setVisibility(View.GONE);
			button = (Button) findViewById(R.id.retry_btn);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (isOnline() && isLocEnabled()) {
						finish();
						startActivity(getIntent());
					} else if (isOnline() && !isLocEnabled()) {
						Toast.makeText(getApplicationContext(),
								"Location Services are Disabled",
								Toast.LENGTH_SHORT).show();
					} else if (!isOnline() && !isLocEnabled()) {
						Toast.makeText(getApplicationContext(),
								"Internet and Location Services Not Available",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(),
								"No Internet Connection", Toast.LENGTH_SHORT)
								.show();
					}
				}
			});
		} else {
			Layout1.setVisibility(View.GONE);
			Layout2.setVisibility(View.VISIBLE);
			ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
			viewPager.setAdapter(new SampleFragmentPagerAdapter(
					getSupportFragmentManager(), MainActivity.this));
			SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
			slidingTabLayout.setViewPager(viewPager);
			slidingTabLayout.setBackgroundColor(Color.BLACK);
			slidingTabLayout.setSelectedIndicatorColors(Color.CYAN);
		}
	}

	public boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	public boolean isLocEnabled() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

}