package com.app.history.medical;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MultiTouchActivity extends Activity {
	Bitmap bitmap;
	ProgressDialog pDialog;
	SharedPreferences _preference;
	String Profile_pic,User_Name,Image;
	TouchImageView img;
	TextView TextViewDrug; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_zoom);
        
        img = new TouchImageView(this);
        Intent intent = getIntent();
		Image = intent.getStringExtra("Image");			
		new LoadImage().execute(Image); 	 
    }
    //================= Image load web service call=============
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MultiTouchActivity.this);
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
				 img.setImageBitmap(bitmap);		       
		        img.setMaxZoom(4f);
		        setContentView(img);
				pDialog.dismiss();

			}else{
				pDialog.dismiss();
			}
		}
	}
   //=============End===================================

}
