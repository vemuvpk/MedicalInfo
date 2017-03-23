package com.app.history.medical;



import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.app.history.medical.util.Constant;
import com.app.history.medical.util.Refrence;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class AddNewActivity extends Activity {
	EditText UserName,RelationType;
	TextView _textDate;
	Button Yes,No;
	String _newname,_newrelation,User_Id;
	ImageView Dob_cal;
	SharedPreferences prfs;
	private int mYear;
	private int mMonth;
	private int mDay;
	SharedPreferences _preference;
	AlarmManager am;

	static final int DATE_DIALOG_ID = 0;
	Long NotificationDatemillis;
	int count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_user_activity);
		init();	
		_preference    = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		updateDisplay();
		Yes.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				if(!UserName.getText().toString().trim().equalsIgnoreCase("")){
					if(!RelationType.getText().toString().trim().equalsIgnoreCase("")){
						if(Refrence.isOnline(AddNewActivity.this)){
							_newname     = UserName.getText().toString().trim();
							_newrelation = RelationType.getText().toString().trim();
							new AddNewUser().execute();														
						}else{
							Toast.makeText(AddNewActivity.this, "Internet not available", Toast.LENGTH_LONG).show();
						}
					}else{
						Toast.makeText(AddNewActivity.this, "Please enter relation with user", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(AddNewActivity.this, "Please enter member name", Toast.LENGTH_LONG).show();
				}
			}
		});		
		No.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		Dob_cal.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});
	}

	public void init() {	
		UserName                    = (EditText)findViewById(R.id.User_name);
		RelationType                = (EditText)findViewById(R.id.Relation_type);	
		Yes                        = (Button)findViewById(R.id.Yes);
		_textDate  =  (TextView)  findViewById(R.id.text_date);
		Dob_cal  =  (ImageView)  findViewById(R.id.dob_cal);

		No                        = (Button)findViewById(R.id.No);
		prfs = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		User_Id = prfs.getString("user_id","").toString();

	}

	private void updateDisplay() {
		_textDate.setText(
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

	private class AddNewUser extends AsyncTask<Void, Void, Boolean> {
		public ProgressDialog dialog;
		String success = "";
		JSONObject json , json2;
		JSONArray jArray;
		protected void onPreExecute() {
			dialog = ProgressDialog.show(AddNewActivity.this, "","Loading, please wait...", true);
		}
		protected Boolean doInBackground(Void... unused) {

			String url = Constant.ADD_USER+"user_id="+User_Id+"&relation_type="+RelationType.getText().toString().trim()+"&name="+UserName.getText().toString().trim()+"&dob="+_textDate.getText().toString().trim();

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
			dialog.dismiss();
			if (success.equalsIgnoreCase("200")) {

				DateFormat formatter ; 
				Date date ; 
				formatter = new SimpleDateFormat("dd-MM-yyyy");
				try {
					date = (Date)formatter.parse(_textDate.getText().toString().trim());
					getAge(date);					    
					if(getAge(date) < 4 &&  _preference.getInt("InsideVal",0) == 0){	
						count=0;			    	
						SharedPreferences.Editor prefsEditor = _preference.edit();				
						prefsEditor.putString("Child",  "1");
						prefsEditor.putInt("InsideVal",  1);
						prefsEditor.commit();						
						callAsynchronousTaskChild();										
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Intent intent=new Intent(AddNewActivity.this, HomeActivity.class);			
				startActivity(intent);
				finish();
			}else{

			}
		}
	}
	public int getAge(Date dateOfBirth)      {                                                                                                                                                                         

		Calendar  today = Calendar.getInstance(); 
		Calendar birthDate = Calendar.getInstance();

		birthDate.setTime(dateOfBirth);
		if (birthDate.after(today)) {
			throw new IllegalArgumentException("Can't be born in the future");
		}

		int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
		int  month = today.get(Calendar.MONTH) - birthDate.get(Calendar.MONTH);

		if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
				(birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
			int days = birthDate.get(Calendar.DAY_OF_MONTH) - today.get(Calendar.DAY_OF_MONTH);
			age--;
			Toast.makeText(getApplicationContext(), "inside if", Toast.LENGTH_SHORT).show();
			Log.e("month is",month+"");
			Log.e("Days",days+ " left");


		}else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
				(birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
			Toast.makeText(getApplicationContext(), "inside else if", Toast.LENGTH_SHORT).show();

			age--;
		}
		return age;
	}	
	//Child
	public void callAsynchronousTaskChild() {
		final Handler handler = new Handler();
		final Timer timer = new Timer();

		TimerTask doAsynchronousTask = new TimerTask() {       
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {       
						try{							 
							if(_preference.getString("Child","").equals("1")){
								count++;
								if(count==1){
									NotificationDatemillis = System.currentTimeMillis()+(15*24*60*60*1000);		
								}else if(count==2){	
									SharedPreferences.Editor prefsEditor = _preference.edit();				
									prefsEditor.putString("DrugReq",  "BCG + OPV (zero dose) +HepB1");									
									prefsEditor.commit();
									setOneTimeAlarm();
									NotificationDatemillis = System.currentTimeMillis()+(6*7*24*60*60*1000);
								}else if(count==3){
									SharedPreferences.Editor prefsEditor = _preference.edit();				
									prefsEditor.putString("DrugReq",  "OPV1 +IPV1 + DPT1* + HepB2 + Hib1 + Rotavirus1 + PCV1");									
									prefsEditor.commit();
									setOneTimeAlarm();
									NotificationDatemillis = System.currentTimeMillis()+(10*7*24*60*60*1000);
								}else if(count==4){
									SharedPreferences.Editor prefsEditor = _preference.edit();				
									prefsEditor.putString("DrugReq",  "OPV2 + IPV2 + DPT2* + Hib2 + Rotavirus2 + PCV2");									
									prefsEditor.commit();
									setOneTimeAlarm();
									NotificationDatemillis = System.currentTimeMillis()+(14*7*24*60*60*1000);
								}else if(count==5){
									SharedPreferences.Editor prefsEditor = _preference.edit();				
									prefsEditor.putString("DrugReq",  "OPV3 + IPV3 + DPT3* + Hib3 + Rotavirus3# + PCV3");									
									prefsEditor.commit();
									setOneTimeAlarm();
									NotificationDatemillis = System.currentTimeMillis()+(6*30*24*60*60*1000);
								}else if(count==6){
									SharedPreferences.Editor prefsEditor = _preference.edit();				
									prefsEditor.putString("DrugReq",  "HepB3 + OPV1");									
									prefsEditor.commit();
									setOneTimeAlarm();
									NotificationDatemillis = System.currentTimeMillis()+(9*30*24*60*60*1000);
								}else if(count==7){
									SharedPreferences.Editor prefsEditor = _preference.edit();				
									prefsEditor.putString("DrugReq",  "Measles vaccine + OPV2");									
									prefsEditor.commit();
									setOneTimeAlarm();
									NotificationDatemillis = System.currentTimeMillis()+(12*30*24*60*60*1000);
								}else if(count==8){
									SharedPreferences.Editor prefsEditor = _preference.edit();				
									prefsEditor.putString("DrugReq",  "Hepatitis A1");									
									prefsEditor.commit();
									setOneTimeAlarm();
									NotificationDatemillis = System.currentTimeMillis()+(15*30*24*60*60*1000);
								}else if(count==9){
									SharedPreferences.Editor prefsEditor = _preference.edit();				
									prefsEditor.putString("DrugReq",  "MMR1 + Varicella + PCV booster");									
									prefsEditor.commit();
									setOneTimeAlarm();
									NotificationDatemillis = System.currentTimeMillis()+(18*30*24*60*60*1000);
								}else if(count==10){
									SharedPreferences.Editor prefsEditor = _preference.edit();				
									prefsEditor.putString("DrugReq",  "OPV4 + IPV booster1 + DPT*booster1 + Hib booster1 + Hepatitis A2");									
									prefsEditor.commit();
									setOneTimeAlarm();
									NotificationDatemillis = System.currentTimeMillis()+(24*30*24*60*60*1000);
								}else if(count==11){
									SharedPreferences.Editor prefsEditor = _preference.edit();				
									prefsEditor.putString("DrugReq",  "Typhoid1 (give repeat shots every 3 years)");									
									prefsEditor.commit();
									setOneTimeAlarm();
									NotificationDatemillis = System.currentTimeMillis()+(36*30*24*60*60*1000);
								}	
								else if(count==12){
									setOneTimeAlarm();
									SharedPreferences.Editor prefsEditor = _preference.edit();				
									prefsEditor.putString("Child",  "0");
									prefsEditor.putInt("InsideVal",  0);
									prefsEditor.commit();
									count=0;
								}	
							}else{
								Log.d("Out","out");
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
						}

					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, NotificationDatemillis); //execute in every 50000 ms
	}

	public void setOneTimeAlarm() {
		Intent intent = new Intent(this, TimeAlarm.class);
		intent.putExtra("drugName", _preference.getString("DrugReq","")); 
		intent.putExtra("name", _preference.getString("name","")); 
		intent.putExtra("NotifID", 1); 
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_ONE_SHOT);
		am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), pendingIntent);
	}

}
