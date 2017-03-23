package com.app.history.medical;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.app.history.medical.util.Constant;
import com.app.history.medical.util.Refrence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.MailTo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ForgotActivity extends Activity {
	
EditText _UserEmail,_UserPassword,_ReenterPassword;
Button _Done;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_forgot);
		init();		
		_Done.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!_UserPassword.getText().toString().trim().equals("") || (!_UserPassword.getText().toString().trim().equals(" "))){
					if(_UserPassword.getText().toString().trim().equals(_ReenterPassword.getText().toString().trim())){
						if(Refrence.isOnline(ForgotActivity.this)){
						new ForgotPassword().execute();
						}else{
							Toast.makeText(ForgotActivity.this, "Network is not avaialble", Toast.LENGTH_LONG).show();	
						}
					}else{
						Toast.makeText(ForgotActivity.this, "Password doesn't match", Toast.LENGTH_LONG).show();
					}
					
				}else{
					Toast.makeText(ForgotActivity.this, "Password doesn't match", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	public void init() {
		_UserEmail                    = (EditText)findViewById(R.id.useremail);
		_UserPassword                = (EditText)findViewById(R.id.userpassword);
		_ReenterPassword             = (EditText)findViewById(R.id.reenter_password);
		_Done                        = (Button)findViewById(R.id.change_password);
	}
	
	// ================Web services call====================
	private class ForgotPassword extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		String success = "";
		String MSG;
		JSONObject json , json2;
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ForgotActivity.this, "","Loading, please wait...", true);
		}

		protected Boolean doInBackground(Void... unused) {
			String url = Constant.Reset_Password+"code="+_UserEmail.getText().toString().trim()+"&pwd="+_UserPassword.getText().toString().trim();
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
					json2 = json.getJSONObject("response");
					success = json2.getString("code");
					MSG = json2.getString("msg");
					if (success.equalsIgnoreCase("200")){
						
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
				Toast.makeText(ForgotActivity.this, "Password changed successfully", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(ForgotActivity.this,MainActivity.class);
				startActivity(intent);	
				finish();
			}else if(success.equalsIgnoreCase("300")){
				Toast.makeText(ForgotActivity.this, MSG, Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(ForgotActivity.this, MSG, Toast.LENGTH_LONG).show();
			}
		}
	}
	//=========================== End===============================
	
}
