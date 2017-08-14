package com.design.capstone.cse_499_2ndapp.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.design.capstone.cse_499_2ndapp.Model.Online;
import com.design.capstone.cse_499_2ndapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userOnlineRef = database.getReference("isOnline");
    DatabaseReference userBusyRef = database.getReference("isBusy");

    DatabaseReference monitorRef = database.getReference("isMonitorOnline");


    ArrayList<LatLng> userPositionList;
    ArrayList<String> onlineUserKeyList = new ArrayList<>();
    ArrayList<Online> onlineUserList;
    String userID;
    LatLng acceptUserPosition;
    boolean Flag = false;
    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private Button signOutButton;
    private Switch onlineOfflineSwitch;
    private List<String> onlineMonitorList;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        intent = getIntent();
        initComponents();
        initAction();


        onlineUserList = new ArrayList<>();
        userPositionList = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getPhoneNumber().toString();

        updateOnlineUserData();
        getDataListner();
        initOneSignalData();


       getConnectedUserdata();


    }

    private void getConnectedUserdata() {

        if (intent != null) {

            try {
                double latitude = intent.getDoubleExtra("lat", 23.45);
                double longitude = intent.getDoubleExtra("lon", 90.81);


                Toast.makeText(this, "" + latitude, Toast.LENGTH_SHORT).show();
                acceptUserPosition = new LatLng(latitude , longitude);
                Flag = true;

                addMarkerOnMap(latitude,longitude);

                //  Toast.makeText(this, latitude.toString(), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {

                Log.e(" Errrr", e.getMessage());
            }


        }
    }


    private void initAction() {


        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                monitorRef.child(userID).removeValue();
                finish();
            }
        });

        onlineOfflineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    onlineOfflineSwitch.setText("Online");
                    monitorRef.child(userID).setValue(true);

                } else {
                    onlineOfflineSwitch.setText("Offline");
                    monitorRef.child(userID).removeValue();

                }
            }
        });
    }

    private void initComponents() {

        signOutButton = (Button) findViewById(R.id.sign_out_button);
        onlineOfflineSwitch = (Switch) findViewById(R.id.onlineOfflineSwitch);
    }

    public void initOneSignalData() {

        OneSignal.sendTag("User_ID", userID);
        OneSignal.sendTag("type", "monitor");


    }


    public void updateOnlineUserData() {


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
              /*  if (!dataSnapshot.getKey().toString().trim().equalsIgnoreCase((userID.trim()))) {

                    sendNotification();
                }*/


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

        userOnlineRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!onlineUserList.isEmpty() || !userPositionList.isEmpty()) {

                    onlineUserList.clear();
                    userPositionList.clear();
                    if (mMap != null) {
                        mMap.clear();
                    }

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
                    LatLng latlong = new LatLng(onlineUserList.get(i).getLatitude(), onlineUserList.get(i).getLongitude());
                    userPositionList.add(latlong);
                }


            }
        });

    }


    private void sendNotification() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);


                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic ODVkNDdjZTMtMThlNy00M2VmLTkwYTItOGI3NTgyNmQ5MDlm");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"9902773d-e28d-4b87-9ed2-b1683306d0bc\","
                                + "\"included_segments\": [\"All\"],"
                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"Someone is Visible in Map \"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }


    public void addMarkerOnMap(double latitude, double longitude) {
        MarkerOptions markerOption = new MarkerOptions()
                .position(new LatLng(latitude, longitude))//setting position
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker2));

        mMap.addMarker(markerOption);


        if (Flag){

            MarkerOptions markerConnectionUser = new MarkerOptions()
                    .position(acceptUserPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker4));
            mMap.addMarker(markerConnectionUser);

        }








    }

    public void sendRequestToSpecificMonitorApp(final String userKey) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonResponse;

                    URL url = new URL("https://onesignal.com/api/v1/notifications");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setUseCaches(false);
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    con.setRequestProperty("Authorization", "Basic ODVkNDdjZTMtMThlNy00M2VmLTkwYTItOGI3NTgyNmQ5MDlm");
                    con.setRequestMethod("POST");

                    String strJsonBody = "{"
                            + "\"app_id\": \"9902773d-e28d-4b87-9ed2-b1683306d0bc\","
                            + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + userKey + "\"}],"
                            + "\"data\": {\"tap\":\"tap\",\"requestId\":\"" + userID + "\"},"
                            + "\"buttons\": [{\"id\":\"accept\",\"text\":\"OPEN\",\"icon\":\"\"},{\"id\":\"cancel\",\"text\":\"CANCEL\",\"icon\":\"\"}],"
                            + "\"contents\": {\"en\": \"Tap Here To See Notification\"}"
                            + "}";


                    System.out.println("strJsonBody:\n" + strJsonBody);

                    byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                    con.setFixedLengthStreamingMode(sendBytes.length);

                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(sendBytes);

                    int httpResponse = con.getResponseCode();
                    System.out.println("httpResponse: " + httpResponse);

                    if (httpResponse >= HttpURLConnection.HTTP_OK
                            && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                        Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    } else {
                        Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    }
                    System.out.println("jsonResponse:\n" + jsonResponse);

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }


    private void sendNotificationToOnlineUser() {


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonResponse;

                    URL url = new URL("https://onesignal.com/api/v1/notifications");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setUseCaches(false);
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    con.setRequestProperty("Authorization", "Basic ODVkNDdjZTMtMThlNy00M2VmLTkwYTItOGI3NTgyNmQ5MDlm");
                    con.setRequestMethod("POST");

                    String strJsonBody = "{"
                            + "\"app_id\": \"9902773d-e28d-4b87-9ed2-b1683306d0bc\","
                            //  +   "\"included_segments\": [\"All\"],"
                            + "\"filters\": [ {\"field\": \"tag\", \"key\": \"Online\", \"relation\": \"exists\"}],"
                            + "\"data\": {\"tap\":\"tap\"},"
                            + "\"buttons\": [{\"id\":\"explore\",\"text\":\"EXPLORE NOW\",\"icon\":\"\"}],"
                            + "\"contents\": {\"en\": \"Someone is visible on Map\"}"
                            + "}";


                    System.out.println("strJsonBody:\n" + strJsonBody);

                    byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                    con.setFixedLengthStreamingMode(sendBytes.length);

                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(sendBytes);

                    int httpResponse = con.getResponseCode();
                    System.out.println("httpResponse: " + httpResponse);

                    if (httpResponse >= HttpURLConnection.HTTP_OK
                            && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                        Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    } else {
                        Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    }
                    System.out.println("jsonResponse:\n" + jsonResponse);

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });

    }


}
