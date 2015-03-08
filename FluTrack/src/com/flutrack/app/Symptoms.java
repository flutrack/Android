package com.flutrack.app;

public class Symptoms {
	private String pid; 
	private String person;
	private String userID;
	private String latitude;
	private String longitude;
	private String symptoms;
	private String created_at;
	
	public Symptoms(String pid,String person,String symptoms,String userID,String latitude,String longitude,String created_at){ //for debugging
		this.pid=pid;
		this.person=person;
		this.symptoms=symptoms;
		this.userID=userID;
		this.latitude=latitude;
		this.longitude=longitude;
		this.created_at=created_at;
	}
	
	public Symptoms(){}
	
	public void setPid(String pid){
		this.pid=pid;
	}
	
	public void setUserID(String userID){
		this.userID=userID;
	}
	
	public void setLatitude(String latitude){
		this.latitude=latitude;
	}
	public void setLongitude(String longitude){
		this.longitude=longitude;
	}
	
	public void setPerson(String person){
		this.person=person;
	}
	
	public void setSymptoms(String symptoms){
		this.symptoms=symptoms;
	}
	
	public void setDate(String created_at){
		this.created_at=created_at;
	}
	
	public String getUserID(){
		return this.userID;
	}
	
	public String getLatitude(){
		return this.latitude;
	}
	
	public String getLongitude(){
		return this.longitude;
	}
	
	public String getPid(){
		return this.pid;
	}
	
	public String getPerson(){
		return this.person;
	}
	
	public String getSymptoms(){
		return this.symptoms;
	}
	
	public String getDate(){
		return this.created_at;
	}
	
}
