package com.bapspatil.surface.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.bapspatil.surface.R;
import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.message.model.MessageModel;

import java.io.IOException;
import java.io.InputStreamReader;

/*
 ** Created by Bapusaheb Patil {@link https://bapspatil.com}
 */

public class MileageModel extends MessageModel {
    public static final String MIME_TYPE = "application/vnd.bapspatil.mileage+json";
    public static final int PLACE_PICKER_ORIGIN = 1;
    public static final int PLACE_PICKER_DESTINATION = 2;

    private MileageMetadata mMetadata;

    public MileageModel(@NonNull Context context, @NonNull LayerClient layerClient, @NonNull Message message) {
        super(context, layerClient, message);
    }

    @Override
    protected void parse(@NonNull MessagePart messagePart) {
        InputStreamReader inputStreamReader = new InputStreamReader(messagePart.getDataStream());
        JsonReader reader = new JsonReader(inputStreamReader);
        mMetadata = getGson().fromJson(reader, MileageMetadata.class);
        try {
            inputStreamReader.close();
        } catch (IOException e) {
                Log.e("Failed to close stream", e.getMessage());
        }
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.mileage_message_view;
    }

    @Override
    public int getContainerViewLayoutId() {
        return R.layout.xdk_ui_standard_message_container;
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart) {
        return true;
    }

    @Override
    public boolean getHasContent() {
        return mMetadata != null;
    }

    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

    @Nullable
    @Override
    public String getFooter() {
        return null;
    }

    public MileageMetadata getmMetadata() {
        return mMetadata;
    }

    public String getName() {
        return mMetadata == null ? null : mMetadata.name;
    }

    public String getOrigin() {
        return mMetadata == null ? null : mMetadata.origin;
    }

    public String getDestination() {
        return mMetadata == null ? null : mMetadata.destination;
    }

    public String getMileageDistance() {
        return mMetadata == null ? null : mMetadata.mileageDistance;
    }

}
