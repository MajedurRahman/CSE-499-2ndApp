package com.design.capstone.cse_499_2ndapp;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by androidbash on 12/16/2016.
 */

public class AnotherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);

        final Dialog dialog = new Dialog(AnotherActivity.this);
        dialog.setContentView(R.layout.custom_dailog);

        dialog.setTitle("Custom Dialog");
        dialog.show();
        dialog.setCancelable(false);




    }
}