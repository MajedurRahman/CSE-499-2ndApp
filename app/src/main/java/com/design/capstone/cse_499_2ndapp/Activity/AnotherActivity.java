package com.design.capstone.cse_499_2ndapp.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.design.capstone.cse_499_2ndapp.R;

/**
 * Created by androidbash on 12/16/2016.
 */

public class AnotherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);

        final Dialog dialog = new Dialog(AnotherActivity.this);
        dialog.setTitle("Request Notification ");
        dialog.setContentView(R.layout.custom_dailog);
        dialog.setTitle("Custom Dialog");
        dialog.show();
        dialog.setCancelable(false);

        Button acceptButton = (Button) dialog.findViewById(R.id.accept);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(AnotherActivity.this, " Request Accepted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AnotherActivity.this,MapsActivity.class));

            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                Toast.makeText(AnotherActivity.this, " Request Canceled ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AnotherActivity.this,MapsActivity.class));
            }
        });




    }
}