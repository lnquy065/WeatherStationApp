package com.lnquy065.qstation.googlemaps;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.lnquy065.qstation.R;
import com.lnquy065.qstation.pojos.NodeDataChild;

/**
 * Created by LN Quy on 12/07/2018.
 */

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private Context context;


    public CustomInfoWindow(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = null;
        try {

            NodeDataChild nodeDataChild = (NodeDataChild) marker.getTag();

            v = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_infowindow, null);

            TextView ggInfoTitle = v.findViewById(R.id.ggInfoTitle);
            TextView ggInfoCo2 = v.findViewById(R.id.ggInfoCo2);
            TextView ggInfoDust = v.findViewById(R.id.ggInfoDust);
            TextView ggInfoUv = v.findViewById(R.id.ggInfoUv);
            TextView ggInfoTime = v.findViewById(R.id.ggInfoTime);

            ggInfoTitle.setText(nodeDataChild.getNodeID());
            ggInfoCo2.setText(nodeDataChild.getCo2String());
            ggInfoDust.setText(nodeDataChild.getDustString());
            ggInfoUv.setText(nodeDataChild.getUVString());
            ggInfoTime.setText(nodeDataChild.getTimeString());


        } catch (Exception e) {

        }
        return v;
    }
}
