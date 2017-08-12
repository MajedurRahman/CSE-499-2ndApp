package com.design.capstone.cse_499_2ndapp.Application;

import android.app.Application;
import android.content.Context;

import com.design.capstone.cse_499_2ndapp.NotificationHandler.MyNotificationOpenedHandler;
import com.design.capstone.cse_499_2ndapp.NotificationHandler.MyNotificationReceivedHandler;
import com.onesignal.OneSignal;

public class MyApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    //Called when the application is starting, before any other application objects have been created.
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //MyNotificationOpenedHandler : This will be called when a notification is tapped on.
        //MyNotificationReceivedHandler : This will be called when a notification is received while your app is running.
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler())
                .setNotificationReceivedHandler( new MyNotificationReceivedHandler() )
                .init();




    }


}
