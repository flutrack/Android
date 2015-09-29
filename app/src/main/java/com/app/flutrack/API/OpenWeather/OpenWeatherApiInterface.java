package com.app.flutrack.API.OpenWeather;

import com.app.flutrack.Models.CurrentWeather;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;


public interface OpenWeatherApiInterface {

    @GET("/weather?units=metric")
    void getOpenWeatherData(@Query("lat") double latitude, @Query("lon") double longitude, Callback<CurrentWeather> response);
}
