package com.app.history.medical;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.xml.sax.Parser;

import com.app.history.medical.util.ConnectionDetector;
import com.app.history.medical.util.Constant;
import com.app.history.medical.util.MyHttpClient;
import com.app.history.medical.util.SlidingAct;
import com.flurry.android.FlurryAgent;


import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class AddPrescription extends SlidingAct implements OnClickListener{
	ImageView menubtn;
	TextView _textDate;
	ImageView _imgCal , _imgSlip , _imgAddNew;
	Button _btnSubmit;
	EditText _editTab1 , _editTab2 , _editTab3 , _editTab4 , _editTab5 , _editTab6;
	EditText _editDay1 , _editDay2 , _editDay3 , _editDay4 , _editDay5 , _editDay6;
	CheckBox _checkMor1 , _checkAftr1 , _checkEve1 , _checkMor2 , _checkAftr2 , _checkEve2 ,
	_checkMor3 , _checkAftr3 , _checkEve3 , _checkMor4 , _checkAftr4 , _checkEve4 ,
	_checkMor5 , _checkAftr5 , _checkEve5 , _checkMor6 , _checkAftr6 , _checkEve6;
	int Mor1=0 , Aftr1=0 , Eve1=0 , Mor2=0 , Aftr2=0 , Eve2=0 ,
			Mor3=0 , Aftr3=0 , Eve3=0 , Mor4=0 , Aftr4=0 , Eve4=0 ,
			Mor5=0 , Aftr5=0 , Eve5=0 , Mor6=0 , Aftr6=0 , Eve6=0;
	LinearLayout _lay1 , _lay2 , _lay3 , _lay4 , _lay5 , _lay6;
	private int _newOpn = 0;

	private int mYear;
	private int mMonth;
	private int mDay;

	static final int DATE_DIALOG_ID = 0;
	File imageFile;
	public static final int PICK_FROM_CAMERA     = 1;
	public static final int PICK_FROM_FILE       = 2;
	Bitmap picBitmap;
	byte [] picarrray = null;
	ArrayList<HashMap<String , String>> _arrList;
	ConnectionDetector _cd;
	int _addSerial = 0;
	SharedPreferences _preference;
	private ProgressDialog dialog;

	AlarmManager am;
	long NotificationDatemillis,Inmillisecond;
	int callvalue=0;
	int count;
	
	long NotificationDatemillis_second,Inmillisecond_second;
	int count_second;

	String FirstName,LastName,RelationType,UserType;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prescription);

		init();

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		updateDisplay();
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

	public void init(){
		_textDate  =  (TextView)  findViewById(R.id.text_presdate);
		_imgCal    =  (ImageView) findViewById(R.id.image_cal);
		_imgSlip   =  (ImageView) findViewById(R.id.image_slip);
		_imgAddNew =  (ImageView) findViewById(R.id.image_addnew);
		_btnSubmit =  (Button)    findViewById(R.id.button_submit);
		_editTab1  =  (EditText)  findViewById(R.id.text_tab1);
		_editTab2  =  (EditText)  findViewById(R.id.text_tab2);
		_editTab3  =  (EditText)  findViewById(R.id.text_tab3);
		_editTab4  =  (EditText)  findViewById(R.id.text_tab4);
		_editTab5  =  (EditText)  findViewById(R.id.text_tab5);
		_editTab6  =  (EditText)  findViewById(R.id.text_tab6);	
		_editDay1  =  (EditText)  findViewById(R.id.text_day1);
		_editDay2  =  (EditText)  findViewById(R.id.text_day2);
		_editDay3  =  (EditText)  findViewById(R.id.text_day3);
		_editDay4  =  (EditText)  findViewById(R.id.text_day4);
		_editDay5  =  (EditText)  findViewById(R.id.text_day5);
		_editDay6  =  (EditText)  findViewById(R.id.text_day6);	
		_checkMor1 =  (CheckBox)  findViewById(R.id.check_tabmor1);
		_checkMor2 =  (CheckBox)  findViewById(R.id.check_tabmor2);
		_checkMor3 =  (CheckBox)  findViewById(R.id.check_tabmor3);
		_checkMor4 =  (CheckBox)  findViewById(R.id.check_tabmor4);
		_checkMor5 =  (CheckBox)  findViewById(R.id.check_tabmor5);
		_checkMor6 =  (CheckBox)  findViewById(R.id.check_tabmor6);
		_checkAftr1=  (CheckBox)  findViewById(R.id.check_tabaftr1);
		_checkAftr2=  (CheckBox)  findViewById(R.id.check_tabaftr2);
		_checkAftr3=  (CheckBox)  findViewById(R.id.check_tabaftr3);
		_checkAftr4=  (CheckBox)  findViewById(R.id.check_tabaftr4);
		_checkAftr5=  (CheckBox)  findViewById(R.id.check_tabaftr5);
		_checkAftr6=  (CheckBox)  findViewById(R.id.check_tabaftr6);
		_checkEve1 =  (CheckBox)  findViewById(R.id.check_tabeve1);
		_checkEve2 =  (CheckBox)  findViewById(R.id.check_tabeve2);
		_checkEve3 =  (CheckBox)  findViewById(R.id.check_tabeve3);
		_checkEve4 =  (CheckBox)  findViewById(R.id.check_tabeve4);
		_checkEve5 =  (CheckBox)  findViewById(R.id.check_tabeve5);
		_checkEve6 =  (CheckBox)  findViewById(R.id.check_tabeve6);
		_lay1      =  (LinearLayout) findViewById(R.id.lay1);
		_lay2      =  (LinearLayout) findViewById(R.id.lay2);
		_lay3      =  (LinearLayout) findViewById(R.id.lay3);
		_lay4      =  (LinearLayout) findViewById(R.id.lay4);
		_lay5      =  (LinearLayout) findViewById(R.id.lay5);
		_lay6      =  (LinearLayout) findViewById(R.id.lay6);


		_imgCal.setOnClickListener(this);
		_imgSlip.setOnClickListener(this);
		_imgAddNew.setOnClickListener(this);
		_btnSubmit.setOnClickListener(this);
		_checkMor1.setOnClickListener(this);
		_checkMor2.setOnClickListener(this);
		_checkMor3.setOnClickListener(this);
		_checkMor4.setOnClickListener(this);
		_checkMor5.setOnClickListener(this);
		_checkMor6.setOnClickListener(this);
		_checkAftr1.setOnClickListener(this);
		_checkAftr2.setOnClickListener(this);
		_checkAftr3.setOnClickListener(this);
		_checkAftr4.setOnClickListener(this);
		_checkAftr5.setOnClickListener(this);
		_checkAftr6.setOnClickListener(this);
		_checkEve1.setOnClickListener(this);
		_checkEve2.setOnClickListener(this);
		_checkEve3.setOnClickListener(this);
		_checkEve4.setOnClickListener(this);
		_checkEve5.setOnClickListener(this);
		_checkEve6.setOnClickListener(this);

		_arrList       = new ArrayList<HashMap<String , String>>();
		_cd            = new ConnectionDetector(AddPrescription.this);
		_preference    = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);

		Intent intent = getIntent();
		FirstName = intent.getStringExtra("UserFirstName");
		LastName = intent.getStringExtra("UserLastName");
		RelationType = intent.getStringExtra("Relation");
		UserType= intent.getStringExtra("TypeUser");
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

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
		case R.id.image_cal:
			showDialog(DATE_DIALOG_ID);
			//showDialog(DATE_DIALOG_ID);
			break;
		case R.id.image_slip:
			dialogshow();
			break;
		case R.id.image_addnew:
			openNew();
			break;
		case R.id.button_submit:
			if(_arrList.size() > 0){
				_arrList.clear();
			}
			_addSerial = 0;
			if(!_textDate.getText().toString().trim().equalsIgnoreCase("")){
				//if(picarrray != null){
				if(_cd.isConnectingToInternet()){
					submit1();
				}else{
					Toast.makeText(AddPrescription.this, "Internet not available", Toast.LENGTH_LONG).show();
				}
				//}else{
				//	Toast.makeText(AddPrescription.this, "Please select image first", Toast.LENGTH_LONG).show();
				//}
			}else{
				Toast.makeText(AddPrescription.this, "Please enter prescription date", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.check_tabmor1:
			if(Mor1 == 0){
				Mor1 = 1;

			}else{
				Mor1 = 0;
			}
			break;
		case R.id.check_tabaftr1:
			if(Aftr1 == 0){
				Aftr1 = 1;
			}else{
				Aftr1 = 0;
			}
			break;
		case R.id.check_tabeve1:
			if(Eve1 == 0){
				Eve1 = 1;
			}else{
				Eve1 = 0;
			}
			break;
		case R.id.check_tabmor2:
			if(Mor2 == 0){
				Mor2 = 1;
			}else{
				Mor2 = 0;
			}
			break;
		case R.id.check_tabaftr2:
			if(Aftr2 == 0){
				Aftr2 = 1;
			}else{
				Aftr2 = 0;
			}
			break;
		case R.id.check_tabeve2:
			if(Eve2 == 0){
				Eve2 = 1;
			}else{
				Eve2 = 0;
			}
			break;
		case R.id.check_tabmor3:
			if(Mor3 == 0){
				Mor3 = 1;
			}else{
				Mor3 = 0;
			}
			break;
		case R.id.check_tabaftr3:
			if(Aftr3 == 0){
				Aftr3 = 1;
			}else{
				Aftr3 = 0;
			}
			break;
		case R.id.check_tabeve3:
			if(Eve3 == 0){
				Eve3 = 1;
			}else{
				Eve3 = 0;
			}
			break;
		case R.id.check_tabmor4:
			if(Mor4 == 0){
				Mor4 = 1;
			}else{
				Mor4 = 0;
			}
			break;
		case R.id.check_tabaftr4:
			if(Aftr4 == 0){
				Aftr4 = 1;
			}else{
				Aftr4 = 0;
			}
			break;
		case R.id.check_tabeve4:
			if(Eve4 == 0){
				Eve4 = 1;
			}else{
				Eve4 = 0;
			}
			break;
		case R.id.check_tabmor5:
			if(Mor5 == 0){
				Mor5 = 1;
			}else{
				Mor5 = 0;
			}
			break;
		case R.id.check_tabaftr5:
			if(Aftr5 == 0){
				Aftr5 = 1;
			}else{
				Aftr5 = 0;
			}
			break;
		case R.id.check_tabeve5:
			if(Eve5 == 0){
				Eve5 = 1;
			}else{
				Eve5 = 0;
			}
			break;
		case R.id.check_tabmor6:
			if(Mor6 == 0){
				Mor6 = 1;
			}else{
				Mor6 = 0;
			}
			break;
		case R.id.check_tabaftr6:
			if(Aftr6 == 0){
				Aftr6 = 1;
			}else{
				Aftr6 = 0;
			}
			break;
		case R.id.check_tabeve6:
			if(Eve6 == 0){
				Eve6 = 1;
			}else{
				Eve6 = 0;
			}
			break;

		}
	}

	public void openNew(){
		if(_newOpn == 0){
			_lay2.setVisibility(View.VISIBLE);
			_newOpn = 1;
		}else if(_newOpn == 1){
			_lay3.setVisibility(View.VISIBLE);
			_newOpn = 2;
		}else if(_newOpn == 2){
			_lay4.setVisibility(View.VISIBLE);
			_newOpn = 3;
		}else if(_newOpn == 3){
			_lay5.setVisibility(View.VISIBLE);
			_newOpn = 4;
		}else if(_newOpn == 4){
			_lay6.setVisibility(View.VISIBLE);
			_newOpn = 5;
		}
	}
