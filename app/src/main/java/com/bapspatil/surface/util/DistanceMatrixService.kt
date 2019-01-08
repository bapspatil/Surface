package com.bapspatil.surface.util

import com.bapspatil.surface.model.distance.DistanceMatrixResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

interface DistanceMatrixService {

    @GET("distancematrix/json")
    fun getDistanceMatrix(@Query("origins") ORIGINS: String?, @Query("destinations") DESTINATIONS: String?, @Query("key") KEY: String?): Call<DistanceMatrixResponse>

    companion object {
        const val BASE_URL = "https://maps.googleapis.com/maps/api/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
}