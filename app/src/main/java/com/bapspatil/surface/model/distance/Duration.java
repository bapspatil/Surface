package com.bapspatil.surface.model.distance;

import com.google.gson.annotations.SerializedName;

public class Duration{

	@SerializedName("text")
	private String text;

	@SerializedName("value")
	private int value;

	public void setText(String text){
		this.text = text;
	}

	public String getText(){
		return text;
	}

	public void setValue(int value){
		this.value = value;
	}

	public int getValue(){
		return value;
	}

	@Override
 	public String toString(){
		return 
			"Duration{" + 
			"text = '" + text + '\'' + 
			",value = '" + value + '\'' + 
			"}";
		}
}