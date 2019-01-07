package com.bapspatil.surface.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import com.bapspatil.surface.R
import com.bapspatil.surface.SurfaceApp
import com.bapspatil.surface.adapter.MessagesAdapter
import com.bapspatil.surface.databinding.ActivityMainBinding
import com.bapspatil.surface.util.Constants
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
import java.util.*

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class MainActivity : AppCompatActivity(), LayerChangeEventListener {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var messagesAdapter: MessagesAdapter

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
        val identities: List<Identity> = SurfaceApp.layerClient?.executeQuery(query, Query.ResultType.OBJECTS) as List<Identity>

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
        )
        messagesRv.adapter = messagesAdapter
        messagesRv.scrollToPosition(0)

        sendFab.setOnClickListener {
            if (!TextUtils.isEmpty(messageEt.text.toString())) {
                val messagePart = SurfaceApp.layerClient?.newMessagePart(messageEt.text.toString())
                val message = SurfaceApp.layerClient?.newMessage(Collections.singleton(messagePart))
                convo?.send(message)
                messagesAdapter.addItem(message!!)
                messagesRv.scrollToPosition(0)
            }
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
}
