package com.app.history.medical;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
 
public class AlarmDetails extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
 
        //---look up the notification manager service---
        NotificationManager nm = (NotificationManager) 
            getSystemService(NOTIFICATION_SERVICE);
        //---cancel the notification---
        nm.cancel(getIntent().getExtras().getInt("NotifID"));  
        Intent intent = new Intent(AlarmDetails.this,HomeActivity.class);
		startActivity(intent);
		finish();
    }
}