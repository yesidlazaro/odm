package com.yelamastudios.android.mdm;

import static com.yelamastudios.android.mdm.CommonUtilities.Logd;
import static com.yelamastudios.android.mdm.CommonUtilities.getVAR;

import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.yelamastudios.android.mdm.R;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	// Label to display GCM messages
	TextView lblMessage;
	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	// Connection detector
	ConnectionDetector cd;
	GoogleCloudMessaging gcm;
	Context context;
	String regId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = getApplicationContext();
		cd = new ConnectionDetector(context);
		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this, "Internet Connection Error", "Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		lblMessage = (TextView) findViewById(R.id.lblMessage);
		Logd(TAG, "Starting message receiver.");
		registerReceiver(mHandleMessageReceiver, new IntentFilter("com.yelamastudios.android.mdm.DISPLAY_MESSAGE"));
		// Get GCM registration id
		Logd(TAG, "Starting registration procedure.");
		new RegisterBackground().execute();
	}

	@Override
	public void onDestroy() {
		Logd(TAG, "Stopping message receiver.");
		unregisterReceiver(mHandleMessageReceiver);
		super.onDestroy();
	}

	public void editSettings(View view) {
		Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
		startActivity(i);
		finish();
	}

	class RegisterBackground extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			try {
				Logd(TAG, "Checking if GCM is null.");
				if (gcm == null) {
					Logd(TAG, "It is, initializing.");
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				Logd(TAG, "Registering.");
				regId = gcm.register(getVAR("SENDER_ID"));
				Logd(TAG, "Device registered, registration ID=" + regId);
				SharedPreferences mPrefs = getSharedPreferences("usersettings", 0);
				SharedPreferences.Editor mEditor = mPrefs.edit();
				mEditor.putString("REG_ID", regId).commit();
				// We are never setting the application REG_ID, but it shouldn't matter, as the service will refresh
				Logd(TAG, "Executing server registration.");
				ServerUtilities.register(context, regId);
			} catch (IOException ex) {
				Logd(TAG, "Error :" + ex.getMessage());
			}
			return null;
		}
	}

	// Receive screen notifications
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString("message");
			if (newMessage != null) {
				// Waking up mobile if it is sleeping
				WakeLocker.acquire(getApplicationContext());
				// Showing received message
				Date date = new Date();
				lblMessage.append(date.toString() + ": " + newMessage + "\n");
				// Releasing wake lock
				WakeLocker.release();
			} else {
				Logd(TAG, "Got a NULL message");
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				Logd(TAG, "Admin enabled!");
			} else {
				Logd(TAG, "Admin enable FAILED!");
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
