package com.lnquy065.qstation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.lnquy065.qstation.events.ChildEvent;
import com.lnquy065.qstation.events.SingleValueEvent;
import com.lnquy065.qstation.pojos.NodeDataChild;
import com.lnquy065.qstation.pojos.NodeInfo;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase database;
    private DatabaseReference nodeDataRef;
    private DatabaseReference nodeInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        onFirebaseCreate();


//        nodeDataRef.push().setValue(new NodeDataChild(123, 0,1,2,"N01"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



    public void onFirebaseCreate() {
        database = FirebaseDatabase.getInstance();
        nodeDataRef = database.getReference("nodeData");
        nodeInfoRef = database.getReference("nodeInfo");


        nodeInfoRef.addChildEventListener(new ChildEvent() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> t = (Map<String, Object>) dataSnapshot.getValue();
                String nodeID = dataSnapshot.getKey();
                double lat = Double.valueOf(t.get("lat").toString());
                double lng = Double.valueOf(t.get("lng").toString());
                NodeStaticList.nodeList.add(new NodeInfo(nodeID, lat, lng));

                Query maxTimeStamp = nodeDataRef.child(nodeID).orderByChild("timeStamp").limitToLast(1);
                maxTimeStamp.addListenerForSingleValueEvent(new SingleValueEvent() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()!=0) {
                            NodeDataChild nodeDataChild;

                            for (DataSnapshot i: dataSnapshot.getChildren()) {
                                nodeDataChild = i.getValue(NodeDataChild.class);
                                Log.d("Firebase", String.valueOf(nodeDataChild.getTimeStamp()));
                            }

                            //=> Da lay duoc max timestamp cua tung node;
                        }

                    }
                });

            }
        });

    }

}
