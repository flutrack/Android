package com.flutrack.app;

public class Tweet {
	
private String user_name; 
private String tweet_text;
private String latitude;
private String longitude;
private String tweet_date;
private String aggravation;

public Tweet(String name,String text,String lat,String lon,String date,String aggra){ //for debugging
	this.user_name=name;
	this.tweet_text=text;
	this.latitude=lat;
	this.longitude=lon;
	this.tweet_date=aggra;
	this.aggravation=date;
}

public Tweet(){}

public void setName(String name){
	this.user_name=name;
}

public void setText(String text){
	this.tweet_text=text;
}

public void setLat(String lat){
	this.latitude=lat;
}

public void setLon(String lon){
	this.longitude=lon;
}

public void setDate(String date){
	this.tweet_date=date;
}

public void setAggra(String aggra){
	this.aggravation=aggra;
}

public String getName(){
	return this.user_name;
}

public String getText(){
	return this.tweet_text;
}

public String getLat(){
	return this.latitude;
}

public String getLon(){
	return this.longitude;
}

public String getDate(){
	return this.tweet_date;
}

public String getAggra(){
	return this.aggravation;
}

}