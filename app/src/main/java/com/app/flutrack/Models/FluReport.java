package com.app.flutrack.Models;

import com.google.gson.annotations.SerializedName;

public class FluReport {

    @SerializedName("user_id")
    private String userID;
	private double latitude;
	private double longitude;
	private String symptoms;
    @SerializedName("report_date")
    private String reportDate;

	public FluReport() {}

	public FluReport(double latitude, double longitude, String symptoms){
		this.latitude = latitude;
		this.longitude = longitude;
		this.symptoms = symptoms;
	}

	public void setUserID(String userID){
		this.userID=userID;
	}
	
	public void setLatitude(double latitude){
		this.latitude=latitude;
	}

	public void setLongitude(double longitude){
		this.longitude=longitude;
	}

	public void setSymptoms(String symptoms){
		this.symptoms=symptoms;
	}
	
	public void setDate(String created_at){
		this.reportDate=reportDate;
	}
	
	public String getUserID(){
		return this.userID;
	}
	
	public double getLatitude(){
		return this.latitude;
	}
	
	public double getLongitude(){
		return this.longitude;
	}

	public String getSymptoms(){
		return this.symptoms;
	}
	
	public String getDate(){
		return this.reportDate;
	}
	
}
