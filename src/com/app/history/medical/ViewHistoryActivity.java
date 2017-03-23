package com.app.history.medical;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.app.history.medical.util.Constant;
import com.app.history.medical.util.MyHttpClient;
import com.app.history.medical.util.Refrence;
import com.app.history.medical.util.SlidingAct;
import com.flurry.android.FlurryAgent;

public class ViewHistoryActivity extends SlidingAct {
	SharedPreferences prfs;
	String first_name,last_name,User_Id,RelationType,UserType;
	ListView History_listView;
	SimpleAdapter adapter;
	ImageView menubtn;
	TextView TextMessage,NoOftimeInday;
	ArrayList<HashMap<String, String>> historyList = new ArrayList<HashMap<String, String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.summary);
		History_listView    =  (ListView)  findViewById(R.id.History_listView);
		
		
		TextMessage    =  (TextView)  findViewById(R.id.textmessage);
		NoOftimeInday    =  (TextView)  findViewById(R.id.NoOftimeInday);
		
		prfs = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		User_Id = prfs.getString("user_id","").toString();
		
		Intent intent = getIntent();
		first_name = intent.getStringExtra("UserFirstName");
		last_name = intent.getStringExtra("UserLastName");
		RelationType = intent.getStringExtra("Relation");
		UserType= intent.getStringExtra("TypeUser");
		
		Log.d("UderId", ""+User_Id+" +first_name");
		if(Refrence.isOnline(ViewHistoryActivity.this)){
		new DisplayMedicalHistory().execute();
		}else{
			Toast.makeText(ViewHistoryActivity.this, "Please connect you internet first", Toast.LENGTH_LONG).show();
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
		
		
		History_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String Name= historyList.get(position).get("drugname");
				String ImageUrl= historyList.get(position).get("imageUrl");
				String NoOfTime= historyList.get(position).get("NoOfTimes");
				String NoOfDays= historyList.get(position).get("NoOfDays");
				Log.d("HHHH","HHH"+ImageUrl);
				Intent intent=new Intent(ViewHistoryActivity.this, ImageViewActivity.class);
				intent.putExtra("UserFirstName", Name);
				intent.putExtra("NoOfTime", NoOfTime);	
				intent.putExtra("NoOfDays", NoOfDays);
				intent.putExtra("Image", ImageUrl);	
				startActivity(intent);
				//Toast.makeText(getApplicationContext(),"Click ListItem Number " + position+" "+Name, Toast.LENGTH_LONG).show();
			}
		}); 
	}
	//===============Web services call for displaying user medical history================== 
	private class DisplayMedicalHistory extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		String success = "";
		String MSG;
		JSONObject json , json2;
		JSONArray jArray;
		String myImage="";
		int count=0;
		String time;

		protected void onPreExecute() {
			dialog = ProgressDialog.show(ViewHistoryActivity.this, "","Loading, please wait...", true);
		}

		@SuppressWarnings("unchecked")
		protected Boolean doInBackground(Void... unused) {
			//JSONObject mainObject;
			try {
			MyHttpClient client = new MyHttpClient(Constant.USER_History);
			client.connectForMultipart();
			client.addFormPart("user_id", 	    User_Id);
			client.addFormPart("name", 	    first_name);
			client.finishMultipart();
			String data = client.getResponse();
			Log.d("RESPONSE","Respone"+data);
			json 			  = new JSONObject(data);
			//success               = mainObject.getString("Response");
			//Log.d("myResponse","myResponse"+success);
			json2 = json.getJSONObject("response");
			//MSG = json2.getString("msg");
			success = json2.getString("code");
			Log.d("Gurukant", "Gurukant"+success);
			if (success.equalsIgnoreCase("200")) {
				historyList.clear();
				jArray = json2.getJSONArray("data");
				for(int i=0;i<jArray.length();i++){							
					JSONObject json_data = jArray.getJSONObject(i);
					if(json_data.get("morning").toString().equals("1") && json_data.get("afternoon").toString().equals("1") && json_data.get("night").toString().equals("1")){
						count=3;
						time="times a day";
						
					}else if(json_data.get("morning").toString().equals("0") && json_data.get("afternoon").toString().equals("0")){
						count=1;
						time="time a day";
					}else if(json_data.get("morning").toString().equals("0") && json_data.get("night").toString().equals("0")){
						count=1;
						time="time a day";
					}else if(json_data.get("afternoon").toString().equals("0") && json_data.get("night").toString().equals("0")){
						count=1;
						time="time a day";
					}else{
						count=2;
						time="times a day";
					}
					HashMap map = new HashMap<String, String>();
					Log.d("Guru", "Guru"+json_data.get("drug_name").toString());
					map.put("drugname","DrugName-:"+json_data.get("drug_name").toString());
					map.put("datetime", json_data.get("pre_datetime").toString());
					map.put("NoOfDays", json_data.get("no_time_in_day").toString());				
					map.put("NoOfTimes", count+" "+time);
					map.put("Morning", json_data.get("morning").toString());
					map.put("AfterNoon", json_data.get("afternoon").toString());
					map.put("Night", json_data.get("night").toString());
					if(json_data.get("image").toString().equals("")){
					map.put("imageUrl", myImage);
					}else{
					map.put("imageUrl", json_data.get("image").toString());
					myImage=json_data.get("image").toString();
					}
					historyList.add(map);
				}							
			  } 
			}
		catch(Throwable t) {
			t.printStackTrace();
			return null;
		}
		return null;
		}
		protected void onPostExecute(Boolean unused) {
			super.onPostExecute(unused);
			dialog.dismiss();
			Log.d("Hey","Hey"+historyList.size());
			if (success.equalsIgnoreCase("200")) {
				adapter = new SimpleAdapter(ViewHistoryActivity.this, historyList, R.layout.history_activity, new String[] { "drugname","datetime","NoOfTimes" },new int[]{R.id.textView_drugs,R.id.textViewdate,R.id.NoOftimeInday});
				History_listView.setAdapter(adapter);
			}else{
				TextMessage.setVisibility(View.VISIBLE);
				TextMessage.setText("No record exist");			
			}
		}
	}
	//================End================
	//============Flurry=====================
	public void onStart(){
		   super.onStart();
		   FlurryAgent.onStartSession(this, "VZZFBN2NYBNTVX5VCYCQ");
		}
		public void onStop(){
		   super.onStop();
		   FlurryAgent.onEndSession(this);
		}
    //==============End=========================
}
