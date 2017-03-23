package com.app.history.medical;

import com.app.history.medical.util.Constant;
import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class Splash extends Activity {
	//	String userName,password;
	SharedPreferences _preference;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		_preference                     = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		Thread thread                   = new Thread(runable);
		thread.start();
	}

	public Runnable runable = new Runnable() {
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
			if(!_preference.getString("user_id", "").equals("")){
				if(_preference.getString(Constant.APP_VERIFY, "").equals("yes")){
					Intent intent = new Intent(Splash.this, HomeActivity.class);
					intent.putExtra("Direct", "1");
					startActivity(intent);
					finish();
				}
				else if(_preference.getString(Constant.APP_VERIFY, "").equals("no")){
					Intent intent = new Intent(Splash.this, ActivateCodeActivity.class);
					startActivity(intent);
					finish();
				}
			}
			else{
				Intent intent = new Intent(Splash.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}
	};
	public void onStart(){
		   super.onStart();
		   FlurryAgent.onStartSession(this, "VZZFBN2NYBNTVX5VCYCQ");
		}
		public void onStop(){
		   super.onStop();
		   FlurryAgent.onEndSession(this);
		}
}
