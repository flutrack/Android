package com.app.flutrack.API.OpenWeather;


import retrofit.RestAdapter;

public class OpenWeatherClient {

    private static OpenWeatherApiInterface sOpenWeatherService;

    public static OpenWeatherApiInterface getOpenWeatherApiClient() {
        if (sOpenWeatherService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://api.openweathermap.org/data/2.5")
                    .build();
            sOpenWeatherService = restAdapter.create(OpenWeatherApiInterface.class);
        }
        return sOpenWeatherService;
    }
}
