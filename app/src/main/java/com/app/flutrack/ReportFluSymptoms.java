package com.app.flutrack;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.flutrack.API.Flutrack.FlutrackClient;
import com.app.flutrack.Models.FluReport;

import io.nlopez.smartlocation.SmartLocation;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Class that is responsible for creating and posting the user-generated flu reports.
 */
public class ReportFluSymptoms extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        view.findViewById(R.id.multiChoice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultipleChoice();
            }
        });
        return view;
    }

    /**
     * Creates a multi-choice list dialog that displays symptoms associated with the flu.
     */
    private void showMultipleChoice() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.materialDialogTitle)
                .items(R.array.fluSymptoms)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        StringBuilder str = new StringBuilder();
                        Location lastLocation = SmartLocation.with(getActivity()).location().getLastLocation();

                        for (int i = 0; i < which.length; i++) {
                            if (i > 0) {
                                str.append(',');
                                str.append(' ');
                            }
                            str.append(text[i]);
                        }
                        if (lastLocation != null) {
                            // postFluReport((str.toString()),lastLocation.getLatitude(),
                            //  lastLocation.getLongitude());
                        } else {
                            showToast("Location not available for Report submission");
                        }
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }

    /**
     * Sends the user's report back to a pre-defined endpoint.
     */
    private void postFluReport(String fluSymptoms, double latitude, double longitude) {
        FluReport fluReport = new FluReport(latitude, longitude, fluSymptoms);

        FlutrackClient.
                getFlutrackApiClient().
                postFluReport(fluReport, new Callback<FluReport>() {
                    @Override
                    public void success(FluReport result, Response response) {
                        showToast("Submitted Report");
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        showToast(retrofitError.getMessage());
                    }
                });

    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}