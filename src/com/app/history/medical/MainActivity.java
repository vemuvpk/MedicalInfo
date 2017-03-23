package com.app.history.medical;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.history.medical.util.ConnectionDetector;
import com.app.history.medical.util.Constant;
import com.app.history.medical.util.MyHttpClient;
import com.app.history.medical.util.Refrence;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	ConnectionDetector _cd;
	private LoginButton loginBtn;
	EditText _userName,_Password;
	String _firstName,_lastName,_email,_id,_profileic,_msg="";
	private UiLifecycleHelper uiHelper;
	LinearLayout _login;
	SharedPreferences _preference;
	Bitmap _fbuserpic;
	TextView _signUp,_Forgot;
	CheckBox checkbox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();

	}
	public void init() {
		loginBtn                     = (LoginButton)findViewById(R.id.fb_login_button);
		_userName                    = (EditText)findViewById(R.id.username);
		_Password                    = (EditText)findViewById(R.id.password);
		_login                       = (LinearLayout)findViewById(R.id.Done);
		_signUp                      = (TextView) findViewById(R.id.signup);
		_Forgot                      = (TextView) findViewById(R.id.forgot);
		_preference       = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		
		 checkbox = (CheckBox)findViewById(R.id.termcondition);
        TextView textView = (TextView)findViewById(R.id.displayterm);
        
        checkbox.setText("");
        textView.setText(Html.fromHtml("I have read and agree to the " +
                "<a href='com.app.history.medical.TCActivity://Kode'>TERMS AND CONDITIONS</a>"));
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        
		_cd      = new ConnectionDetector(MainActivity.this);
		_login.setOnClickListener(this);
		_signUp.setOnClickListener(this);	
		_Forgot.setOnClickListener(this);	
		//  ************************************Facebook Integration************************  
		loginBtn.setReadPermissions(Arrays.asList("email","user_about_me","user_photos"));

		loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
			@Override
			public void onUserInfoFetched(GraphUser user) {
				if (user != null) {
					try{
						Log.v("", ""+user.toString());
						_firstName    = user.getName();
						_lastName     = user.getFirstName();
						_id           = user.getId();
						_profileic     = String.format("https://graph.facebook.com/%s",user.getId())+"/picture?type=large";

						if(user.asMap().containsKey("email") && user.asMap().get("email").toString()!= null && !user.asMap().get("email").toString().equalsIgnoreCase("")){
							_email = user.asMap().get("email").toString();
						}
						else{
							_email = "";
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					new FbRegister().execute();
				}
			} 

		});
	}


	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {

				Log.d("MainActivity", "Facebook session opened.");
			} else if (state.isClosed()) {
				Log.d("MainActivity", "Facebook session closed.");
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();

		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();

	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.d("MainActivity", "Facebook session opened.");
			if(_preference.getString("Logout","").toString().trim().equals("true")){
				session.closeAndClearTokenInformation();
				session.close();
				SharedPreferences.Editor prefsEditor = _preference.edit();
				prefsEditor.putString("Logout", "");
				prefsEditor.commit();
			}
		} else if (state.isClosed()) {
			Log.d("MainActivity", "Facebook session closed.");
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		Log.d("HeyGru", "HeyGru"+requestCode+ "  hi  " +resultCode +"  hi "+data);

	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}
	private class FbRegister extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		String success = "error";
		JSONObject json2;
		JSONObject json3;
		protected void onPreExecute() {
			dialog = ProgressDialog.show(MainActivity.this, "","", true);

		}

		protected Boolean doInBackground(Void... unused) {
			try {

				MyHttpClient client = new MyHttpClient(Constant.Fb_Regsiter);
				client.connectForMultipart();
				client.addFormPart("fb_id", 			_id);
				Log.v("fbid", "fbid"+_id);
				client.addFormPart("last_name", 	    _lastName);
				client.addFormPart("first_name", 			_firstName);
				client.addFormPart("email", 				_email);

				_fbuserpic = getBitmapFromURL(_profileic);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				_fbuserpic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] bytes = stream.toByteArray(); 
				stream.close();
				if(bytes!=null){

					client.addFilePart("profile_pic",      "profile_pic.jpg",       bytes);
				}
				client.finishMultipart();
				Log.v("client","client"+client);
				String data = client.getResponse();
				Log.v("RESPONSE", data);
				//				success = WebserviceParser.addExpenseParser(MainActivity.this,data);
				json2 = new JSONObject(data);
				json3 = json2.getJSONObject("response");
				success = json3.getString("code");
				_msg = json3.getString("msg");
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
			if ((success.equalsIgnoreCase("200")) || (success.equalsIgnoreCase("100"))) {
				try {

					SharedPreferences.Editor prefsEditor = _preference.edit();
					prefsEditor.putString(Constant.APP_VERIFY, 	  "yes");
					prefsEditor.putString(Constant.LOGIN_TYPE, 	  Constant.FB_LOGIN);
					prefsEditor.putString("user_id", json3.getString("id"));
					prefsEditor.putString("first_name", json3.getString("first_name"));
					prefsEditor.putString("last_name", json3.getString("last_name"));
					prefsEditor.putString("Phone", json2.getString("phone"));
					prefsEditor.putString("email", json3.getString("email"));
					prefsEditor.putString("blood_group", json2.getString("blood_group"));
					prefsEditor.putString("dob", json2.getString("dob"));
					prefsEditor.putString("profile_pic", json2.getString("profile_pic"));
					prefsEditor.putString("height", json2.getString("height"));
					prefsEditor.putString("weight", json2.getString("weight"));
					prefsEditor.putInt("no_time_in_day",  0);
					prefsEditor.putString("NoficationStatus", "ON");
					prefsEditor.putString("Logout", "false");
					prefsEditor.putInt("InsideVal",  0);
					prefsEditor.commit();
					Intent intent = new Intent(MainActivity.this,HomeActivity.class);
					intent.putExtra("Direct", "1");
					startActivity(intent);
					finish();
				}
				catch(Exception e){
					e.printStackTrace();
				}

			}
			else{
				Toast.makeText(MainActivity.this, "User not added", Toast.LENGTH_LONG).show();
				//Constant.toastCall(MainActivity.this, _msg);
			}


		}
	}
	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Done:
			
			if(checkbox.isChecked()){
			if(!_userName.getText().toString().trim().equalsIgnoreCase("")){
				if(!_Password.getText().toString().trim().equalsIgnoreCase("")){
					if(_cd.isConnectingToInternet()){
						new Login().execute();
					}else{
						Toast.makeText(MainActivity.this, "Internet not available", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(MainActivity.this, "Please enter password", Toast.LENGTH_LONG).show();
				}	
			}else{
				Toast.makeText(MainActivity.this, "Please enter username", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(MainActivity.this, "Please check the terms and conditions", Toast.LENGTH_LONG).show();
		}
			break;
		case R.id.signup:
			Intent intent = new Intent(MainActivity.this,RegisterUser.class);
			startActivity(intent);
			break;

		case R.id.forgot:
			Intent forgrtintent = new Intent(MainActivity.this,ForGotEmailActivity.class);
			startActivity(forgrtintent);
			//finish();
			break;
		}
	}
	private class Login extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		String success = "";
		String MSG;
		JSONObject json , json2;
		protected void onPreExecute() {
			dialog = ProgressDialog.show(MainActivity.this, "","Loading, please wait...", true);
		}

		protected Boolean doInBackground(Void... unused) {

			String url = Constant.Login_Url+"email="+_userName.getText().toString().trim()+"&pwd="+
					_Password.getText().toString().trim();

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
					MSG = json2.getString("msg");
					if (success.equalsIgnoreCase("200")){
						SharedPreferences.Editor prefsEditor = _preference.edit();
						prefsEditor.putString(Constant.APP_VERIFY, 	  "yes");
						prefsEditor.putString(Constant.LOGIN_TYPE, 	  Constant.APP_LOGIN);
						prefsEditor.putString("user_id", json2.getString("id"));
						prefsEditor.putString("first_name", json2.getString("first_name"));
						prefsEditor.putString("last_name", json2.getString("last_name"));
						prefsEditor.putString("email", json2.getString("email"));
						prefsEditor.putString("Phone", json2.getString("phone"));	
						prefsEditor.putString("blood_group", json2.getString("blood_group"));
						prefsEditor.putString("dob", json2.getString("dob"));
						prefsEditor.putString("profile_pic", json2.getString("profile_pic"));	
						prefsEditor.putString("height", json2.getString("height"));
						prefsEditor.putString("weight", json2.getString("weight"));
						prefsEditor.putString("NoficationStatus", "ON");
						prefsEditor.putInt("no_time_in_day",  0);
						prefsEditor.putInt("InsideVal",  0);

						prefsEditor.commit();
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
			dialog.dismiss();
			if (success.equalsIgnoreCase("200")) {
				Intent intent = new Intent(MainActivity.this,HomeActivity.class);
				intent.putExtra("Direct", "1");
				startActivity(intent);
				finish();
			}else if(success.equalsIgnoreCase("300")){
				Toast.makeText(MainActivity.this, MSG, Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(MainActivity.this, MSG, Toast.LENGTH_LONG).show();
			}
		}
	}
//==================Flury=================
	public void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "VZZFBN2NYBNTVX5VCYCQ");
	}
	public void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	//==================End==========================

}
