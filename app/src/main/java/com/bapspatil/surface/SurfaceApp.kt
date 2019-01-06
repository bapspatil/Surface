package com.bapspatil.surface

import android.app.Application
import android.util.Log
import com.bapspatil.surface.model.IWSResponse
import com.bapspatil.surface.util.IdentityTokenService
import com.layer.sdk.LayerClient
import com.layer.sdk.exceptions.LayerException
import com.layer.sdk.listeners.LayerAuthenticationListener
import com.layer.sdk.listeners.LayerConnectionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class SurfaceApp : Application(), LayerConnectionListener, LayerAuthenticationListener {

    private lateinit var layerClient: LayerClient

    override fun onCreate() {
        super.onCreate()

        layerClient = LayerClient.newInstance(this, BuildConfig.LAYER_APP_ID, LayerClient.Options().useFirebaseCloudMessaging(true))

        LayerClient.applicationCreated(this)

        layerClient.registerConnectionListener(this)
        layerClient.registerAuthenticationListener(this)

        if(!layerClient.isConnected)
            layerClient.connect()
    }

    override fun onConnectionConnected(p0: LayerClient?) {
        layerClient.authenticate()
    }

    override fun onConnectionError(p0: LayerClient?, p1: LayerException?) {
    }

    override fun onConnectionDisconnected(p0: LayerClient?) {
    }

    override fun onAuthenticated(layerClient: LayerClient?, userId: String?) {
        Log.e("LAYER_AUTHENTICATED", "User authenticated: $userId")
    }

    override fun onDeauthenticated(p0: LayerClient?) {
    }

    override fun onAuthenticationError(layerClient: LayerClient?, exception: LayerException?) {
    }

    override fun onAuthenticationChallenge(layerClient: LayerClient?, nonce: String?) {
        lateinit var identityToken: String
        val iwsService = IdentityTokenService.retrofit.create(IdentityTokenService::class.java)
        val iwsCall = iwsService.getIdentityToken("a157b7be-1cdd-40fa-bcb2-9166f0a8fe7a", nonce)
        iwsCall.enqueue(object : Callback<IWSResponse> {
            override fun onFailure(call: Call<IWSResponse>, t: Throwable) {
                // Do nothing here
            }

            override fun onResponse(call: Call<IWSResponse>, response: Response<IWSResponse>) {
                if(response.body() != null) {
                    identityToken = response.body()?.identityToken!!
                    layerClient?.answerAuthenticationChallenge(identityToken)
                }
            }
        })
        Log.d("LAYER_NONCE", nonce)
    }

    fun getLayerClient() = layerClient
}