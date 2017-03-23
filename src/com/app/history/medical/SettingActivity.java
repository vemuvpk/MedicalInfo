package com.app.history.medical;

import com.app.history.medical.util.Constant;
import com.app.history.medical.util.SlidingAct;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

public class SettingActivity extends SlidingAct {
	CheckBox checkBoxNotify;
	SharedPreferences prfs;
	String first_name,last_name,User_Id;
	ImageView menubtn;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.setting_activity);
		checkBoxNotify=(CheckBox)findViewById(R.id.checkBoxNotify);
		prfs = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		first_name = prfs.getString("first_name","").toString();
		last_name = prfs.getString("last_name","").toString();
		User_Id = prfs.getString("user_id","").toString();
		setBehindLeftContentView(R.layout.menubar);
		this.setSlidingActionBarEnabled(true);
		menubtn = (ImageView)findViewById(R.id.menuicon);
		menubtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showMenu();
			}
		});
		
		if(prfs.getString("NoficationStatus","").toString().equals("ON")){
			checkBoxNotify.setChecked(true);
			checkBoxNotify.setText("Enable");
			Log.d("Noti", "Noti");
			SharedPreferences.Editor prefsEditor = prfs.edit();
			prefsEditor.putString("NoficationStatus", "ON");
			prefsEditor.commit();
		}else{
			checkBoxNotify.setChecked(false);
			checkBoxNotify.setText("Disable");
			Log.d("Noti1", "Noti2");
			SharedPreferences.Editor prefsEditor = prfs.edit();
			prefsEditor.putString("NoficationStatus", "OFF");
			prefsEditor.commit();
		}
		checkBoxNotify.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkBoxNotify.isChecked()){
					checkBoxNotify.setText("Enable");
					SharedPreferences.Editor prefsEditor = prfs.edit();
					prefsEditor.putString("NoficationStatus", "ON");
					prefsEditor.commit();
				}else{
					checkBoxNotify.setText("Disable");
					SharedPreferences.Editor prefsEditor = prfs.edit();
					prefsEditor.putString("NoficationStatus", "OFF");
					prefsEditor.commit();			
				}
				
			}
		});
	}
}
