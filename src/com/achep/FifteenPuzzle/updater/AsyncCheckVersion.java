/*
 * Copyright (C) 2012-2013 AChep@xda <artemchep@gmail.com>
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

package com.achep.FifteenPuzzle.updater;

import com.achep.FifteenPuzzle.R;
import com.achep.FifteenPuzzle.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;

public class AsyncCheckVersion extends AsyncTask<Context, Void, String> {

	private static final String URL_MASTER_MANIFEST = "https://raw.github.com/AChep/FifteenPuzzle/master/AndroidManifest.xml";

	private static final String MANIFEST_VERSION_CODE_PART = "android:versionCode=\"";
	private static final String MANIFEST_VERSION_NAME_PART = "android:versionName=\"";

	private static final int NOTIFICATION_ID = 0;

	private Context mContext;

	@Override
	protected String doInBackground(Context... context) {
		mContext = context[0];
		String manifest = Utils.downloadText(mContext, URL_MASTER_MANIFEST);
		if (manifest == null)
			return null;

		int a = manifest.indexOf(MANIFEST_VERSION_CODE_PART);
		if (a == -1)
			return null;
		manifest = manifest.substring(a + MANIFEST_VERSION_CODE_PART.length());
		a = manifest.indexOf("\"");
		if (a == -1)
			return null;

		int versionCodeNew = Integer.parseInt(manifest.substring(0, a));
		try {
			if (mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0).versionCode >= versionCodeNew)
				return null;
		} catch (NameNotFoundException e) {
			return null;
		}

		a = manifest.indexOf(MANIFEST_VERSION_NAME_PART);
		if (a == -1)
			return null;
		manifest = manifest.substring(a + MANIFEST_VERSION_NAME_PART.length());
		a = manifest.indexOf("\"");
		if (a == -1)
			return null;

		String versionNameNew = mContext.getResources().getString(
				R.string.app_name)
				+ " " + manifest.substring(0, a);

		return versionNameNew;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPostExecute(String versionNameNew) {
		if (versionNameNew == null)
			return;

		Notification n = new Notification();
		n.tickerText = versionNameNew;
		n.icon = R.drawable.ic_statusbar_new_version;
		n.flags = Notification.FLAG_AUTO_CANCEL;
		n.setLatestEventInfo(mContext, versionNameNew, mContext.getResources()
				.getString(R.string.auto_updater_notification_summary),
				PendingIntent.getActivity(mContext, 0, new Intent(mContext,
						AutoUpdater.class).setAction(versionNameNew), 0));

		NotificationManager nm = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, n);
	}
}