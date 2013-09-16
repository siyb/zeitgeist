package org.geekosphere.zeitgeist.activity;

import org.geekosphere.zeitgeist.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class ZGPreferenceActivity extends PreferenceActivity implements OnPreferenceClickListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZGPreferenceActivity.class);

	public static final String KEY_INITIALIZED = "KEY_INITIALIZED";
	public static final String KEY_HOST = "KEY_HOST";
	public static final String KEY_EMAIL = "KEY_EMAIL";
	public static final String KEY_API_SECRET = "KEY_API_SECRET";
	public static final String KEY_SCAN_API_SECRET = "KEY_SCAN_API_SECRET";

	private EditTextPreference hostPreference;
	private EditTextPreference emailPreference;
	private EditTextPreference apiSecretPreference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.zgpreferenceactivity);

		hostPreference = (EditTextPreference) findPreference(KEY_HOST);

		emailPreference = (EditTextPreference) findPreference(KEY_EMAIL);
		apiSecretPreference = (EditTextPreference) findPreference(KEY_API_SECRET);
		findPreference(KEY_SCAN_API_SECRET).setOnPreferenceClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null && scanResult.getContents() != null) {
			String qrcode = scanResult.getContents();
			LOGGER.info("qrcode scan success: " + qrcode);

			// parse qrcode into url, email and secret:
			int auth_token = qrcode.indexOf("#auth:");
			if (auth_token != -1) {
				String baseUrl = qrcode.substring(0, auth_token);
				String eMail = qrcode.substring(auth_token + 6, qrcode.indexOf('|'));
				String apiSecret = qrcode.substring(qrcode.indexOf('|') + 1);

				LOGGER.debug("qrcode baseUrl: " + baseUrl);
				LOGGER.debug("qrcode eMail: " + eMail);

				hostPreference.setText(baseUrl);
				emailPreference.setText(eMail);
				apiSecretPreference.setText(apiSecret);

				LOGGER.info("Successfully changed baseUrl, eMail and apiSecret!");
			}
			else {
				LOGGER.info("QR Code with invalid token scanned: " + qrcode);
			}
		}
	}

	@Override
	public boolean onPreferenceClick(Preference pref) {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
		return false;
	}
}
