package com.app.history.medical;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.app.history.medical.util.ConnectionDetector;
import com.app.history.medical.util.Constant;
import com.app.history.medical.util.Refrence;
import com.app.history.medical.util.SlidingAct;
import com.flurry.android.FlurryAgent;

public class HomeActivity extends SlidingAct implements OnClickListener{
	ListView _listUser;
	ImageView _imgAddUser,ImageView_Profile;
	LinearLayout AddUser_Info;
	TextView User_name;
	ConnectionDetector _cd;
	String _newname = "" , _newrelation = "";
	SharedPreferences prfs;
	String first_name,last_name,User_Id,Profile_pic;
	SimpleAdapter adapter;
	ImageView menubtn;
	public ProgressDialog dialog;

	Bitmap bitmap;
	//ProgressDialog pDialog;


	ArrayList<HashMap<String, String>> inviteList = new ArrayList<HashMap<String, String>>();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.homescreen);	
		init();	
		new DisplayUaser().execute();	
		_listUser.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String Name= inviteList.get(position).get("Username");
				String Relation= inviteList.get(position).get("Type");
				Log.d("HHHH","HHH"+Relation);
				Intent intent=new Intent(HomeActivity.this, UserActivity.class);
				intent.putExtra("UserFirstName", Name);
				intent.putExtra("UserLastName", "");
				intent.putExtra("Relation", Relation);
				intent.putExtra("TypeUser", "0");
				startActivity(intent);
				//Toast.makeText(getApplicationContext(),"Click ListItem Number " + position+" "+Name, Toast.LENGTH_LONG).show();
			}
		}); 
	}

	public void init(){
		_imgAddUser  =  (ImageView) findViewById(R.id.add_new_record);

		ImageView_Profile=  (ImageView) findViewById(R.id.imageView_profile);
		_listUser    =  (ListView)  findViewById(R.id.listView_user);		
		AddUser_Info=(LinearLayout)findViewById(R.id.addrecord_user);
		User_name=(TextView)findViewById(R.id.user_name);
		_cd          =   new ConnectionDetector(HomeActivity.this);
		_imgAddUser.setOnClickListener(this);
		AddUser_Info.setOnClickListener(this);
		prfs = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		first_name = prfs.getString("first_name","").toString();
		last_name = prfs.getString("last_name","").toString();
		User_Id = prfs.getString("user_id","").toString();
		User_name.setText(first_name +" "+last_name);
		Log.d("UderId", ""+User_Id+" +first_name");

		if(!prfs.getString("profile_pic_face","").toString().equals("") && prfs.getString(Constant.LOGIN_TYPE,"").toString().equals(Constant.FB_LOGIN)){
			Log.d("Guru", "Guru"+prfs.getString("profile_pic_face","").toString().equals("")+""+prfs.getString(Constant.LOGIN_TYPE,"").toString().equals(Constant.FB_LOGIN));
			Profile_pic = prfs.getString("profile_pic_face","").toString();
			new LoadImage().execute(Profile_pic);  	 
		}else if (!prfs.getString("profile_pic","").toString().equals("") && prfs.getString(Constant.LOGIN_TYPE,"").toString().equals(Constant.APP_LOGIN)){
			Log.d("Guruaa", "Guruaa"+prfs.getString("profile_pic","").toString().equals("")+""+prfs.getString(Constant.LOGIN_TYPE,"").toString().equals(Constant.APP_LOGIN));
			Profile_pic = prfs.getString("profile_pic","").toString();
			new LoadImage().execute(Profile_pic); 			  
		}else{
			Log.d("Guruss", "Guruss");			 
		}

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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_new_record:
			Intent intentuser=new Intent(HomeActivity.this, AddNewActivity.class);			
			startActivity(intentuser);
			break;
		case R.id.addrecord_user:
			Intent intent=new Intent(HomeActivity.this, UserActivity.class);
			intent.putExtra("UserFirstName", first_name);
			intent.putExtra("UserLastName", last_name);
			intent.putExtra("Relation", "");
			intent.putExtra("TypeUser", "1");
			startActivity(intent);
			break;
		}
	}

	public void diaShow(){
		_newname     = "";
		_newrelation = "";
		AlertDialog.Builder _alert = new AlertDialog.Builder(HomeActivity.this);
		_alert.setTitle("Add New member");
		_alert.setMessage("Do you want to add new member ?");
		_alert.setCancelable(false);

		LinearLayout _lay = new LinearLayout(HomeActivity.this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
		_lay.setOrientation(LinearLayout.VERTICAL);
		_lay.setLayoutParams(params);

		TextView _text1 = new TextView(HomeActivity.this);
		_text1.setText("Please enter member name");
		_text1.setTextSize(16);
		_text1.setPadding(10, 10, 10, 0);
		final EditText _edittext1 = new EditText(HomeActivity.this);
		_edittext1.setHint("Enter name");
		_edittext1.setTextSize(16);
		_edittext1.setPadding(10, 5, 10, 0);
		TextView _text2 = new TextView(HomeActivity.this);
		_text2.setText("Please enter relation with user");
		_text2.setTextSize(16);
		_text2.setPadding(10, 10, 10, 0);
		final EditText _edittext2 = new EditText(HomeActivity.this);
		_edittext2.setHint("Enter relation");
		_edittext2.setTextSize(16);
		_edittext2.setPadding(10, 5, 10, 10);

		_lay.addView(_text1);
		_lay.addView(_edittext1);
		_lay.addView(_text2);
		_lay.addView(_edittext2);
		_alert.setView(_lay);

		_alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				if(!_edittext1.getText().toString().trim().equalsIgnoreCase("")){
					if(!_edittext2.getText().toString().trim().equalsIgnoreCase("")){
						if(_cd.isConnectingToInternet()){
							_newname     = _edittext1.getText().toString().trim();
							_newrelation = _edittext2.getText().toString().trim();
							new AddNewUser().execute();
						}else{
							Toast.makeText(HomeActivity.this, "Internet not available", Toast.LENGTH_LONG).show();
						}
					}else{
						Toast.makeText(HomeActivity.this, "Please enter relation with user", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(HomeActivity.this, "Please enter member name", Toast.LENGTH_LONG).show();
				}
				dialog.cancel();		
			}
		});
		_alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				_newname     = "";
				_newrelation = "";
				dialog.cancel();
			}
		});

		AlertDialog _alertDialog = _alert.create();
		_alertDialog.show();
	}
	//============ Add new User ========================================
	private class AddNewUser extends AsyncTask<Void, Void, Boolean> {
		public ProgressDialog dialog;
		String success = "";
		JSONObject json , json2;
		JSONArray jArray;
		protected void onPreExecute() {
			dialog = ProgressDialog.show(HomeActivity.this, "","Loading, please wait...", true);
		}

		protected Boolean doInBackground(Void... unused) {
			String url = Constant.ADD_USER+"user_id="+User_Id+"&relation_type="+_newrelation+"&name="+_newname;
			HttpResponse responseGet;
			try {
				HttpClient client = new DefaultHttpClient();  

				Log.v("URL DAta", url);
				HttpGet get = new HttpGet(url);

				try {
					responseGet = client.execute(get); 
				} catch (Exception e) {
					// TODO: handle exception
					return null;
				}

				HttpEntity resEntityGet = responseGet.getEntity();  
				if (resEntityGet != null) {  
					InputStream instream = resEntityGet.getContent();
					String result = Refrence.convertStreamToString(instream);
					json = new JSONObject(result);
					Log.d("re" + result, "msg");					
					json2 = json.getJSONObject("response");
					success = json2.getString("code");
					if (success.equalsIgnoreCase("200")) {

					} 
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return null;
		}


		protected void onPostExecute(Boolean unused) {
			super.onPostExecute(unused);

			if (success.equalsIgnoreCase("200")) {
				new DisplayUaser().execute();

			}
		}
	}
	private class DisplayUaser extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		String success = "";
		//String MSG;
		JSONObject json , json2;
		JSONArray jArray;

		protected void onPreExecute() {
			dialog = ProgressDialog.show(HomeActivity.this, "","Loading, please wait...", true);
		}

		@SuppressWarnings("unchecked")
		protected Boolean doInBackground(Void... unused) {

			String url = Constant.VIEW_USER+"user_id="+User_Id;

			HttpResponse responseGet;
			try {
				HttpClient client = new DefaultHttpClient();  

				Log.v("URL DAta", url);
				HttpGet get = new HttpGet(url);

				try {
					responseGet = client.execute(get); 
				} catch (Exception e) {
					// TODO: handle exception
					return null;
				}

				HttpEntity resEntityGet = responseGet.getEntity();  
				if (resEntityGet != null) {  
					InputStream instream = resEntityGet.getContent();
					String result = Refrence.convertStreamToString(instream);
					json = new JSONObject(result);
					Log.d("re" + result, "msg");					
					json2 = json.getJSONObject("response");
					//MSG = json2.getString("msg");
					success = json2.getString("code");
					if (success.equalsIgnoreCase("200")) {
						inviteList.clear();
						jArray = json2.getJSONArray("data");
						for(int i=0;i<jArray.length();i++){							
							JSONObject json_data = jArray.getJSONObject(i); 							  
							HashMap map = new HashMap<String, String>();
							map.put("Username",json_data.get("name").toString());
							map.put("Type", json_data.get("relation_type").toString());
							inviteList.add(map);
						}							
					} 
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return null;
		}
		protected void onPostExecute(Boolean unused) {
			super.onPostExecute(unused);			
			if (success.equalsIgnoreCase("200")) {
				adapter = new SimpleAdapter(HomeActivity.this, inviteList, R.layout.listuser, new String[] { "Username" },new int[]{R.id.textView_usrname});
				_listUser.setAdapter(adapter);
				dialog.dismiss();
			}else{
				dialog.dismiss();
			}
		}
	}
	

	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// dialog = ProgressDialog.show(HomeActivity.this, "","Loading, please wait...", true);

		}
		protected Bitmap doInBackground(String... args) {
			try {
				bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {

			if(image != null){
				ImageView_Profile.setImageBitmap(image);
				// dialog.dismiss();

			}else{
			}
		}
	}
	//=============================End========================

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Closing Application")
			.setMessage("Are you sure you want to close?")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();    
				}
			})
			.setNegativeButton("No", null)
			.show();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//===================Flury===============
	public void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "VZZFBN2NYBNTVX5VCYCQ");
	}
	public void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	//================End=======================
}
