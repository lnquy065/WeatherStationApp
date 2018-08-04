package com.lnquy065.qstation.pojos;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lnquy065.qstation.GraphActivity;
import com.lnquy065.qstation.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by LN Quy on 06/07/2018.
 */

public class NodeDataChild {
    private long timeStamp;
    private double vCo2;
    private double vUv;
    private double vDust;
    private String nodeID;
    private MarkerOptions markerOptions;

    public static int LEVEL_CO2_LV0 = 0;
    public static int LEVEL_CO2_LV1 = 1;
    public static int LEVEL_CO2_LV2 = 2;
    public static int LEVEL_CO2_LV3 = 3;

    public static int LEVEL_DUST_LV0 = 0;
    public static int LEVEL_DUST_LV1 = 1;

    public static int LEVEL_UV_LV0 = 0;
    public static int LEVEL_UV_LV1 = 1;
    public static int LEVEL_UV_LV2 = 2;
    public static int LEVEL_UV_LV3 = 3;


    public NodeDataChild(){};


    public NodeDataChild(long timeStamp, double vCo2, double vUv, double vDust) {
        this.timeStamp = timeStamp;
        this.vCo2 = vCo2;
        this.vUv = vUv;
        this.vDust = vDust;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public long getTimeStamp() {
        return timeStamp/1000;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getvCo2() {
        return vCo2;
    }

    public void setvCo2(double vCo2) {
        this.vCo2 = vCo2;
    }

    public double getvUv() {
        return vUv;
    }

    public void setvUv(double vUv) {
        this.vUv = vUv;
    }

    public double getvDust() {
        return vDust;
    }

    public void setvDust(double vDust) {
        this.vDust = vDust;
    }


    public String getCo2String() {
        return vCo2 + " ppm";
    }

    public String getUVString() {
        return vUv + " mW/cm²";
    }

    public String getDustString() {
        return vDust + " mg/m³";
    }


    public String getTimeString() {
        Date date = new Date( timeStamp);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return "("+dateFormat.format(date)+")";
    }

    public double getVal(int valueType) {
        if (valueType == GraphActivity.GRAPH_TYPE_CO2) return vCo2;
        if (valueType == GraphActivity.GRAPH_TYPE_DUST) return vDust;
        if (valueType == GraphActivity.GRAPH_TYPE_UV) return vUv;
        return 0;
    }

    public int getCo2DangerousLevel() {
        if ( vCo2 <= 1000) return LEVEL_CO2_LV0;
        if ( vCo2 <= 2000) return LEVEL_CO2_LV1;
        if ( vCo2 <= 5000) return LEVEL_CO2_LV2;
        return LEVEL_CO2_LV3;
    }

    public int getUVDangerousLevel() {
        if (vUv <= 3) return LEVEL_UV_LV0;
        if ( vUv <= 5) return LEVEL_UV_LV1;
        if ( vUv <= 7 ) return LEVEL_UV_LV2;
        return LEVEL_UV_LV3;
    }
}
