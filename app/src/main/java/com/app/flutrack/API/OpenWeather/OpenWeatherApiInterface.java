package com.app.flutrack.API.OpenWeather;

import com.app.flutrack.Models.WeeklyWeatherForecast;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;


public interface OpenWeatherApiInterface {

    @GET("/forecast/daily?units=metric&cnt=7")
    void getOpenWeatherData(@Query("lat") double latitude, @Query("lon") double longitude, Callback<WeeklyWeatherForecast> response);
}