//============= Putting data into Array===============================
	public void submitAll(){
		dialog = ProgressDialog.show(AddPrescription.this, "","Loading, please wait...", true);
		new AddPresc().execute();
	}

	public void submit1(){
		if(!_editTab1.getText().toString().trim().equalsIgnoreCase("")){
			if(!_editDay1.getText().toString().trim().equalsIgnoreCase("") && !_editDay1.getText().toString().trim().equalsIgnoreCase("0")){
				if(Mor1 == 0 && Aftr1 == 0 && Eve1 == 0){
					Toast.makeText(AddPrescription.this, "Please select atleast one timing for Tab1", Toast.LENGTH_LONG).show();
				}else{
					if(_newOpn > 0){
						HashMap<String , String> _hashMap = new HashMap<String , String>();
						_hashMap.put("tab", _editTab1.getText().toString().trim());
						_hashMap.put("day", _editDay1.getText().toString().trim());
						_hashMap.put("mor", ""+Mor1);
						_hashMap.put("aftr", ""+Aftr1);
						_hashMap.put("eve", ""+Eve1);
						_arrList.add(_hashMap);
						submit2();

					}else{
						HashMap<String , String> _hashMap = new HashMap<String , String>();
						_hashMap.put("tab", _editTab1.getText().toString().trim());
						_hashMap.put("day", _editDay1.getText().toString().trim());
						_hashMap.put("mor", ""+Mor1);
						_hashMap.put("aftr", ""+Aftr1);
						_hashMap.put("eve", ""+Eve1);
						_arrList.add(_hashMap);
						submitAll();
					}
				}
			}else{
				Toast.makeText(AddPrescription.this, "Please Enter days for Tab1", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(AddPrescription.this, "Please Enter Tab1 name", Toast.LENGTH_LONG).show();
		}
	}

	public void submit2(){
		if(!_editTab2.getText().toString().trim().equalsIgnoreCase("")){
			if(!_editDay2.getText().toString().trim().equalsIgnoreCase("") && !_editDay2.getText().toString().trim().equalsIgnoreCase("0")){
				if(Mor2 == 0 && Aftr2 == 0 && Eve2 == 0){
					Toast.makeText(AddPrescription.this, "Please select atleast one timing for Tab2", Toast.LENGTH_LONG).show();
				}else{
					if(_newOpn > 1){
						Log.d("Guru1", "Guru1");
						HashMap<String , String> _hashMap = new HashMap<String , String>();
						_hashMap.put("tab", _editTab2.getText().toString().trim());
						_hashMap.put("day", _editDay2.getText().toString().trim());
						_hashMap.put("mor", ""+Mor2);
						_hashMap.put("aftr", ""+Aftr2);
						_hashMap.put("eve", ""+Eve2);
						_arrList.add(_hashMap);
						submit3();
					}else{
						HashMap<String , String> _hashMap = new HashMap<String , String>();
						_hashMap.put("tab", _editTab2.getText().toString().trim());
						_hashMap.put("day", _editDay2.getText().toString().trim());
						_hashMap.put("mor", ""+Mor2);
						_hashMap.put("aftr", ""+Aftr2);
						_hashMap.put("eve", ""+Eve2);
						_arrList.add(_hashMap);
						submitAll();
					}
				}
			}else{
				Toast.makeText(AddPrescription.this, "Please Enter days for Tab2", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(AddPrescription.this, "Please Enter Tab2 name", Toast.LENGTH_LONG).show();
		}
	}

	public void submit3(){
		if(!_editTab3.getText().toString().trim().equalsIgnoreCase("")){
			if(!_editDay3.getText().toString().trim().equalsIgnoreCase("") && !_editDay3.getText().toString().trim().equalsIgnoreCase("0")){
				if(Mor3 == 0 && Aftr3 == 0 && Eve3 == 0){
					Toast.makeText(AddPrescription.this, "Please select atleast one timing for Tab3", Toast.LENGTH_LONG).show();
				}else{
					if(_newOpn > 2){
						Log.d("Guru2", "Guru2");
						HashMap<String , String> _hashMap = new HashMap<String , String>();
						_hashMap.put("tab", _editTab3.getText().toString().trim());
						_hashMap.put("day", _editDay3.getText().toString().trim());
						_hashMap.put("mor", ""+Mor3);
						_hashMap.put("aftr", ""+Aftr3);
						_hashMap.put("eve", ""+Eve3);
						_arrList.add(_hashMap);
						submit4();
					}else{
						HashMap<String , String> _hashMap = new HashMap<String , String>();
						_hashMap.put("tab", _editTab3.getText().toString().trim());
						_hashMap.put("day", _editDay3.getText().toString().trim());
						_hashMap.put("mor", ""+Mor3);
						_hashMap.put("aftr", ""+Aftr3);
						_hashMap.put("eve", ""+Eve3);
						_arrList.add(_hashMap);
						submitAll();
					}
				}
			}else{
				Toast.makeText(AddPrescription.this, "Please Enter days for Tab3", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(AddPrescription.this, "Please Enter Tab3 name", Toast.LENGTH_LONG).show();
		}
	}

	public void submit4(){
		if(!_editTab4.getText().toString().trim().equalsIgnoreCase("")){
			if(!_editDay4.getText().toString().trim().equalsIgnoreCase("") && !_editDay4.getText().toString().trim().equalsIgnoreCase("0")){
				if(Mor4 == 0 && Aftr4 == 0 && Eve4 == 0){
					Toast.makeText(AddPrescription.this, "Please select atleast one timing for Tab4", Toast.LENGTH_LONG).show();
				}else{
					if(_newOpn > 3){
						HashMap<String , String> _hashMap = new HashMap<String , String>();
						_hashMap.put("tab", _editTab4.getText().toString().trim());
						_hashMap.put("day", _editDay4.getText().toString().trim());
						_hashMap.put("mor", ""+Mor4);
						_hashMap.put("aftr", ""+Aftr4);
						_hashMap.put("eve", ""+Eve4);
						_arrList.add(_hashMap);
						submit5();
					}else{
						HashMap<String , String> _hashMap = new HashMap<String , String>();
						_hashMap.put("tab", _editTab4.getText().toString().trim());
						_hashMap.put("day", _editDay4.getText().toString().trim());
						_hashMap.put("mor", ""+Mor4);
						_hashMap.put("aftr", ""+Aftr4);
						_hashMap.put("eve", ""+Eve4);
						_arrList.add(_hashMap);
						submitAll();
					}
				}
			}else{
				Toast.makeText(AddPrescription.this, "Please Enter days for Tab4", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(AddPrescription.this, "Please Enter Tab4 name", Toast.LENGTH_LONG).show();
		}
	}

	public void submit5(){
		if(!_editTab5.getText().toString().trim().equalsIgnoreCase("")){
			if(!_editDay5.getText().toString().trim().equalsIgnoreCase("") && !_editDay5.getText().toString().trim().equalsIgnoreCase("0")){
				if(Mor5 == 0 && Aftr5 == 0 && Eve5 == 0){
					Toast.makeText(AddPrescription.this, "Please select atleast one timing for Tab5", Toast.LENGTH_LONG).show();
				}else{
					if(_newOpn > 4){
						HashMap<String , String> _hashMap = new HashMap<String , String>();
						_hashMap.put("tab", _editTab5.getText().toString().trim());
						_hashMap.put("day", _editDay5.getText().toString().trim());
						_hashMap.put("mor", ""+Mor5);
						_hashMap.put("aftr", ""+Aftr5);
						_hashMap.put("eve", ""+Eve5);
						_arrList.add(_hashMap);
						submit6();
					}else{
						HashMap<String , String> _hashMap = new HashMap<String , String>();
						_hashMap.put("tab", _editTab5.getText().toString().trim());
						_hashMap.put("day", _editDay5.getText().toString().trim());
						_hashMap.put("mor", ""+Mor5);
						_hashMap.put("aftr", ""+Aftr5);
						_hashMap.put("eve", ""+Eve5);
						_arrList.add(_hashMap);
						submitAll();
					}
				}
			}else{
				Toast.makeText(AddPrescription.this, "Please Enter days for Tab5", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(AddPrescription.this, "Please Enter Tab5 name", Toast.LENGTH_LONG).show();
		}
	}

	public void submit6(){
		if(!_editTab6.getText().toString().trim().equalsIgnoreCase("")){
			if(!_editDay6.getText().toString().trim().equalsIgnoreCase("") && !_editDay6.getText().toString().trim().equalsIgnoreCase("0")){
				if(Mor6 == 0 && Aftr6 == 0 && Eve6 == 0){
					Toast.makeText(AddPrescription.this, "Please select atleast one timing for Tab6", Toast.LENGTH_LONG).show();
				}else{
					HashMap<String , String> _hashMap = new HashMap<String , String>();
					_hashMap.put("tab", _editTab6.getText().toString().trim());
					_hashMap.put("day", _editDay6.getText().toString().trim());
					_hashMap.put("mor", ""+Mor6);
					_hashMap.put("aftr", ""+Aftr6);
					_hashMap.put("eve", ""+Eve6);
					_arrList.add(_hashMap);
					submitAll();
				}
			}else{
				Toast.makeText(AddPrescription.this, "Please Enter days for Tab6", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(AddPrescription.this, "Please Enter Tab6 name", Toast.LENGTH_LONG).show();
		}
	}
	//========================End=========================================

	//================= Selecting Prescription Images===========================
	private void dialogshow() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
		"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(AddPrescription.this);
		builder.setTitle("Add prescription image!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					if(picBitmap != null && !picBitmap.isRecycled()) {
						picBitmap.recycle();
						picBitmap = null; // null reference
					}
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
					try {
						intent.putExtra("return-data", true);
						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						// Do nothing for now
					}
				} else if (items[item].equals("Choose from Library")) {
					if(picBitmap != null && !picBitmap.isRecycled()) {
						picBitmap.recycle();
						picBitmap = null; // null reference
					}
					Intent intent = new Intent(Intent.ACTION_PICK);
					// call android default gallery
					intent.setType("image/*");
					//				intent.setAction(Intent.ACTION_GET_CONTENT);
					try {
						intent.putExtra("return-data", true);

						startActivityForResult(Intent.createChooser(intent,
								"Complete action using"), PICK_FROM_FILE);

					} catch (ActivityNotFoundException e) {
						// Do nothing for now
					}
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data ) {
		//				Log.v("Activity Result", "Called"+data.getData().getPath());

		switch ( requestCode ) {
		case PICK_FROM_FILE:
			if ( resultCode == RESULT_OK ) {     
				try {
					if ( picBitmap != null ) {
						picBitmap.recycle();
					}
					_imgSlip.setImageBitmap(null);
					imageFile = new File(getRealPathFromURI(data.getData()));
					Log.v("img_path", imageFile.getPath());
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;
					picBitmap = BitmapFactory.decodeFile(imageFile.getPath(), options);
					//					bitmap = getRoundedCornerBitmap(picBitmap);
					Drawable d  =  new BitmapDrawable(getResources(), picBitmap);
					_imgSlip.setBackgroundDrawable(d);
					ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
					picBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

					picarrray = baos.toByteArray(); 

				} catch (Exception e) {
					e.printStackTrace();
				} 
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(getApplicationContext(), "User cancelled image selection. ",Toast.LENGTH_SHORT).show();
			} else {
				// failed to capture image
				Toast.makeText(getApplicationContext(),"Sorry! Failed to select image",Toast.LENGTH_SHORT).show();
			}

			break;

		case PICK_FROM_CAMERA:
			if (resultCode == RESULT_OK) {
				try {

					if ( picBitmap != null ) {
						picBitmap.recycle();
					}
					BitmapFactory.Options options = new BitmapFactory.Options();
					// downsizing image as it throws OutOfMemory Exception for larger images
					options.inSampleSize = 4;
					Bundle extras = data.getExtras();
					if (extras != null) {
						_imgSlip.setImageBitmap(null);
						picBitmap = extras.getParcelable("data");
						//							bitmap = getRoundedCornerBitmap(picBitmap);

						Drawable d  =  new BitmapDrawable(getResources(), picBitmap);
						_imgSlip.setBackgroundDrawable(d);

						ByteArrayOutputStream baos = new ByteArrayOutputStream();  
						picBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
						picarrray = baos.toByteArray(); 

					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(getApplicationContext(), "User cancelled image capture",Toast.LENGTH_SHORT).show();
			} else {
				// failed to capture image
				Toast.makeText(getApplicationContext(),"Sorry! Failed to capture image",Toast.LENGTH_SHORT).show();
			}
			break;

		}
	}
	private String getRealPathFromURI(Uri contentURI) {
		Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
		if (cursor == null) { // Source is Dropbox or other similar local file path
			return contentURI.getPath();
		} else { 
			cursor.moveToFirst(); 
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
			return cursor.getString(idx); 
		}
	}
	//==============================End========================
	
	//=========Web Services call================================

	private class AddPresc extends AsyncTask<Void, Void, Boolean> {
		String success = "error";
		int countNumber=0;
		protected void onPreExecute() {

		}
		protected Boolean doInBackground(Void... unused) {
			JSONObject mainObject;
			try {
				MyHttpClient client = new MyHttpClient(Constant.ADD_PRESCRIPTION);
				client.connectForMultipart();
				client.addFormPart("user_id", 	    _preference.getString("user_id", ""));
				client.addFormPart("member_id", 	 UserType);
				client.addFormPart("pre_datetime",   _textDate.getText().toString().trim());
				client.addFormPart("drug_name", 	 _arrList.get(_addSerial).get("tab"));
				client.addFormPart("no_time_in_day",  _arrList.get(_addSerial).get("day"));
				if(picarrray != null){
					client.addFilePart("image",      "image"+System.currentTimeMillis()+".jpg",       picarrray);	
				}
				client.addFormPart("morning",    _arrList.get(_addSerial).get("mor"));
				client.addFormPart("afternoon",  _arrList.get(_addSerial).get("aftr"));
				client.addFormPart("night",      _arrList.get(_addSerial).get("eve"));
				client.addFormPart("name",      FirstName);
				client.finishMultipart();
				String data = client.getResponse();
				Log.d("RESPONSE","Respone"+data);
				mainObject 			  = new JSONObject(data);
				success               = mainObject.getString("Response");			

			}
			catch(Throwable t) {
				t.printStackTrace();
				return null;
			}
			return null;
		}

		protected void onPostExecute(Boolean unused) {
			super.onPostExecute(unused);		
			int DayValue = Integer.parseInt(_arrList.get(_addSerial).get("day"));
			String DrogName=_arrList.get(_addSerial).get("tab");
			
			String Morning=_arrList.get(_addSerial).get("mor");
			String AfterNoon=_arrList.get(_addSerial).get("aftr");
			String Night=_arrList.get(_addSerial).get("eve");
						
			if(Morning.equals("1") && AfterNoon.equals("1") && Night.equals("1")){
				countNumber=3;			
			}else if(Morning.equals("0") && AfterNoon.equals("0")){
				countNumber=1;		
			}else if(Morning.equals("0") && Night.equals("0")){
				countNumber=1;			
			}else if(AfterNoon.equals("0") && Night.equals("0")){
				countNumber=1;				
			}else{
				countNumber=2;			
			}		
			if(DayValue > callvalue){				
				callvalue=DayValue;
				if(_preference.getInt("no_time_in_day",0) <= callvalue){
					Log.d("Sanju", "DDD"+callvalue);
					SharedPreferences.Editor prefsEditor = _preference.edit();				
					prefsEditor.putInt("no_time_in_day",  DayValue);			
					prefsEditor.putString("name",  FirstName);
					prefsEditor.putString("drugName",  DrogName);
					prefsEditor.putInt("CountNumber",  countNumber);
					prefsEditor.putInt("Inside",  0);
					prefsEditor.commit();	
				}else if(_preference.getInt("Inside",0) == 1){
					SharedPreferences.Editor prefsEditor = _preference.edit();				
					prefsEditor.putInt("no_time_in_day_second",  DayValue);			
					prefsEditor.putString("name_second",  FirstName);
					prefsEditor.putString("drugName_second",  DrogName);
					prefsEditor.putInt("CountNumber_second",  countNumber);
					prefsEditor.commit();	
				}else{
					
				}
			}
			_addSerial = _addSerial + 1;
			if(_addSerial < _arrList.size()){				
				new AddPresc().execute();
			}else{
				dialog.dismiss();
				picarrray = null;
				if(_preference.getInt("no_time_in_day",0) <= callvalue && _preference.getInt("Inside",0) == 0){				
					String dtStart = _textDate.getText().toString().trim();  
					DateFormat formatter ; 
					Date date ; 
					formatter = new SimpleDateFormat("dd-MM-yyyy");
					try {
						date = (Date)formatter.parse(dtStart);
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						if(_preference.getInt("CountNumber",0)==3){
							 NotificationDatemillis = cal.getTimeInMillis()+(1*24*60*60*1000)+(7*60*60*1000);									
						}else{
							NotificationDatemillis = cal.getTimeInMillis()+(1*24*60*60*1000)+(9*60*60*1000)+(30*60*1000);
						}							
						Inmillisecond=5*60*1000;
						count=0;
						callAsynchronousTask();					
						System.out.println("Today is " +date.getTime());
						SharedPreferences.Editor prefsEditor = _preference.edit();				
						prefsEditor.putInt("Inside",  1);	
						prefsEditor.putLong("interval",  NotificationDatemillis);
						prefsEditor.putLong("NoOfDays",  _preference.getInt("no_time_in_day",0)*24*60*60*1000);
						prefsEditor.commit();
						Log.d("Helloaa", "hi"+date);
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d("Hello", "hi"+e.getMessage());

					} 
				}else if(!_preference.getString("name","").equals(FirstName) &&  _preference.getInt("Inside",0) == 1){
					
					String dtStart = _textDate.getText().toString().trim();  
					DateFormat formatter ; 
					Date date ; 
					formatter = new SimpleDateFormat("dd-MM-yyyy");
					try {
						date = (Date)formatter.parse(dtStart);
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						if(_preference.getInt("CountNumber_second",0)==3){
							 NotificationDatemillis_second = cal.getTimeInMillis()+(1*24*60*60*1000)+(7*60*60*1000)+(15*60*1000);									
						}else{
							NotificationDatemillis_second = cal.getTimeInMillis()+(1*24*60*60*1000)+(9*60*60*1000)+(35*60*1000);
						}							
						Inmillisecond_second=8*60*1000;
						count_second=0;
						callAsynchronousTaskSecond();					
						System.out.println("Today is " +date.getTime());
						SharedPreferences.Editor prefsEditor = _preference.edit();				
						prefsEditor.putLong("interval_second",  NotificationDatemillis_second);
						prefsEditor.putLong("NoOfDays_second",  _preference.getInt("no_time_in_day_second",0)*24*60*60*1000);
						prefsEditor.commit();
						Log.d("Helloaa", "hi"+date);
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d("Hello", "hi"+e.getMessage());

					} 

				}else{
					
				}
				Toast.makeText(AddPrescription.this, "Successfully added", Toast.LENGTH_LONG).show();
				Intent _intent = new Intent(AddPrescription.this,UserActivity.class);		
				_intent.putExtra("UserFirstName", FirstName);
				_intent.putExtra("UserLastName", LastName);
				_intent.putExtra("Relation", RelationType);
				_intent.putExtra("TypeUser", UserType);
				startActivity(_intent);			
				finish();
			}
		}
	}
	
	//===========================End===================================
	
	//==================Notification scheduler===============================
	public void setOneTimeAlarm() {
		Intent intent = new Intent(this, TimeAlarm.class);	
		intent.putExtra("drugName", _preference.getString("drugName","")); 
		intent.putExtra("name", _preference.getString("name","")); 
		//intent.putExtra("name", _preference.getInt("name",0));
		intent.putExtra("NotifID", 1); 
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_ONE_SHOT);
		am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), pendingIntent);
	}
	public void setRepeatingAlarm() {
		Intent intent = new Intent(this, TimeAlarm.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),(7*60*60*1000), pendingIntent);
		am.cancel(pendingIntent);
	}
	public void callAsynchronousTask() {
		final Handler handler = new Handler();
		final Timer timer = new Timer();

		TimerTask doAsynchronousTask = new TimerTask() {       
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {       
						try{									
							if(System.currentTimeMillis() >= _preference.getLong("interval",0) && System.currentTimeMillis() <= _preference.getInt("NoOfDays",0)){
								if(_preference.getInt("CountNumber",0)==2){
									count++;
									if(count%2 == 0){
										Inmillisecond=(4*60*60*1000)+(28*60*1000);
										
										if(_preference.getString("NoficationStatus","").toString().equals("ON")){
											setOneTimeAlarm();
											SharedPreferences.Editor prefsEditor = _preference.edit();					
											prefsEditor.putLong("interval",  System.currentTimeMillis()+Inmillisecond);
											prefsEditor.commit();
										}else{
										}
									}else{
										Inmillisecond=(19*60*60*1000)+(30*60*1000);
										
										if(_preference.getString("NoficationStatus","").toString().equals("ON")){
											setOneTimeAlarm();
											SharedPreferences.Editor prefsEditor = _preference.edit();					
											prefsEditor.putLong("interval",  System.currentTimeMillis()+Inmillisecond);
											prefsEditor.commit();
										}else{

										}
									}
								}else if(_preference.getInt("CountNumber",0)==1){
									Inmillisecond=(24*60*60*1000)+(30*60*1000);									
									if(_preference.getString("NoficationStatus","").toString().equals("ON")){
										setOneTimeAlarm();
										SharedPreferences.Editor prefsEditor = _preference.edit();					
										prefsEditor.putLong("interval",  System.currentTimeMillis()+Inmillisecond);
										prefsEditor.commit();
									}else{

									}
								}else{
									count++;
									if(count%4 == 0){									
										Inmillisecond=(10*60*60*1000);
										if(_preference.getString("NoficationStatus","").toString().equals("ON")){
											setOneTimeAlarm();
											SharedPreferences.Editor prefsEditor = _preference.edit();					
											prefsEditor.putLong("interval",  System.currentTimeMillis()+Inmillisecond);
											prefsEditor.commit();
										}else{
										}	
									}else{								
										Inmillisecond=(7*60*60*1000);
										if(_preference.getString("NoficationStatus","").toString().equals("ON")){
											setOneTimeAlarm();
											SharedPreferences.Editor prefsEditor = _preference.edit();					
											prefsEditor.putLong("interval",  System.currentTimeMillis()+Inmillisecond);
											prefsEditor.commit();
										}else{

										}
									}	
								}
							}else{
								Log.d("Out","Out");
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
						}

					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, Inmillisecond); //execute in every 50000 ms
	}
	
	//Second Timer
	
	public void callAsynchronousTaskSecond() {
		final Handler handlersecond = new Handler();
		final Timer timersecond = new Timer();
		TimerTask doAsynchronousTasksecond = new TimerTask() {       
			@Override
			public void run() {
				handlersecond.post(new Runnable() {
					public void run() {       
						try{									
							if(System.currentTimeMillis() >= _preference.getLong("interval_second",0) && System.currentTimeMillis() <= _preference.getInt("NoOfDays_second",0)){
								if(_preference.getInt("CountNumber_second",0)==2){
									count_second++;
									if(count_second%2 == 0){
										Inmillisecond_second=(4*60*60*1000)+(28*60*1000);
										
										if(_preference.getString("NoficationStatus","").toString().equals("ON")){
											setOneTimeAlarm();
											SharedPreferences.Editor prefsEditor = _preference.edit();					
											prefsEditor.putLong("interval_second",  System.currentTimeMillis()+Inmillisecond_second);
											prefsEditor.commit();
										}else{
										}
									}else{
										Inmillisecond_second=(19*60*60*1000)+(30*60*1000);
										
										if(_preference.getString("NoficationStatus","").toString().equals("ON")){
											setOneTimeAlarm();
											SharedPreferences.Editor prefsEditor = _preference.edit();					
											prefsEditor.putLong("interval_second",  System.currentTimeMillis()+Inmillisecond_second);
											prefsEditor.commit();
										}else{

										}
									}
								}else if(_preference.getInt("CountNumber_second",0)==1){
									Inmillisecond_second=(24*60*60*1000)+(30*60*1000);									
									if(_preference.getString("NoficationStatus","").toString().equals("ON")){
										setOneTimeAlarm();
										SharedPreferences.Editor prefsEditor = _preference.edit();					
										prefsEditor.putLong("interval_second",  System.currentTimeMillis()+Inmillisecond_second);
										prefsEditor.commit();
									}else{

									}
								}else{
									count_second++;
									if(count_second%4 == 0){									
										Inmillisecond_second=(10*60*60*1000);
										if(_preference.getString("NoficationStatus","").toString().equals("ON")){
											setOneTimeAlarm();
											SharedPreferences.Editor prefsEditor = _preference.edit();					
											prefsEditor.putLong("interval_second",  System.currentTimeMillis()+Inmillisecond_second);
											prefsEditor.commit();
										}else{
										}	
									}else{								
										Inmillisecond_second=(7*60*60*1000);	
										if(_preference.getString("NoficationStatus","").toString().equals("ON")){
											setOneTimeAlarm();
											SharedPreferences.Editor prefsEditor = _preference.edit();					
											prefsEditor.putLong("interval_second",  System.currentTimeMillis()+Inmillisecond_second);
											prefsEditor.commit();
										}else{

										}
									}	
								}

							}else{
								Log.d("Out","Out");
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
						}

					}
				});
			}
		};
		timersecond.schedule(doAsynchronousTasksecond, 0, Inmillisecond_second); 
	}
	// ==================== End=====================
	
	//====== Flury==============================

	public void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "VZZFBN2NYBNTVX5VCYCQ");
	}
	public void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
//===================End===========================
}
