package com.app.flutrack.API.Flutrack;


import retrofit.RestAdapter;

public class FlutrackClient {

    private static FlutrackApiInterface sFlutrackService;

    public static FlutrackApiInterface getFlutrackApiClient() {
        if (sFlutrackService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://api.flutrack.org")
                    .build();
            sFlutrackService = restAdapter.create(FlutrackApiInterface.class);
        }

        return sFlutrackService;
    }

}
