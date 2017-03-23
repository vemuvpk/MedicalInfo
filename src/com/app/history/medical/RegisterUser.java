package com.app.history.medical;

import java.util.Calendar;

import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.history.medical.util.ConnectionDetector;
import com.app.history.medical.util.Constant;
import com.app.history.medical.util.MyHttpClient;

public class RegisterUser extends Activity{
	ConnectionDetector _cd;
	EditText _firstName,_LastName,_email,_number,_password,_conPass,edit_dob;
	Button _register;
	SharedPreferences _preference;
	ImageView _imgCal;
	private int mYear;
	private int mMonth;
	private int mDay;

	static final int DATE_DIALOG_ID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		init();
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		//updateDisplay();
	}

	private void updateDisplay() {
		edit_dob.setText(
				new StringBuilder()
				// Month is 0 based so add 1	                
				.append(mDay).append("-")
				.append(mMonth + 1).append("-")
				.append(mYear).append(" "));
	}
	private DatePickerDialog.OnDateSetListener mDateSetListener =
			new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, 
				int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this,
					mDateSetListener,
					mYear, mMonth, mDay);
		}
		return null;	    	
	}
	public void init() {

		_firstName        = (EditText)findViewById(R.id.editfisrt_name);
		_LastName         = (EditText)findViewById(R.id.edit_lastname);
		_email            = (EditText)findViewById(R.id.edit_email);
		_number           = (EditText)findViewById(R.id.edit_phone);
		_password         = (EditText)findViewById(R.id.edit_paswrd);
		_conPass          = (EditText)findViewById(R.id.edit_paswrd2);
		edit_dob          = (EditText)findViewById(R.id.edit_dob);
		
		//_bloodGroup       = (EditText)findViewById(R.id.edit_blood);
		_register         = (Button)findViewById(R.id.button_register);
		_imgCal    =  (ImageView) findViewById(R.id.imageViewdob);
		_cd               = new ConnectionDetector(RegisterUser.this);
		_preference       = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		_imgCal.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		edit_dob.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Constant.toastCall(RegisterUser.this, "Please select DOB from calander");
				
			}
		});
		
		_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(_cd.isConnectingToInternet()){
					if(!_firstName.getText().toString().equals("")){
						if(!_LastName.getText().toString().equals("")){
							if(!_email.getText().toString().equals("")){
								if(checkEmail(_email.getText().toString().trim())){
									if(!_number.getText().toString().equals("")){
										if(!_password.getText().toString().equals("")){
											if(!_conPass.getText().toString().equals("")){
												if(_password.getText().toString().equals(_conPass.getText().toString())){
													if(!edit_dob.getText().toString().equals("")){
													new Register().execute();
													}else{
													Constant.toastCall(RegisterUser.this, "Please select Date of birth first");
														}
												}
												else{
													Constant.toastCall(RegisterUser.this, "Passwords not matching");
												}
											}
											else{
												Constant.toastCall(RegisterUser.this, "Please enter confirm password");
											}
										}
										else{
											Constant.toastCall(RegisterUser.this, "Please enter password");
										}
									}
									else{
										Constant.toastCall(RegisterUser.this, "Please enter phone number");
									}
								}
								else{
									Constant.toastCall(RegisterUser.this, "Please enter valid email");
								}
							}
							else{
								Constant.toastCall(RegisterUser.this, "Please enter email");
							}
						}
						else{
							Constant.toastCall(RegisterUser.this, "Please enter lastname");
						}
					}
					else{
						Constant.toastCall(RegisterUser.this, "Please enter firstname");
					}
				}
				else{
					Constant.toastCall(RegisterUser.this, "Please connect to network first");
				}
			}
		});

	}
	private boolean checkEmail(String email) {
		return Constant.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
	//===============Web serivce Call============================
	private class Register extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		String success = "error",_msg="";
		JSONObject json2;
		JSONObject json3;
		protected void onPreExecute() {
			dialog = ProgressDialog.show(RegisterUser.this, "","", true);

		}

		protected Boolean doInBackground(Void... unused) {
			try {

				MyHttpClient client = new MyHttpClient(Constant.Register_Url);
				client.connectForMultipart();			
				client.addFormPart("last_name", 	    _LastName.getText().toString());
				client.addFormPart("first_name", 			_firstName.getText().toString());
				client.addFormPart("email", 				_email.getText().toString());
				client.addFormPart("pwd", 				_password.getText().toString());
				client.addFormPart("profile_pic", 				"");
				client.addFormPart("phone", 				_number.getText().toString());
				client.addFormPart("blood_group", 				"");
				client.addFormPart("dob", 				edit_dob.getText().toString());

				client.finishMultipart();
				Log.v("client","client"+client);
				String data = client.getResponse();
				Log.v("RESPONSE", data);
				//				success = WebserviceParser.addExpenseParser(MainActivity.this,data);
				json2 = new JSONObject(data);
				json3 = json2.getJSONObject("response");
				success = json3.getString("code");
				_msg = json3.getString("msg");
				
				Log.d("Mess", "Mess"+success);
			
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
			if ((success.equalsIgnoreCase("200"))) {
				try {

					SharedPreferences.Editor prefsEditor = _preference.edit();
					prefsEditor.putString(Constant.APP_VERIFY, 	  "no");
					prefsEditor.putString(Constant.LOGIN_TYPE, 	  Constant.APP_LOGIN);
					prefsEditor.putString("user_id", json3.getString("id"));
					prefsEditor.putString("first_name", json3.getString("first_name"));
					prefsEditor.putString("last_name", json3.getString("last_name"));
					prefsEditor.putString("email", json3.getString("email"));
					prefsEditor.putString("Phone", json3.getString("phone"));
					prefsEditor.putString("DOB", json3.getString("dob"));
					prefsEditor.putInt("no_time_in_day",  0);
					prefsEditor.putInt("InsideVal",  0);
					prefsEditor.putString("NoficationStatus", "ON");
					prefsEditor.commit();
					Toast.makeText(RegisterUser.this, "Verification code is sent to your mail", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(RegisterUser.this,ActivateCodeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);
					finish();

				}
				catch(Exception e){
					e.printStackTrace();
				}

			}else if( (success.equalsIgnoreCase("100"))){
				Toast.makeText(RegisterUser.this, _msg, Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(RegisterUser.this, _msg, Toast.LENGTH_LONG).show();
			}
//======================End===========================

		}
	}
}
