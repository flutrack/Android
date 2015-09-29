package com.app.flutrack.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Used by retrofit and gson to automatically convert Flutrack's response into a simple Java Object.
 * Each class property corresponds to a field in the JSON response.
 */
public class Flutrack{

    // Explicitly state the name of the field in the JSON when it differs from its corresponding
    // in the class.
    @SerializedName("user_name")
    private String userName;
    @SerializedName("tweet_text")
    private String tweetText;
    private String latitude;
    private String longitude;
    @SerializedName("tweet_date")
    private String tweetDate;
    private String aggravation;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setTweetDate(String tweetDate) {
        this.tweetDate = tweetDate;
    }

    public void setAggravation(String aggravation) {
        this.aggravation = aggravation;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getTweetText() {
        return this.tweetText;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public String getTweetDate() {
        return this.tweetDate;
    }

    public String getAggravation() {
        return this.aggravation;
    }
}
