package com.bapspatil.surface.sync;

import android.content.Context;
import com.bapspatil.surface.model.MileageMetadata;
import com.bapspatil.surface.model.MileageModel;
import com.google.gson.Gson;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.message.sender.MessageSender;

/*
 ** Created by Bapusaheb Patil {@link https://bapspatil.com}
 */

public class MileageSender extends MessageSender {
    private Gson mGson;

    public MileageSender(Context context, LayerClient layerClient, Gson gson) {
        super(context, layerClient);
        mGson = gson;
    }

    public boolean requestSend(String name, String origin, String destination, String mileageDistance) {
        // Get a MIME type with role = root
        String mimeType = MessagePartUtils.getAsRoleRoot(MileageModel.MIME_TYPE);

        // Create and populate the metadata object
        MileageMetadata metadata = new MileageMetadata();
        // This could also be always set to the current authenticated user's display name
        metadata.name = name;
        metadata.origin = origin;
        metadata.destination = destination;
        metadata.mileageDistance = mileageDistance;
        // Convert the metadata to a JSON string and get the byte array
        byte[] bytes = mGson.toJson(metadata).getBytes();
        // Create the message part
        MessagePart root = getLayerClient().newMessagePart(mimeType, bytes);
        // Create the message
        Message message = getLayerClient().newMessage(root);
        // Send the message
        return send(message);
    }
}
