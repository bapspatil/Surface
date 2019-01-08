package com.bapspatil.surface.model.distance;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DistanceMatrixResponse{

	@SerializedName("destination_addresses")
	private List<String> destinationAddresses;

	@SerializedName("rows")
	private List<RowsItem> rows;

	@SerializedName("status")
	private String status;

	@SerializedName("origin_addresses")
	private List<String> originAddresses;

	public void setDestinationAddresses(List<String> destinationAddresses){
		this.destinationAddresses = destinationAddresses;
	}

	public List<String> getDestinationAddresses(){
		return destinationAddresses;
	}

	public void setRows(List<RowsItem> rows){
		this.rows = rows;
	}

	public List<RowsItem> getRows(){
		return rows;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setOriginAddresses(List<String> originAddresses){
		this.originAddresses = originAddresses;
	}

	public List<String> getOriginAddresses(){
		return originAddresses;
	}

	@Override
 	public String toString(){
		return 
			"DistanceMatrixResponse{" + 
			"destination_addresses = '" + destinationAddresses + '\'' + 
			",rows = '" + rows + '\'' + 
			",status = '" + status + '\'' + 
			",origin_addresses = '" + originAddresses + '\'' + 
			"}";
		}
}