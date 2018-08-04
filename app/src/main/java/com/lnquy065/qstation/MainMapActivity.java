package com.lnquy065.qstation;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lnquy065.qstation.events.ChildEvent;
import com.lnquy065.qstation.events.SingleValueEvent;
import com.lnquy065.qstation.googlemaps.CustomInfoWindow;
import com.lnquy065.qstation.pojos.NodeDataChild;
import com.lnquy065.qstation.pojos.NodeInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainMapActivity extends AppCompatActivity
        implements  OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase database;
    private DatabaseReference nodeDataRef;
    private DatabaseReference nodeInfoRef;
    private MapFragment mapFragment;
    private Geocoder geocoder;
    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private LocationManager locationManager;

    private Switch swTracking;
    private SeekBar seekBarMapZoom;
    private ImageButton ibtnLayer, ibtnStatistic;
    private boolean mMapNormalMode = true;

    private NodeInfo revNode = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map2);

        revNode = (NodeInfo) getIntent().getSerializableExtra("Node");

        requirePermission();
        initGoogleMap();
        initFirebase();
        initControls();
        initEvents();
        initTracking();
    }

    private void initTracking() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (!swTracking.isChecked()) return;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    for (final NodeInfo node: NodeStaticList.nodeList) {
                        if (!node.isInbound(location)) continue;

                        Query query = nodeDataRef.child(node.getNodeID()).orderByChild("timeStamp").limitToLast(1);
                        query.addChildEventListener(new ChildEvent() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                NodeDataChild nodeDataChild = dataSnapshot.getValue(NodeDataChild.class);

                            }
                        });

                        query.addListenerForSingleValueEvent(new SingleValueEvent() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount()!=0) {
                                    NodeDataChild nodeData;
                                    for (DataSnapshot i: dataSnapshot.getChildren()) {
                                        nodeData = i.getValue(NodeDataChild.class);
                                        int co2Level = nodeData.getCo2DangerousLevel();
                                        int uvLevel = nodeData.getUVDangerousLevel();
                                        String notifyMessage = "You are in ";
                                        Log.d("NodeDistanceLvl", co2Level + " (" + nodeData.getvCo2() + ")");
                                        if (co2Level > 1) notifyMessage += " high Co2 intensity";
                                        if (uvLevel > 1) notifyMessage += " high UV intensity";
                                        if (co2Level > 1 || uvLevel > 1) {
                                            Toast.makeText(MainMapActivity.this, notifyMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            }
                        });

                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {
                    Log.d("GPSchange", "Provider Enable");
                }

                @Override
                public void onProviderDisabled(String s) {
                    Log.d("GPSchange", "Provider Failed");
                }
            });

        } else {
            Log.d("GPSchange", "Provider Failed");
        }
    }

    private void initControls() {
        seekBarMapZoom = this.findViewById(R.id.seekBarMapZoom);
        swTracking = this.findViewById(R.id.swTracking);
        ibtnLayer = this.findViewById(R.id.ibtnLayer);
        ibtnStatistic = this.findViewById(R.id.ibtnStatistic);
    }

    private void initEvents() {
        ibtnStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMapActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        ibtnLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapNormalMode = !mMapNormalMode;
                if (mMapNormalMode) mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                else mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        swTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (swTracking.isChecked()) initTracking();
            }
        });

        seekBarMapZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo((float) i));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void requirePermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ActivityCompat.checkSelfPermission(MainMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainMapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainMapActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            if(!mMap.isMyLocationEnabled())
                mMap.setMyLocationEnabled(true);
        }
        seekBarMapZoom.setProgress(10);
        //setup custom info
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));
        //zoom toi node
        if (revNode!= null) {
            mMap.animateCamera( CameraUpdateFactory.newLatLng(revNode.getLatLng()));
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_map, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        geocoder = new Geocoder(MainMapActivity.this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("Geocoder-text", s);
                List<Address> addressList=null;
                if (s!=null && !s.equals("")) {

                    try {
                        addressList  = geocoder.getFromLocationName(s, 3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList!=null) {
                        for (Address v: addressList) {
                            Log.d("Geocoder", v.getLatitude() + v.getLongitude()+ "");
                        }

                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.app_bar_search) {
            Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


    public void initGoogleMap() {
        //load gg map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).build();

        placeAutocompleteFragment.setFilter(autocompleteFilter);

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng pLatLng = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(pLatLng).title(place.getName().toString()));
                //zoom: 2 -> 21
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pLatLng, 10f));
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();

            }
        });

    }


    public void initFirebase() {
        database = FirebaseDatabase.getInstance();
        nodeDataRef = database.getReference("nodeData");
        nodeInfoRef = database.getReference("nodeInfo");

        NodeStaticList.nodeList.clear();
        nodeInfoRef.addChildEventListener(new ChildEvent() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> t = (Map<String, Object>) dataSnapshot.getValue();
                final String nodeID = dataSnapshot.getKey();
                final double lat = Double.valueOf(t.get("lat").toString());
                final double lng = Double.valueOf(t.get("lng").toString());
                final String name = t.get("name").toString();
                final NodeInfo node = new NodeInfo(nodeID, lat, lng, name);
                NodeStaticList.nodeList.add(node);
                Log.d("FIREBASE_MARKER", dataSnapshot.getKey());

                database.getReference("nodeData").child(node.getNodeID()).addChildEventListener(new ChildEvent() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        NodeDataChild nodeDataChild = dataSnapshot.getValue(NodeDataChild.class);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(node.getLatLng())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.qnode))
                                .anchor(1.0f, 0.5f);
                        nodeDataChild.setNodeID(node.getNodeID());
                        Marker mk = mMap.addMarker( node.createMarkerOption() );
                        mk.setTag(nodeDataChild);
                        node.setMarker(mk);
                        Log.d("FIREBASE_MARKER", node.getNodeID()+": "+ dataSnapshot.getKey());
                    }
                });

//                Query maxTimeStamp = nodeDataRef.child(nodeID).orderByChild("timeStamp").limitToLast(1);
//
//                maxTimeStamp.addListenerForSingleValueEvent(new SingleValueEvent() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.getChildrenCount()!=0) {
//                            NodeDataChild nodeDataChild;
//
//                            for (DataSnapshot i: dataSnapshot.getChildren()) {
//                                nodeDataChild = i.getValue(NodeDataChild.class);
//
//                                MarkerOptions markerOptions = new MarkerOptions()
//                                        .position(new LatLng(lat, lng))
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.qnode))
//                                        .anchor(1.0f, 0.5f);
//                                nodeDataChild.setNodeID(nodeID);
//                                mMap.addMarker( markerOptions ).setTag(nodeDataChild);
//
//
//                            }
//
//                            //=> Da lay duoc max timestamp cua tung node;
//                        }
//
//                    }
//                });
//
            }
        });

    }



}
