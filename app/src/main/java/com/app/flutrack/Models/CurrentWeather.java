package com.app.flutrack.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Each class property corresponds to a field in OpenWeather's JSON response.
 */
public class CurrentWeather {
    @SerializedName("name")
    public String locationName;
    @SerializedName("dt")
    public long timestamp;
    public Main main;

    public String getLocationName() {
        return this.locationName;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public class Main {

        @SerializedName("temp")
        public float temperature;
        @SerializedName("temp_min")
        public float minTemperature;
        @SerializedName("temp_max")
        public float maxTemperature;
        public float pressure;
        public float humidity;

        public float getHumidity() {
            return this.humidity;
        }

        public float getPressure() {
            return this.pressure;
        }

        public float getTemperature() {
            return this.temperature;
        }

        public float getMinTemperature() {
            return this.minTemperature;
        }

        public float getMaxTemperature() {
            return this.maxTemperature;
        }

        public void setHumidity(float humidity) {
            this.humidity = humidity;
        }

        public void setPressure(float pressure) {
            this.pressure = pressure;
        }

        public void setTemperature(float temperature) {
            this.temperature = temperature;
        }

        public void setMinTemperature(float minTemperature) {
            this.minTemperature = minTemperature;
        }

        public void setMaxTemperature(float maxTemperature) {
            this.maxTemperature = maxTemperature;
        }
    }

}