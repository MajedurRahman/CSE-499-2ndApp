package com.design.capstone.cse_499_2ndapp.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.design.capstone.cse_499_2ndapp.Model.Online;
import com.design.capstone.cse_499_2ndapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by androidbash on 12/16/2016.
 */

public class AnotherActivity extends AppCompatActivity {

    String lat;
    String lon;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userOnlineRef = database.getReference("isOnline");
    private DatabaseReference requestRef = database.getReference("Requests");
    private List<String> requestListKey;

    ArrayList<Online> Onldata ;
    private String requestFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);
        Onldata = new ArrayList<>();
        requestListKey = new ArrayList<>();

        getUserData();
        Intent intent = getIntent();
        requestFrom = intent.getStringExtra("requestId");
        getRequestList();
    /*
        lat = intent.getStringExtra("lat");

        lat = intent.getStringExtra("lon");
*/


        final Dialog dialog = new Dialog(AnotherActivity.this);
        dialog.setTitle("Request Notification ");
        dialog.setContentView(R.layout.custom_dailog);
        dialog.setTitle("Custom Dialog");
        dialog.show();
        dialog.setCancelable(false);

        TextView requestTV = (TextView) dialog.findViewById(R.id.requestUserID);
        Button acceptButton = (Button) dialog.findViewById(R.id.accept);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel);

        Online user = new Online();
        user.setId(requestFrom);





        requestTV.setText(requestFrom);


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                if (requestListKey.contains(requestFrom)) {


                    requestRef.child(requestFrom).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {


                               // Toast.makeText(AnotherActivity.this, " Connected " , Toast.LENGTH_SHORT).show();

                                Online online =  finduserdata();

                                Thread.currentThread();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(AnotherActivity.this, " Connected", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AnotherActivity.this, MapsActivity.class).putExtra("lat", online.getLatitude()).putExtra("lon", online.getLongitude()));
                            } else {
                                Toast.makeText(AnotherActivity.this, "This request not available now", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } else {

                    Toast.makeText(AnotherActivity.this, "This request not available now", Toast.LENGTH_SHORT).show();
                }


/*
                Toast.makeText(AnotherActivity.this, " Request Accepted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AnotherActivity.this,MapsActivity.class));*/

            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                Toast.makeText(AnotherActivity.this, " Request Canceled ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AnotherActivity.this, MapsActivity.class));
            }
        });


    }

    private Online finduserdata() {

        Online tempdta = new Online();
        for (int i = 0 ; i < Onldata.size(); i++){
            if (Onldata.get(i).getId().equalsIgnoreCase(requestFrom)){

                tempdta = Onldata.get(i);
            }

        }

        return tempdta;

    }


    private void getRequestList() {


        if (!requestListKey.isEmpty()) {
            requestListKey.clear();
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            requestListKey.add(data.getKey());


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    public void getUserData() {

        if (!Onldata.isEmpty())
        {
            Onldata.clear();
        }

        userOnlineRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()){

                    Online us =  data.getValue(Online.class);
                    Onldata.add(us);

                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}