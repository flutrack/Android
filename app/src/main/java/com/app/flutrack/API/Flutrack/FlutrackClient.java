package com.app.flutrack.API.Flutrack;


import retrofit.RestAdapter;

public class FlutrackClient {

    private static FlutrackApiInterface sFlutrackService;

    public static FlutrackApiInterface getFlutrackApiClient() {
        if (sFlutrackService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://api.flutrack.org")
                    .setLogLevel(RestAdapter.LogLevel.FULL) // this is the important line
                    .build();
            sFlutrackService = restAdapter.create(FlutrackApiInterface.class);
        }

        return sFlutrackService;
    }

}
