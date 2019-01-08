package com.bapspatil.surface.model.distance;

import com.google.gson.annotations.SerializedName;

public class ElementsItem{

	@SerializedName("duration")
	private Duration duration;

	@SerializedName("distance")
	private Distance distance;

	@SerializedName("status")
	private String status;

	public void setDuration(Duration duration){
		this.duration = duration;
	}

	public Duration getDuration(){
		return duration;
	}

	public void setDistance(Distance distance){
		this.distance = distance;
	}

	public Distance getDistance(){
		return distance;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"ElementsItem{" + 
			"duration = '" + duration + '\'' + 
			",distance = '" + distance + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}