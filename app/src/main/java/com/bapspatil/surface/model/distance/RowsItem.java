package com.bapspatil.surface.model.distance;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RowsItem{

	@SerializedName("elements")
	private List<ElementsItem> elements;

	public void setElements(List<ElementsItem> elements){
		this.elements = elements;
	}

	public List<ElementsItem> getElements(){
		return elements;
	}

	@Override
 	public String toString(){
		return 
			"RowsItem{" + 
			"elements = '" + elements + '\'' + 
			"}";
		}
}