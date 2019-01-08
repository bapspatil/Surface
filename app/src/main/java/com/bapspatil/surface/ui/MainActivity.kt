package com.bapspatil.surface.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import com.bapspatil.surface.BuildConfig
import com.bapspatil.surface.R
import com.bapspatil.surface.SurfaceApp
import com.bapspatil.surface.adapter.MessagesAdapter
import com.bapspatil.surface.databinding.ActivityMainBinding
import com.bapspatil.surface.model.MileageModel
import com.bapspatil.surface.model.MileageModel.PLACE_PICKER_DESTINATION
import com.bapspatil.surface.model.MileageModel.PLACE_PICKER_ORIGIN
import com.bapspatil.surface.model.distance.DistanceMatrixResponse
import com.bapspatil.surface.sync.MileageSender
import com.bapspatil.surface.util.Constants
import com.bapspatil.surface.util.DistanceMatrixService
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.gson.Gson
import com.layer.sdk.changes.LayerChangeEvent
import com.layer.sdk.exceptions.LayerConversationException
import com.layer.sdk.listeners.LayerChangeEventListener
import com.layer.sdk.messaging.Conversation
import com.layer.sdk.messaging.Identity
import com.layer.sdk.messaging.LayerObject
import com.layer.sdk.messaging.Message
import com.layer.sdk.query.Predicate
import com.layer.sdk.query.Query
import com.layer.sdk.query.SortDescriptor
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*


