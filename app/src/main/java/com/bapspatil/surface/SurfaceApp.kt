package com.bapspatil.surface

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log
import com.bapspatil.surface.model.MileageModel
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import com.layer.sdk.LayerClient
import com.layer.sdk.exceptions.LayerException
import com.layer.sdk.listeners.LayerConnectionListener
import com.layer.xdk.ui.ServiceLocator
import com.layer.xdk.ui.message.model.MessageModelManager

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class SurfaceApp : Application(), LayerConnectionListener {

    companion object {
        var mContext: Context? = null
        var layerClient: LayerClient? = null
        var serviceLocator: ServiceLocator? = null
        var messageModelManager: MessageModelManager? = null
        var geoDataClient: GeoDataClient? = null
        var placeDetectionClient: PlaceDetectionClient? = null
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this

        geoDataClient = Places.getGeoDataClient(this)
        placeDetectionClient = Places.getPlaceDetectionClient(this)

        val options = LayerClient.Options()
        options.historicSyncPolicy(LayerClient.Options.HistoricSyncPolicy.ALL_MESSAGES)
        options.useFirebaseCloudMessaging(true)
        layerClient = LayerClient.newInstance(this, BuildConfig.LAYER_APP_ID, options)
        LayerClient.applicationCreated(this)

        messageModelManager?.registerModel(MileageModel.MIME_TYPE, MileageModel::class.java)

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