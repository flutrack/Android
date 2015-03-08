package com.flutrack.app;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;



public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "GOOGLE MAP", "REPORT SYMPTOMS"};
    private Context context;

    public SampleFragmentPagerAdapter(android.support.v4.app.FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
        case 0:
            return new fluMap();
        case 1:
            return new reportSymptoms();
         default:
        	return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}