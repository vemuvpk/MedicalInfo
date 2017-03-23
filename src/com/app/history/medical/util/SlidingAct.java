package com.app.history.medical.util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.app.history.medical.HomeActivity;
import com.app.history.medical.MainActivity;
import com.app.history.medical.ProfileActivity;
import com.app.history.medical.R;
import com.app.history.medical.SettingActivity;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivityBase;
import com.slidingmenu.lib.app.SlidingActivityHelper;

@SuppressWarnings("deprecation")
public class SlidingAct extends Activity  implements SlidingActivityBase{
	private SlidingActivityHelper mHelper;	
	SharedPreferences _preference;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		_preference       = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setBehindLeftContentView(R.layout.menubar);
		this.setSlidingActionBarEnabled(true);		
		showMenu();				
	} 
	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return mHelper.findViewById(id);
	}
	@Override
	public void setContentView(int id){
		setContentView(getLayoutInflater().inflate(id, null));
	}
	@Override
	public void setContentView(View v){
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
	}
	public void setContentView(View v, LayoutParams params){
		super.setContentView(v, params);
		mHelper.registerAboveContentView(v, params);
	}
	// behind left view
	public void setBehindLeftContentView(int id) {
		setBehindLeftContentView(getLayoutInflater().inflate(id, null));
	}
	public void setBehindLeftContentView(View v){
		setBehindLeftContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
	}
	@Override
	public void setBehindLeftContentView(View v,
			android.view.ViewGroup.LayoutParams p) {
		// TODO Auto-generated method stub
		mHelper.setBehindLeftContentView(v);
	}
	@Override
	public void setBehindRightContentView(View v,
			android.view.ViewGroup.LayoutParams p) {
		// TODO Auto-generated method stub
		mHelper.setBehindRightContentView(v);
	}
	// behind right view
	public void setBehindRightContentView(int id) {
		setBehindRightContentView(getLayoutInflater().inflate(id, null));
	}
	public void setBehindRightContentView(View v) {
		setBehindRightContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	public SlidingMenu getSlidingMenu() {
		return mHelper.getSlidingMenu();
	}
	public void toggle(int side) {
		mHelper.toggle(side);
	}
	public void showAbove() {
		mHelper.showAbove();
	}
	public void showBehind(int side) {
		mHelper.showBehind(side);
	}
	public void setSlidingActionBarEnabled(boolean b) {
		mHelper.setSlidingActionBarEnabled(b);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		boolean b = mHelper.onKeyUp(keyCode, event);
		if (b)
			return b;
		return super.onKeyUp(keyCode, event);
	}
	protected void showMenu() {
		// TODO Auto-generated method stub	
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow,SlidingDrawer.SCROLLBAR_POSITION_DEFAULT);
		getSlidingMenu().setBehindOffsetRes(R.dimen.actionbar_home_width,SlidingMenu.LEFT);
		getSlidingMenu().setBehindScrollScale(0.25f, SlidingMenu.LEFT);showBehind(SlidingMenu.LEFT);		
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate(savedInstanceState);
		SharedPreferences user_prefe = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = user_prefe.edit();
		findViewById(R.id.menubar_linear1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				toggle(256);
				Intent i = new Intent(SlidingAct.this , HomeActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
				finish();
			}
		});
		findViewById(R.id.menubar_linear2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				toggle(256);
				// TODO Auto-generated method stub
				Intent i = new Intent(SlidingAct.this , ProfileActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
				finish();
			}
		});
		
		
		findViewById(R.id.menubar_linear3).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				toggle(256);
				// TODO Auto-generated method stub
				Intent i = new Intent(SlidingAct.this , SettingActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
				finish();
			}
		});
		findViewById(R.id.menubar_linear5).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				toggle(256);
				_preference = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);	
				if(_preference.getString(Constant.LOGIN_TYPE,"").toString().equals(Constant.FB_LOGIN)){
					SharedPreferences.Editor prefsEditor = _preference.edit();
					prefsEditor.putString("user_id", "");
					prefsEditor.putString("first_name", "");
					prefsEditor.putString("last_name", "");
					prefsEditor.putString("email", "");
					prefsEditor.putString("Phone","");	
					prefsEditor.putString("blood_group", "");
					prefsEditor.putString("dob", "");
					prefsEditor.putString("profile_pic", "");	
					prefsEditor.putString("height", "");
					prefsEditor.putString("weight", "");	
					prefsEditor.putString("Logout", "true");
					prefsEditor.commit();
					Intent _intent = new Intent(SlidingAct.this , MainActivity.class);
					startActivity(_intent);			
					finish();
				}else{
					SharedPreferences.Editor prefsEditor = _preference.edit();
					prefsEditor.putString("user_id", "");
					prefsEditor.putString("first_name", "");
					prefsEditor.putString("last_name", "");
					prefsEditor.putString("email", "");
					prefsEditor.putString("Phone","");	
					prefsEditor.putString("blood_group", "");
					prefsEditor.putString("dob", "");
					prefsEditor.putString("profile_pic", "");	
					prefsEditor.putString("height", "");
					prefsEditor.putString("weight", "");					
					prefsEditor.commit();
					Intent _intent = new Intent(SlidingAct.this , MainActivity.class);
					startActivity(_intent);			
					finish();
				}						
			}
		});

	}
}
