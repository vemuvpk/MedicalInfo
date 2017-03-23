package com.app.history.medical;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.history.medical.util.Constant;
import com.app.history.medical.util.MyHttpClient;
import com.app.history.medical.util.Refrence;
import com.app.history.medical.util.SlidingAct;
import com.flurry.android.FlurryAgent;

public class ProfileActivity extends SlidingAct {
	ImageView menubtn,profile_pic;
	Button Submit;
	SharedPreferences _preference;
	String User_Id,first_name,last_name,Emai_Id,Weight_User,Height_User,BloodGroup,Phone,Profile_pic;
	EditText _firstName,_LastName,_email,_number,Edit_blood,Dob,_register,Weigth,Height;

	File imageFile;
	public static final int PICK_FROM_CAMERA     = 1;
	public static final int PICK_FROM_FILE       = 2;
	Bitmap picBitmap;
	byte [] picarrray = null;


	Bitmap bitmap;
	ProgressDialog pDialog;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_activity);	
		init();
		_preference = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		if(_preference.getString(Constant.LOGIN_TYPE,"").toString().equals(Constant.FB_LOGIN)){
			if(!_preference.getString("profile_pic_face","").toString().equals("")){
				Profile_pic = _preference.getString("profile_pic_face","").toString();
				new LoadImage().execute(Profile_pic); 
			}else{

			}
		}else{
			if(!_preference.getString("profile_pic","").toString().equals("")){
				Profile_pic = _preference.getString("profile_pic","").toString();
				new LoadImage().execute(Profile_pic);  
			}else{

			}
		}
		profile_pic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogshow();
			}
		});

	}
	public void init(){
		_firstName        = (EditText)findViewById(R.id.editfisrt_myname);
		_LastName         = (EditText)findViewById(R.id.edit_mylastname);
		_email            = (EditText)findViewById(R.id.edit_emailaddress);
		_number           = (EditText)findViewById(R.id.edit_contact);
		Edit_blood         = (EditText)findViewById(R.id.edit_blood);
		Weigth       = (EditText)findViewById(R.id.edit_wait);
		Height       = (EditText)findViewById(R.id.editfisrt_hight);
		Submit         = (Button)findViewById(R.id.button_submit);
		profile_pic      = (ImageView)findViewById(R.id.profile_pic);


		_preference       = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		if(_preference.getString(Constant.LOGIN_TYPE,"").toString().equals(Constant.FB_LOGIN)){
			first_name = _preference.getString("first_name","").toString();
			last_name = _preference.getString("last_name","").toString();
			Emai_Id= _preference.getString("email","").toString();
			Weight_User= _preference.getString("Weight_face","").toString();
			Height_User= _preference.getString("Height_face","").toString();
			BloodGroup= _preference.getString("Bloodgroup_face","").toString();
			Phone= _preference.getString("Phone_face","").toString();
			User_Id = _preference.getString("user_id","").toString();
			_firstName.setText(first_name);
			_LastName.setText(last_name);
			_email.setText(Emai_Id);
			_number.setText(Phone);

			Weigth.setText(Weight_User);
			Height.setText(Height_User);
			Edit_blood.setText(BloodGroup);
		}else{
			first_name = _preference.getString("first_name","").toString();
			last_name = _preference.getString("last_name","").toString();
			Emai_Id= _preference.getString("email","").toString();
			Weight_User= _preference.getString("Weight","").toString();
			Height_User= _preference.getString("Height","").toString();
			BloodGroup= _preference.getString("Bloodgroup","").toString();
			Phone= _preference.getString("Phone","").toString();
			User_Id = _preference.getString("user_id","").toString();
			_firstName.setText(first_name);
			_LastName.setText(last_name);
			_email.setText(Emai_Id);
			_number.setText(Phone);
			Weigth.setText(Weight_User);
			Height.setText(Height_User);
			Edit_blood.setText(BloodGroup);
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
		Submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Refrence.isOnline(ProfileActivity.this)){
					new UpdateProfile().execute();
				}else{
					Toast.makeText(ProfileActivity.this, "Internet not available", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	private void dialogshow() {
		final CharSequence[] items = { "Take Photo", "Choose from Library","Cancel" };
		AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
		builder.setTitle("Add profile image!");
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
					profile_pic.setImageBitmap(null);
					imageFile = new File(getRealPathFromURI(data.getData()));
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;
					picBitmap = BitmapFactory.decodeFile(imageFile.getPath(), options);

					//					bitmap = getRoundedCornerBitmap(picBitmap);
					Drawable d  =  new BitmapDrawable(getResources(), picBitmap);
					profile_pic.setBackgroundDrawable(d);
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
				// successfully captured the image display it in image view
				try {

					if ( picBitmap != null ) {
						picBitmap.recycle();
					}
					//						// bimatp factory
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;
					Bundle extras = data.getExtras();
					if (extras != null) {
						profile_pic.setImageBitmap(null);
						picBitmap = extras.getParcelable("data");
						//							bitmap = getRoundedCornerBitmap(picBitmap);

						Drawable d  =  new BitmapDrawable(getResources(), picBitmap);
						profile_pic.setBackgroundDrawable(d);

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
		if (cursor == null) { 
			return contentURI.getPath();
		} else { 
			cursor.moveToFirst(); 
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
			return cursor.getString(idx); 
		}
	}

	private class UpdateProfile extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		String success = "";
		JSONObject json , json2;
		JSONArray jArray;
		String MSG;
		String imageUrl="";
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ProfileActivity.this, "","Loading, please wait...", true);
		}

		protected Boolean doInBackground(Void... unused) {

			try {
				MyHttpClient client = new MyHttpClient(Constant.USER_Profile);
				client.connectForMultipart();
				client.addFormPart("user_id", 	    User_Id);
				client.addFormPart("height", 	    Height.getText().toString().trim());
				client.addFormPart("weight", 	    Weigth.getText().toString().trim());
				client.addFormPart("blood_group", 	    Edit_blood.getText().toString().trim());
				client.addFormPart("medicines", 	    "");
				client.addFormPart("phone", 	    _number.getText().toString().trim());			
				if(picarrray != null){
					imageUrl="image"+System.currentTimeMillis()+".jpg";
					client.addFilePart("profile_pic",imageUrl,picarrray);	
				}
				client.finishMultipart();
				String data = client.getResponse();
				Log.d("RESPONSE","Respone"+data);
				json 			  = new JSONObject(data);
				json2 = json.getJSONObject("response");
				MSG = json2.getString("msg");
				success = json2.getString("code");
				Log.d("Gurukant", "Gurukant"+success);
				if (success.equalsIgnoreCase("200")) {

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
			if (success.equalsIgnoreCase("200")) {
				if(_preference.getString(Constant.LOGIN_TYPE,"").toString().equals(Constant.FB_LOGIN)){
					SharedPreferences.Editor prefsEditor = _preference.edit();					
					prefsEditor.putString("Height_face", Height.getText().toString());
					prefsEditor.putString("Weight_face", Weigth.getText().toString().trim());
					prefsEditor.putString("Bloodgroup_face",Edit_blood.getText().toString().trim());
					prefsEditor.putString("Phone_face",_number.getText().toString().trim());
					if(picarrray != null){
						prefsEditor.putString("profile_pic_face",Constant.ProfileImage+imageUrl);
						picarrray = null;
					}
					prefsEditor.putString("Height", "");
					prefsEditor.putString("Weight", "");
					prefsEditor.putString("Bloodgroup","");
					prefsEditor.putString("Phone","");
					prefsEditor.putString("profile_pic","");

					prefsEditor.commit();	
					Toast.makeText(ProfileActivity.this, MSG, Toast.LENGTH_LONG).show();
				}else{
					SharedPreferences.Editor prefsEditor = _preference.edit();					
					prefsEditor.putString("Height", Height.getText().toString());
					prefsEditor.putString("Weight", Weigth.getText().toString().trim());
					prefsEditor.putString("Bloodgroup",Edit_blood.getText().toString().trim());
					prefsEditor.putString("Phone",_number.getText().toString().trim());
					if(picarrray != null){
						prefsEditor.putString("profile_pic",Constant.ProfileImage+imageUrl);
						picarrray = null;
					}
					prefsEditor.putString("Height_face", "");
					prefsEditor.putString("Weight_face","");
					prefsEditor.putString("Bloodgroup_face","");
					prefsEditor.putString("Phone_face","");
					prefsEditor.putString("profile_pic_face","");
					prefsEditor.commit();	
					Toast.makeText(ProfileActivity.this, MSG, Toast.LENGTH_LONG).show();
				}

			}
		}
	}


	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ProfileActivity.this);
			pDialog.setMessage("Loading Image ....");
			pDialog.show();

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
				profile_pic.setImageBitmap(image);
				pDialog.dismiss();	          
			}else{          
				pDialog.dismiss();
				// Toast.makeText(ProfileActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

			}
		}
	}

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
	//===============Flurry=============
	public void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "VZZFBN2NYBNTVX5VCYCQ");
	}
	public void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	//=============End===================
}
