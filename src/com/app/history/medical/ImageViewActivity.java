package com.app.history.medical;

import java.io.InputStream;
import java.net.URL;

import com.app.history.medical.util.Constant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ImageViewActivity extends Activity {

	ImageView ImageView_Prescription;
	Bitmap bitmap;
	LinearLayout ImageView_prescription_view;
	ProgressDialog pDialog;
	SharedPreferences _preference;
	String Profile_pic,User_Name,Image,NoOfTime,NoOfDays;
	TextView TextViewDrug,TextViewNo,Textnumberdays;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.imageview_activity);
		Intent intent = getIntent();
		User_Name = intent.getStringExtra("UserFirstName");
		Image = intent.getStringExtra("Image");
		NoOfTime = intent.getStringExtra("NoOfTime");
		NoOfDays = intent.getStringExtra("NoOfDays");
		ImageView_Prescription=(ImageView)findViewById(R.id.imageView_prescription);	
		TextViewDrug=(TextView)findViewById(R.id.textViewDrug);
		TextViewNo=(TextView)findViewById(R.id.textnumber);
		Textnumberdays=(TextView)findViewById(R.id.textnumberdays);
		
		ImageView_prescription_view=(LinearLayout)findViewById(R.id.imageView_prescription_view);
		if(!Image.equals("")){
		ImageView_prescription_view.setVisibility(View.VISIBLE);
		}else{
			
		}
		TextViewDrug.setText(User_Name);
		TextViewNo.setText("Need to use-: "+NoOfTime);
		if(NoOfDays.equals("1")){
			Textnumberdays.setText("For Continuously "+NoOfDays+" Day");
		}else{
			Textnumberdays.setText("For Continuously "+NoOfDays+" Days");
		}
		
		ImageView_Prescription.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ImageViewActivity.this, MultiTouchActivity.class);			
				intent.putExtra("Image", Image);	
				startActivity(intent);
			}
		});
		//new LoadImage().execute(Profile_pic); 
		new LoadImage().execute(Image); 

	}
	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ImageViewActivity.this);
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
				ImageView_Prescription.setImageBitmap(image);
				pDialog.dismiss();

			}else{
				pDialog.dismiss();

			}
		}
	}
}
