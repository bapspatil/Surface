package com.bapspatil.surface

import android.app.Application
import android.util.Log
import com.layer.sdk.LayerClient
import com.layer.sdk.exceptions.LayerException
import com.layer.sdk.listeners.LayerConnectionListener

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class SurfaceApp : Application(), LayerConnectionListener {

    companion object {
        var layerClient: LayerClient? = null
    }

    override fun onCreate() {
        super.onCreate()

        layerClient = LayerClient.newInstance(this, BuildConfig.LAYER_APP_ID, LayerClient.Options().useFirebaseCloudMessaging(true))

        LayerClient.applicationCreated(this)

        layerClient?.registerConnectionListener(this)

        if(!layerClient?.isConnected!!)
            layerClient?.connect()

    }

    override fun onConnectionConnected(layerClient: LayerClient?) {
        Log.d("LAYER_CONNECTED", "Connected successfully.")
    }

    override fun onConnectionError(layerClient: LayerClient?, e: LayerException?) {
        Log.d("LAYER_CONNECTION_ERROR", e.toString())
    }

    override fun onConnectionDisconnected(layerClient: LayerClient?) {
        Log.d("LAYER_DISCONNECT", "Disconnected successfully.")
    }
}