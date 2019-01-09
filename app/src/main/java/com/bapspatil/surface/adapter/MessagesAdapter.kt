package com.bapspatil.surface.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bapspatil.surface.R
import com.bapspatil.surface.SurfaceApp
import com.bapspatil.surface.model.MileageMetadata
import com.bapspatil.surface.model.MileageModel
import com.bapspatil.surface.ui.MainActivity
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.layer.sdk.messaging.Message
import kotlinx.android.synthetic.main.item_message_text.view.*
import kotlinx.android.synthetic.main.item_mileage_message_view.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.toast
import java.io.InputStreamReader

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class MessagesAdapter(
    private val mContext: Context,
    private val mMessagesList: ArrayList<Message>,
    private val mOriginListener: OriginPickerListener?,
    private val mDestinationLister: DestinationPickerListener?,
    private val mSendListener: SendListener?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mName: String? = null
    private var mOrigin: String? = null
    private var mDestination: String? = null
    private var mMileageDistance: String? = null

    companion object {
        const val VIEW_TYPE_TEXT_SENT = 1
        const val VIEW_TYPE_TEXT_RECEIVED = 2
        const val VIEW_TYPE_MAPS_SENT = 3
        const val VIEW_TYPE_MAPS_RECEIVED = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_TEXT_RECEIVED -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.item_message_text, parent, false)
                view.messageBodyTextView.backgroundDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_grey)
                view.messageBodyTextView.setTextColor(ContextCompat.getColor(mContext, R.color.md_black_1000))

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.weight = 1.0f
                params.gravity = Gravity.START
                view.messageBodyTextView.layoutParams = params

                return TextMessageVH(view)
            }
            VIEW_TYPE_TEXT_SENT -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.item_message_text, parent, false)
                return TextMessageVH(view)
            }
            VIEW_TYPE_MAPS_SENT -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.item_mileage_message_view, parent, false)
                return MapsMessageVH(view)
            }
            VIEW_TYPE_MAPS_RECEIVED -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.item_mileage_message_view, parent, false)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.weight = 1.0f
                params.gravity = Gravity.START

                val holder = MapsMessageVH(view)
                holder.nameEt.isFocusable = false
                holder.nameEt.setOnClickListener { null }
                holder.startLocationEt.setOnClickListener { null }
                holder.endLocationEt.setOnClickListener { null }
                return holder
            }
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_message_text, parent, false)
        return TextMessageVH(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val setOfMsgParts = mMessagesList[position].messageParts
        val iterator = setOfMsgParts.iterator()
        while (iterator.hasNext()) {
            val messagePart = iterator.next()
            if (messagePart.mimeType != "image/jpeg") {
                when (holder.itemViewType) {
                    VIEW_TYPE_TEXT_SENT, VIEW_TYPE_TEXT_RECEIVED -> {
                        (holder as TextMessageVH).messageBodyTextView.text = String(messagePart.data)
                    }
                    VIEW_TYPE_MAPS_SENT, VIEW_TYPE_MAPS_RECEIVED -> {
                        val gson = Gson()
                        val inputStreamReader = InputStreamReader(messagePart.dataStream)
                        val reader = JsonReader(inputStreamReader)
                        val mMetadata: MileageMetadata = gson.fromJson(reader, MileageMetadata::class.java)

                        val filledMapsHolder = holder as MapsMessageVH
                        filledMapsHolder.buttonsContainer.visibility = View.GONE
                        filledMapsHolder.mileageDistanceContainer.visibility = View.VISIBLE
                        filledMapsHolder.nameEt.setText(mMetadata.name)
                        filledMapsHolder.startLocationEt.setText(mMetadata.origin)
                        filledMapsHolder.endLocationEt.setText(mMetadata.destination)
                        filledMapsHolder.mileageDistanceTextView.text = mMetadata.mileageDistance
                    }
                }
            } else {
                val emptyMapsHolder = holder as MapsMessageVH
                if (mName != null) {
                    emptyMapsHolder.nameEt.setText(mName)
                } else {
                    emptyMapsHolder.nameEt.text.clear()
                }
                if (mOrigin != null) {
                    emptyMapsHolder.startLocationEt.setText(mOrigin)
                } else {
                    emptyMapsHolder.startLocationEt.text.clear()
                }
                if (mDestination != null) {
                    emptyMapsHolder.endLocationEt.setText(mDestination)
                } else {
                    emptyMapsHolder.endLocationEt.text.clear()
                }
                if (mMileageDistance != null) {
                    emptyMapsHolder.buttonsContainer.visibility = View.GONE
                    emptyMapsHolder.mileageDistanceContainer.visibility = View.VISIBLE
                    emptyMapsHolder.mileageDistanceTextView.text = mMileageDistance
                } else {
                    emptyMapsHolder.buttonsContainer.visibility = View.VISIBLE
                    emptyMapsHolder.mileageDistanceContainer.visibility = View.GONE
                    emptyMapsHolder.mileageDistanceTextView.text = ""
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val iterator = mMessagesList[position].messageParts.iterator()
        if (iterator.hasNext()) {
            val mimeType = iterator.next().mimeType
            when (mimeType) {
                "text/plain" -> {
                    if (mMessagesList[position].sender != null && mMessagesList[position].sender!!.userId != SurfaceApp.layerClient?.authenticatedUser!!.userId) {
                        return VIEW_TYPE_TEXT_RECEIVED
                    } else {
                        return VIEW_TYPE_TEXT_SENT
                    }
                }
                MileageModel.MIME_TYPE -> {
                    if (mMessagesList[position].sender != null && mMessagesList[position].sender!!.userId != SurfaceApp.layerClient?.authenticatedUser!!.userId) {
                        return VIEW_TYPE_MAPS_RECEIVED
                    } else {
                        return VIEW_TYPE_MAPS_SENT
                    }
                }
                "image/jpeg" -> {
                    return VIEW_TYPE_MAPS_SENT
                }
            }
        }
        return VIEW_TYPE_MAPS_RECEIVED
    }

    override fun getItemCount(): Int {
        return if (mMessagesList != null)
            mMessagesList.size
        else
            0
    }

    inner class TextMessageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageBodyTextView: TextView = itemView.messageBodyTextView
    }

    inner class MapsMessageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameEt: EditText = itemView.nameEt
        val startLocationEt: EditText = itemView.startLocationEt
        val endLocationEt: EditText = itemView.endLocationEt
        val cancelBtn: Button = itemView.cancelBtn
        val sendBtn: Button = itemView.sendBtn
        val mileageDistanceTextView: TextView = itemView.mileageDistanceTextView
        val buttonsContainer: LinearLayout = itemView.buttonsContainer
        val mileageDistanceContainer: RelativeLayout = itemView.mileageDistanceContainer

        init {
            startLocationEt.setOnClickListener {
                mOriginListener?.pickOrigin()
            }
            endLocationEt.setOnClickListener {
                mDestinationLister?.pickDestination()
            }
            cancelBtn.setOnClickListener {
                removeItemAt(0)
            }
            sendBtn.setOnClickListener {
                if (mName != null && mOrigin != null && mDestination != null) {
                    mSendListener?.sendDetailsForMileage(mName, mOrigin, mDestination)
                } else {
                    mContext.toast("Please fill out all the details.")
                }
            }
            nameEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mName = s.toString()
                }
            })
            if (mOrigin != null) {
                startLocationEt.setText(mOrigin)
            }
        }
    }

    fun clearItems() {
        mMessagesList.clear()
    }

    fun removeItemAt(position: Int) {
        mMessagesList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItems(messagesList: ArrayList<Message>) {
        mMessagesList.addAll(messagesList)
        notifyDataSetChanged()
    }

    fun addItem(message: Message) {
        mMessagesList.add(0, message)
        Log.d("LAYER_MESSAGE_SENT", message.messageParts.toString())
        notifyItemInserted(0)
    }

    fun addEmptyMapsItem(message: Message) {
        mMessagesList.add(0, message)
        notifyDataSetChanged()
    }

    fun notifyOriginSelected(origin: String) {
        mOrigin = origin
        removeItemAt(0)
        val message = MainActivity.createDummyMapsMessage(mContext)
        addItem(message!!)
    }

    fun notifyDestinationSelected(destination: String) {
        mDestination = destination
        removeItemAt(0)
        val message = MainActivity.createDummyMapsMessage(mContext)
        addItem(message!!)
    }

    fun notifyDistanceCalculated(distance: String?) {
        if (distance != null) mMileageDistance = distance
        removeItemAt(0)
        val message = MainActivity.createDummyMapsMessage(mContext)
        addItem(message!!)
    }

    fun clearAllFormFields() {
        mName = null
        mOrigin = null
        mDestination = null
        mMileageDistance = null
    }

    interface OriginPickerListener {
        fun pickOrigin()
    }

    interface DestinationPickerListener {
        fun pickDestination()
    }

    interface SendListener {
        fun sendDetailsForMileage(name: String?, origin: String?, destination: String?)
    }
}