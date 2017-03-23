package com.app.history.medical;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.app.history.medical.util.ConnectionDetector;
import com.app.history.medical.util.Constant;
import com.app.history.medical.util.Refrence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivateCodeActivity extends Activity {
	EditText _acivCode;
	Button _done;
	SharedPreferences _preference;
	ConnectionDetector _cd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activecode_activity);
		_acivCode     = (EditText)findViewById(R.id.activate_code);
		_done         = (Button)findViewById(R.id.button_done);
		_preference   = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		_cd           = new ConnectionDetector(ActivateCodeActivity.this);

		_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(_cd.isConnectingToInternet()){
					if(!_acivCode.getText().toString().equals("")){
						new ActivateUser().execute();
					}
					else{
						Toast.makeText(ActivateCodeActivity.this, "Please enter code", Toast.LENGTH_LONG).show();
					}
				}
				else{
					Toast.makeText(ActivateCodeActivity.this, "Please connect to network", Toast.LENGTH_LONG).show();
				}
			}
		});

	}
	private class ActivateUser extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		String success = "";
		JSONObject json , json2;
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ActivateCodeActivity.this, "","Loading, please wait...", true);
		}

		protected Boolean doInBackground(Void... unused) {

			String url = Constant.ACTIVATE_ACCOUNT+"user_id="+_preference.getString("user_id", "")+"&ac_code="+_acivCode.getText().toString().trim();
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
						SharedPreferences.Editor prefsEditor = _preference.edit();
						prefsEditor.putString(Constant.APP_VERIFY, 	  "yes");
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
				Intent intent = new Intent(ActivateCodeActivity.this,HomeActivity.class);
				intent.putExtra("Direct", "1");
				startActivity(intent);
				finish();
			}else{
				Toast.makeText(ActivateCodeActivity.this, "Please enter valid verification code", Toast.LENGTH_LONG).show();
			}
		}
	}
}
