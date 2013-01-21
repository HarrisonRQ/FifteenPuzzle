/*
 * Copyright (C) 2012 AChep@xda <artemchep@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.achep.FifteenPuzzle.preferences;

import com.achep.FifteenPuzzle.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements
		OnPreferenceChangeListener {

	public static final String SDCARD_FOLDER = "/Android/data/com.achep.FifteenPuzzles/";
	public static final String SHARED_PREFERENCES_FILE = "preferences2";

	public static class Keys {
		public final static String SPREF_PUZZLE_LENGTH = "spref_puzzle_length";

		public final static String PREF_USER_NAME = "pref_user_name";
		public final static String PREF_ABOUT_FEEDBACK = "pref_about_feedback";
	}

	private EditTextPreference mPrefUserName;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.settings);

		mPrefUserName = (EditTextPreference) findPreference(Keys.PREF_USER_NAME);
		mPrefUserName.setOnPreferenceChangeListener(this);
		updateUserNamePreference(mPrefUserName.getText());

		Preference prefFeedback = (Preference) findPreference(Keys.PREF_ABOUT_FEEDBACK);
		prefFeedback
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO: Add something real XD
						Intent i = new Intent(Intent.ACTION_SEND);
						i.setType("message/rfc822");
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "artemchep@gmail.com" });
						i.putExtra(Intent.EXTRA_SUBJECT, "FifteenPuzzle (suggestions & reports): ");
						StringBuilder body = new StringBuilder();
						body.append("Device model: "+android.os.Build.MODEL+"\n");
						body.append("Android version: "+android.os.Build.VERSION.CODENAME+"\n");
						body.append("CPU: "+android.os.Build.CPU_ABI+"\n\nDear developer,\n");
						i.putExtra(Intent.EXTRA_TEXT, body.toString());
						try {
							startActivity(Intent.createChooser(i,
									"Send mail..."));
						} catch (android.content.ActivityNotFoundException ex) {
							Toast.makeText(
									Settings.this,
									getResources()
											.getString(
													R.string.settings_other_about_feedback_failed),
									Toast.LENGTH_SHORT).show();
						}
						return true;
					}
				});
	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object value) {
		if (pref.equals(mPrefUserName)) {
			updateUserNamePreference((String) value);
			return true;
		}
		return false;
	}

	private void updateUserNamePreference(String text) {
		mPrefUserName.setSummary(getResources().getString(
				R.string.settings_nickname2)
				+ " " + text);
	}

}