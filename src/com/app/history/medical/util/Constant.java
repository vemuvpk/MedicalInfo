package com.app.history.medical.util;

import java.util.regex.Pattern;

import android.content.Context;
import android.widget.Toast;


public class Constant {

	
	public static String BASE_URL      = "http://medicalrecords.mobi/medicalHistory/";
	public static String Fb_Regsiter   = BASE_URL+"fb_login.php";
	public static String Login_Url     = BASE_URL+"login.php?";
	public static String Register_Url  = BASE_URL+"register.php";
	public static String ADD_USER      = BASE_URL+"add_members.php?";
	public static String VIEW_USER     = BASE_URL+"view_members.php?";
	public static String ACTIVATE_ACCOUNT      = BASE_URL+"activate_account.php?";
	public static String LOGIN_TYPE    = "logintype";
	public static String APP_LOGIN     = "applogin";
	public static String FB_LOGIN      = "fblogin";
	public static String APP_VERIFY    = "verify";
	public static String PREFERENCE_NAME    = "medicalhistory";
	public static String ADD_PRESCRIPTION      = BASE_URL+"add_prescription.php";
	//public static String AddPrescription_Url  = BASE_URL+"add_prescription.php";
	public static String Forgot_Url  = BASE_URL+"forgot_pass.php?";
	public static String USER_History    = BASE_URL+"prescription_detail.php?";
	public static String USER_Profile    = BASE_URL+"update_profile.php?";
	public static String Reset_Password    = BASE_URL+"reset_password.php?";
	public static String ProfileImage    = BASE_URL+"profile/";
	

	public static void toastCall(Context con,String message) {
		// TODO Auto-generated method stub
		Toast.makeText(con, message, Toast.LENGTH_LONG).show();
	}
	public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

}