/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class MainActivity : AppCompatActivity(), LayerChangeEventListener {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private var mName: String? = null
    private var mOrigin: String? = null
    private var mDestination: String? = null
    private var mMileageDistance: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        SurfaceApp.layerClient?.registerEventListener(this)

        val currentUser = SurfaceApp.layerClient?.authenticatedUser
        Log.d("CURRENT_USER", currentUser.toString())
        val query: Query<Identity>
        if (currentUser?.userId == Constants.USER_ID_BAPUSAHEB) {
            SurfaceApp.layerClient?.follow(Constants.USER_ID_PRATEEK)
            query = Query.builder(Identity::class.java)
                .predicate(Predicate(Identity.Property.DISPLAY_NAME, Predicate.Operator.LIKE, "hack%"))
                .build()
        } else {
            SurfaceApp.layerClient?.follow(Constants.USER_ID_BAPUSAHEB)
            query = Query.builder(Identity::class.java)
                .predicate(Predicate(Identity.Property.FIRST_NAME, Predicate.Operator.EQUAL_TO, "Bapusaheb"))
                .build()
        }

        Log.d("QUERY", query.toString())
        val identities: List<Identity> =
            SurfaceApp.layerClient?.executeQuery(query, Query.ResultType.OBJECTS) as List<Identity>

        Log.d("IDENTITIES", identities.toString())

        var convo: Conversation? = null
        for (identity in identities) {
            if (identity.emailAddress != "hi@bapspatil") {
                Log.d("IDENTITY", identity.toString())
                convo = try {
                    SurfaceApp.layerClient?.newConversation(identity)!!
                } catch (e: LayerConversationException) {
                    e.conversation
                }
                Log.d("CONVO", convo.toString())
            }
        }

        val messagesQuery = Query.builder(Message::class.java)
            .predicate(Predicate(Message.Property.CONVERSATION, Predicate.Operator.EQUAL_TO, convo))
            .sortDescriptor(SortDescriptor(Message.Property.SENT_AT, SortDescriptor.Order.DESCENDING))
            .limit(20)
            .build()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        messagesRv.layoutManager = layoutManager
        messagesAdapter = MessagesAdapter(
            this,
            SurfaceApp.layerClient?.executeQuery(messagesQuery, Query.ResultType.OBJECTS) as ArrayList<Message>
            , object : MessagesAdapter.OriginPickerListener {
                override fun pickOrigin() {
                    val builder = PlacePicker.IntentBuilder()
                    try {
                        startActivityForResult(builder.build(this@MainActivity), PLACE_PICKER_ORIGIN)
                    } catch (e: GooglePlayServicesRepairableException) {
                        e.printStackTrace()
                    } catch (e: GooglePlayServicesNotAvailableException) {
                        e.printStackTrace()
                    }
                }
            }, object : MessagesAdapter.DestinationPickerListener {
                override fun pickDestination() {
                    val builder = PlacePicker.IntentBuilder()
                    try {
                        startActivityForResult(builder.build(this@MainActivity), PLACE_PICKER_DESTINATION)
                    } catch (e: GooglePlayServicesRepairableException) {
                        e.printStackTrace()
                    } catch (e: GooglePlayServicesNotAvailableException) {
                        e.printStackTrace()
                    }
                }
            }, object : MessagesAdapter.SendListener {
                override fun sendDetailsForMileage(name: String?, origin: String?, destination: String?) {
                    mName = name
                    mOrigin = origin
                    mDestination = destination

                    val distanceMatrixApi = DistanceMatrixService.retrofit.create(DistanceMatrixService::class.java)
                    val distanceMatrixCall =
                        distanceMatrixApi.getDistanceMatrix(origin, destination, "imperial", BuildConfig.GOOGLE_MAPS_API_KEY)
                    Log.e("DISTANCE_CALL", distanceMatrixCall.toString())
                    distanceMatrixCall.enqueue(object : Callback<DistanceMatrixResponse> {
                        override fun onFailure(call: Call<DistanceMatrixResponse>, t: Throwable) {}

                        override fun onResponse(
                            call: Call<DistanceMatrixResponse>,
                            response: Response<DistanceMatrixResponse>
                        ) {
                            if (response != null) {
                                mMileageDistance = response.body()?.rows?.get(0)?.elements?.get(0)?.distance?.text
                                messagesAdapter.notifyDistanceCalculated(mMileageDistance)
                                if(mName != null && mOrigin != null && mDestination != null && mMileageDistance != null) {
                                    val mileageSender = MileageSender(this@MainActivity, SurfaceApp.layerClient, Gson())
                                    mileageSender.setConversation(convo)
                                    mileageSender.requestSend(mName, mOrigin, mDestination, mMileageDistance)
                                }
                            }
                        }
                    })

                }
            }
        )
        messagesRv.adapter = messagesAdapter
        messagesRv.scrollToPosition(0)

        mapButton.setOnClickListener {
            messagesAdapter.clearAllFormFields()
            val message = createDummyMapsMessage(applicationContext)
            messagesAdapter.addItem(message!!)
            messagesRv.scrollToPosition(0)
        }

        sendFab.setOnClickListener {
            if (!TextUtils.isEmpty(messageEt.text.toString())) {
                val messagePart = SurfaceApp.layerClient?.newMessagePart(messageEt.text.toString())
                val message = SurfaceApp.layerClient?.newMessage(Collections.singleton(messagePart))

                convo?.send(message)
                messagesAdapter.addItem(message!!)
                messagesRv.scrollToPosition(0)
                messageEt.clearComposingText()
            }
        }
    }

    companion object {
        fun createDummyMapsMessage(context: Context): Message? {
            val imageBitmap =
                BitmapFactory.decodeResource(context.resources, R.drawable.ic_add_circle_light_blue_700_24dp)
            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val imageData = stream.toByteArray()
            val messagePart = SurfaceApp.layerClient?.newMessagePart("image/jpeg", imageData)
            val message = SurfaceApp.layerClient?.newMessage(Collections.singleton(messagePart))
            return message
        }
    }


    override fun onChangeEvent(event: LayerChangeEvent?) {
        Log.d("LAYER_CHANGE_EVENT", event.toString())
        val changes = event?.changes
        if (changes != null) {
            for (change in changes) {
                when (change.objectType) {
                    LayerObject.Type.MESSAGE -> {
                        val message = change.`object` as Message
                        if (message.sender?.userId != SurfaceApp.layerClient?.authenticatedUser!!.userId) {
                            messagesAdapter.addItem(message)
                        }
                        messagesRv.scrollToPosition(0)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            MileageModel.PLACE_PICKER_ORIGIN -> {
                if (resultCode === Activity.RESULT_OK) {
                    val place = PlacePicker.getPlace(this, data)
                    messagesAdapter.notifyOriginSelected(place.address.toString())
                }
            }
            MileageModel.PLACE_PICKER_DESTINATION -> {
                if (resultCode === Activity.RESULT_OK) {
                    val place = PlacePicker.getPlace(this, data)
                    messagesAdapter.notifyDestinationSelected(place.address.toString())
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}
