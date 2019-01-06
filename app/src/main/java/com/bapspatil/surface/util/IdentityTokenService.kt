package com.bapspatil.surface.util

import com.bapspatil.surface.model.IWSResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

interface IdentityTokenService {

    @FormUrlEncoded
    @POST("authenticate")
    fun getIdentityToken(@Field("email") EMAIL: String?, @Field("password") PASSWORD: String?, @Field("nonce") NONCE: String?): Call<IWSResponse>

    companion object {
        const val BASE_URL = "https://surface-baps.herokuapp.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
}