package com.bapspatil.surface.model;
/*
 ** Created by Bapusaheb Patil {@link https://bapspatil.com}
 */

import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@SuppressWarnings("WeakerAccess")
public class MileageMetadata {
    @SerializedName("name")
    public String name;
    @SerializedName("origin")
    public String origin;
    @SerializedName("destination")
    public String destination;
    @SerializedName("mileageDistance")
    public String mileageDistance;

    public MileageMetadata() {

    }

    public MileageMetadata(String name, String origin, String destination, String mileageDistance) {
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.mileageDistance = mileageDistance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getMileageDistance() {
        return mileageDistance;
    }

    public void setMileageDistance(String mileageDistance) {
        this.mileageDistance = mileageDistance;
    }

    public static byte[] toStream(MileageMetadata mileageMetadata) {
        // Reference for stream of bytes
        byte[] stream = null;
        // ObjectOutputStream is used to convert a Java object into OutputStream
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(mileageMetadata);
            stream = baos.toByteArray();
        } catch (IOException e) {
            // Error in serialization
            e.printStackTrace();
        }
        return stream;
    }
}
