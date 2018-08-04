package com.lnquy065.qstation.pojos;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lnquy065.qstation.R;

import java.io.Serializable;

/**
 * Created by LN Quy on 06/07/2018.
 */

public class NodeInfo implements Serializable{
    private String nodeID;
    private double lat;
    private double lng;
    private long startedDate;
    private String name;
    private transient Marker marker;

    public NodeInfo() {};

    public NodeInfo(String nodeID, double lat, double lng, String name) {
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

    public NodeInfo(String nodeID, double lat, double lng) {
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

    public boolean isInbound(Location location) {
        Location nodeLocation=new Location("locationB");
        nodeLocation.setLatitude( this.lat );
        nodeLocation.setLongitude( this.lng );
        double distance = location.distanceTo(nodeLocation);
        Log.d("NodeDistance", distance +"");
        if ( distance < 300) return true;
        return false;
    }


    public void setMarker(Marker marker) {
        if (this.marker!=null) this.marker.setVisible(false);
        this.marker = marker;
    }

    public MarkerOptions createMarkerOption() {
        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.qnode))
                .anchor(1.0f, 0.5f);
        return marker;
    }
}
