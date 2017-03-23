package com.app.history.medical;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class TimeAlarm extends BroadcastReceiver {

	 NotificationManager nm;

	 @Override
	 public void onReceive(Context context, Intent intent) {
		 int notifID = intent.getExtras().getInt("NotifID");
		 String drugName = intent.getExtras().getString("drugName");
		 String Name = intent.getExtras().getString("name");
		  Intent i = new Intent("com.app.history.medical.AlarmDetails");
	      i.putExtra("NotifID", notifID);   
		  PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				  i, 0);
	  nm = (NotificationManager) context
	    .getSystemService(Context.NOTIFICATION_SERVICE);
	  CharSequence from = "Dear "+Name;
	  CharSequence message = "Time to take Medicine.. Named "+drugName;
	  
	  Notification notif = new Notification(R.drawable.ic_launcher,
	    "Take Your Medicine...Named "+drugName, System.currentTimeMillis());
	  notif.setLatestEventInfo(context, from, message, contentIntent);
	  nm.notify(1, notif); 
	  //nm.cancel(notifID);
	 }
	}