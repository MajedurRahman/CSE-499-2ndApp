package com.design.capstone.cse_499_2ndapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.design.capstone.cse_499_2ndapp.Model.Online;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userOnlineRef = database.getReference("isOnline");
    DatabaseReference userBusyRef = database.getReference("isBusy");
    ArrayList<LatLng> userPositionList;
    ArrayList<String> onlineUserKeyList = new ArrayList<>();
    ArrayList<Online> onlineUserList;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        onlineUserList = new ArrayList<>();
        userPositionList = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        updateOnlineUserData();

        getDataListner();


    }

   public void  updateOnlineUserData(){


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getOnlineUser();
            }
        });


        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < userPositionList.size(); i++) {
                    addMarkerOnMap(userPositionList.get(i).latitude, userPositionList.get(i).longitude);
                }

            }
        }, 2000);
    }

    private void getDataListner() {




        userOnlineRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                updateOnlineUserData();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                updateOnlineUserData();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                updateOnlineUserData();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(23.815193, 90.426079);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));



    }


    public void getOnlineUser() {
/*

        userOnlineRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.e("User : " , dataSnapshot.toString() );



                onlineUserKeyList.add(dataSnapshot.getKey());


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                onlineUserKeyList.remove(onlineUserKeyList.indexOf(dataSnapshot.getKey()));
                Log.e("user : removed " , ""+ dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/

        userOnlineRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!onlineUserList.isEmpty() || !userPositionList.isEmpty()) {

                    onlineUserList.clear();
                    userPositionList.clear();
                    mMap.clear();
                }

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Online tempOnlineUser = data.getValue(Online.class);
                    onlineUserList.add(tempOnlineUser);

                    //Toast.makeText(MapsActivity.this, onlineUserList.size() + "", Toast.LENGTH_SHORT).show();
                    getOnlineUserData();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });









    }

    public void getOnlineUserData() {


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < onlineUserList.size(); i++) {
                    LatLng latlong = new LatLng(onlineUserList.get(i).getLatitide(), onlineUserList.get(i).getLongitude());
                    userPositionList.add(latlong);
                }


            }
        });

    }

    public void addMarkerOnMap(double latitude, double longitude) {
        MarkerOptions markerOption = new MarkerOptions()
                .position(new LatLng(latitude, longitude))//setting position
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker2));

        mMap.addMarker(markerOption);

    }


}
