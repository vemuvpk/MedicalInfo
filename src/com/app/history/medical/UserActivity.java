package com.app.history.medical;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.history.medical.util.Constant;
import com.app.history.medical.util.SlidingAct;
import com.flurry.android.FlurryAgent;

public class UserActivity extends SlidingAct implements OnClickListener{
	Button _btnAddPrescription , _btnViewHistory;
	ImageView menubtn;
	SharedPreferences prfs;
	String first_name,last_name,User_Id;
	TextView User_name;
	String FirstName,LastName,RelationType,UserType;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);		
		init();
		
	}
	public void init(){
		_btnAddPrescription  =  (Button) findViewById(R.id.button_addprescription);
		_btnViewHistory      =  (Button)  findViewById(R.id.button_viewhistory);
		User_name      =  (TextView)findViewById(R.id.user_na);
		_btnAddPrescription.setOnClickListener(this);
		_btnViewHistory.setOnClickListener(this);
		prfs = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);	
		User_Id = prfs.getString("user_id","").toString();

		Intent intent = getIntent();
		FirstName = intent.getStringExtra("UserFirstName");
		LastName = intent.getStringExtra("UserLastName");
		RelationType = intent.getStringExtra("Relation");
		UserType= intent.getStringExtra("TypeUser");
		if(UserType.equals("1")){
			User_name.setText("Hi"+" "+FirstName +" "+ LastName+",");
			Log.d("Guru", "Guru"+FirstName+" "+LastName+" "+RelationType+" "+UserType);
		}else{
			User_name.setText(RelationType+"-: "+FirstName +" "+ LastName+",");	
			Log.d("Guruss", "Guruss"+FirstName+" "+LastName+" "+RelationType+" "+UserType);
		}

		setBehindLeftContentView(R.layout.menubar);
		this.setSlidingActionBarEnabled(true);
		menubtn = (ImageView)findViewById(R.id.menuicon1);
		menubtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showMenu();
			}
		});
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_addprescription:
			Intent _intent = new Intent(UserActivity.this , AddPrescription.class);
			_intent.putExtra("UserFirstName", FirstName);
			_intent.putExtra("UserLastName", LastName);
			_intent.putExtra("Relation", RelationType);
			_intent.putExtra("TypeUser", UserType);
			startActivity(_intent);
			finish();
			break;
		case R.id.button_viewhistory:

			Intent intent = new Intent(UserActivity.this , ViewHistoryActivity.class);
			intent.putExtra("UserFirstName", FirstName);
			intent.putExtra("UserLastName", LastName);
			intent.putExtra("Relation", RelationType);
			intent.putExtra("TypeUser", UserType);
			startActivity(intent);			
			break;
		}
	}
	//=================Flurry=====================
	public void onStart(){
		   super.onStart();
		   FlurryAgent.onStartSession(this, "VZZFBN2NYBNTVX5VCYCQ");
		}
		public void onStop(){
		   super.onStop();
		   FlurryAgent.onEndSession(this);
		}
		
	//===================End=========================
}
