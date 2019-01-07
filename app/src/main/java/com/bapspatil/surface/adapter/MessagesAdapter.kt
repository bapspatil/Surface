package com.bapspatil.surface.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.bapspatil.surface.R
import com.bapspatil.surface.SurfaceApp
import com.layer.sdk.messaging.Message
import kotlinx.android.synthetic.main.item_message_text.view.*

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class MessagesAdapter(private val mContext: Context, private val mMessagesList: ArrayList<Message>) :
    RecyclerView.Adapter<MessagesAdapter.MessageVH>() {

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageVH {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_message_text, parent, false)
        if (viewType == VIEW_TYPE_RECEIVED) {
            view.messageBodyTextView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.md_grey_300))
            view.messageBodyTextView.setTextColor(ContextCompat.getColor(mContext, R.color.md_black_1000))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.weight = 1.0f
            params.gravity = Gravity.START
            view.messageBodyTextView.layoutParams = params
        }
        return MessageVH(view)
    }

    override fun onBindViewHolder(holder: MessageVH, position: Int) {
        val setOfMsgParts = mMessagesList[position].messageParts
        val iterator = setOfMsgParts.iterator()
        while (iterator.hasNext()) {
            val messagePart = iterator.next()
            holder.messageBodyTextView.text = String(messagePart.data)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mMessagesList[position].sender!!.userId != SurfaceApp.layerClient?.authenticatedUser!!.userId) {
            VIEW_TYPE_RECEIVED
        } else {
            VIEW_TYPE_SENT
        }
    }

    override fun getItemCount(): Int {
        return if (mMessagesList != null)
            mMessagesList.size
        else
            0
    }

    inner class MessageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageBodyTextView: TextView = itemView.messageBodyTextView
    }

    fun clearItems() {
        mMessagesList.clear()
    }

    fun addItems(messagesList: ArrayList<Message>) {
        mMessagesList.addAll(messagesList)
        notifyDataSetChanged()
    }

    fun addItem(message: Message) {
        mMessagesList.add(0, message)
        Log.d("LAYER_MESSAGE_SENT", message.messageParts.toString())
        notifyDataSetChanged()
    }
}