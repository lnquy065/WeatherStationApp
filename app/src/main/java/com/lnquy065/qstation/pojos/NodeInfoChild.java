package com.lnquy065.qstation.pojos;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by LN Quy on 06/07/2018.
 */

public class NodeInfoChild implements Serializable{
    private String nodeID;
    private double lat;
    private double lng;
    private long startedDate;
    private String name;

    public NodeInfoChild() {};

    public NodeInfoChild(String nodeID, double lat, double lng, String name) {
        this.nodeID = nodeID;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeInfoChild(String nodeID, double lat, double lng) {
        this.nodeID = nodeID;
        this.lat = lat;
        this.lng = lng;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(long startedDate) {
        this.startedDate = startedDate;
    }
}
