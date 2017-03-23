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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForGotEmailActivity extends Activity {
	EditText _UserEmai;
	Button _Done;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_update_password);
		init();	

		_Done.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Refrence.isOnline(ForGotEmailActivity.this)){
					if(!_UserEmai.getText().toString().equals("")){					
						if(checkEmail(_UserEmai.getText().toString().trim())){
							new ForgotPasswordEmail().execute();
						}else{
							Constant.toastCall(ForGotEmailActivity.this, "Please enter valid email");
						}
					}else{
						Constant.toastCall(ForGotEmailActivity.this, "Please enter email");	
					}
				}else{
					Toast.makeText(ForGotEmailActivity.this, "Please connect to network", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	public void init() {
		_UserEmai                    = (EditText)findViewById(R.id.useremailId);	
		_Done                        = (Button)findViewById(R.id.VarificationBtn);
	}
	//=================Web service Call==========================
	private class ForgotPasswordEmail extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		String success = "";
		String MSG;
		JSONObject json , json2;
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ForGotEmailActivity.this, "","Loading, please wait...", true);
		}
		protected Boolean doInBackground(Void... unused) {
			String url = Constant.Forgot_Url+"email="+_UserEmai.getText().toString().trim();
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
				Toast.makeText(ForGotEmailActivity.this, "Verification code is sent to your Email", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(ForGotEmailActivity.this,ForgotActivity.class);
				startActivity(intent);
				finish();
			}else if(success.equalsIgnoreCase("300")){
				Toast.makeText(ForGotEmailActivity.this, MSG, Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(ForGotEmailActivity.this, MSG, Toast.LENGTH_LONG).show();
			}
		}
	}
	//========================End ==================================
	private boolean checkEmail(String email) {
		return Constant.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
}
