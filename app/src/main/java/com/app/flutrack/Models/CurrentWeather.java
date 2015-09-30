package com.app.flutrack.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Each class property corresponds to a field in OpenWeather's JSON response.
 */
public class CurrentWeather {
    @SerializedName("name")
    private String locationName;
    @SerializedName("dt")
    private long timestamp;
    private Main main;
    private ArrayList<Weather> weather;

    public Main getMain() { return this.main; }

    public ArrayList<Weather> getWeather() { return this.weather; }

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
        private float temperature;
        @SerializedName("temp_min")
        public float minTemperature;
        @SerializedName("temp_max")
        private float maxTemperature;
        private float pressure;
        private float humidity;
        private String description;

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

        public String getDescription() { return this.description; }

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

        public void setDescription(String description) { this.description = description; }
    }

    public class Weather {
        private int id;
        private String main;
        private String description;

        public int getID(){ return this.id; }

        public String getMain() { return this.main; }

        public String getDescription() { return this.description; }

        public void setID(int id) { this.id = id; }

        public void setMain(String main) { this.main = main; }

        public void setDescription(String description) { this.description = description; }
    }

}