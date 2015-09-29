package com.app.flutrack.API.Flutrack;

import com.app.flutrack.Models.FluReport;
import com.app.flutrack.Models.Flutrack;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * This class is used for defining the API endpoints along with the parameters and the HTTP methods
 * that will be used throughout the app to acquire or send specific data.
 */
public interface FlutrackApiInterface {

    /**
     * Downloads data by using the API's time parameter i.e. returns results that lie within
     * a specific number of days.
     */
    @GET("/")
    void getTimeData(@Query("time") String days, Callback<ArrayList<Flutrack>> response);

    @POST("/")
    void postFluReport(@Body FluReport fluReport, Callback<FluReport> response);
}
