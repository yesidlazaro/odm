package com.yelamastudios.android.mdm;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import static com.yelamastudios.android.mdm.CommonUtilities.Logd;

public class GetAdminReceiver extends DeviceAdminReceiver {

	private static final String TAG = "GetAdminReceiver";

	static SharedPreferences getSamplePreferences(Context context) {
		return context.getSharedPreferences(DeviceAdminReceiver.class.getName(), 0);
	}

	static String PREF_PASSWORD_QUALITY = "password_quality";
	static String PREF_PASSWORD_LENGTH = "password_length";
	static String PREF_MAX_FAILED_PW = "max_failed_pw";

	@Override
	public void onEnabled(Context context, Intent intent) {
		Logd(TAG, "ODM Device Admin: enabled");
	}

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		return "Disabling ODM device administration will keep ODM from functioning.";
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		Logd(TAG, "ODM Device Admin: disabled");
	}

	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		Logd(TAG, "Device Admin: pw changed");
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {
		Logd(TAG, "Device Admin: pw failed");
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		Logd(TAG, "Device Admin: pw succeeded");
	}
}
