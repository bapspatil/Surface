package com.bapspatil.surface

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log
import com.layer.sdk.LayerClient
import com.layer.sdk.exceptions.LayerException
import com.layer.sdk.listeners.LayerConnectionListener
import com.layer.xdk.ui.ServiceLocator

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class SurfaceApp : Application(), LayerConnectionListener {

    companion object {
        var layerClient: LayerClient? = null
        var serviceLocator: ServiceLocator? = null
    }

    override fun onCreate() {
        super.onCreate()

        val options = LayerClient.Options()
        options.historicSyncPolicy(LayerClient.Options.HistoricSyncPolicy.ALL_MESSAGES)
        options.useFirebaseCloudMessaging(true)
        layerClient = LayerClient.newInstance(this, BuildConfig.LAYER_APP_ID, options)

        LayerClient.applicationCreated(this)
        serviceLocator = ServiceLocator()
        serviceLocator?.setAppContext(this)
        serviceLocator?.layerClient = layerClient

        layerClient?.registerConnectionListener(this)

        if(!layerClient?.isConnected!!)
            layerClient?.connect()

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
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