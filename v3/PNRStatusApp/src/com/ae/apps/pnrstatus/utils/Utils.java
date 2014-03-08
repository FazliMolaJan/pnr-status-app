/*
 * Copyright 2012 Midhun Harikumar
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ae.apps.pnrstatus.utils;

import java.util.Calendar;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.ae.apps.pnrstatus.adapters.StackAdapter;
import com.ae.apps.pnrstatus.v3.R;
import com.ae.apps.pnrstatus.vo.PNRStatusVo;
import com.ae.apps.pnrstatus.vo.PassengerDataVo;

public class Utils {
	/**
	 * This method checks whether Internet connectivity is available on the device
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isInternetAvailable(Context context) {
		// Request the Connectivity service to the OS
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		// Check the current state of the Network Information
		if (networkInfo == null)
			return false;
		if (networkInfo.isConnected() == false)
			return false;
		if (networkInfo.isAvailable() == false)
			return false;
		return true;
	}

	// TODO: Read this array from locale file
	public static String[]	monthsArray		= { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
			"Nov", "Dec"					};
	public static String[]	dayNamesArray	= { "", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
			"Saturday"						};

	/**
	 * Returns a string with Month name instead of month number
	 * 
	 * @param dateString
	 * @return
	 */
	public static String getDateWithMonthString(String dateString) {
		if (dateString != null && dateString.trim().length() > 0) {
			String tempArray[] = dateString.split("-");
			String monthStr = tempArray[1];
			int monthInt = Integer.valueOf(monthStr);
			String monthText = "-" + monthsArray[monthInt - 1];
			return dateString.replace("-" + monthStr, monthText);
		}
		return dateString;
	}

	/**
	 * Returns the Day name for the day represented by this timestamp
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String getDayName(String dateString) {
		String dayOfWeek = "";
		if (dateString != null && dateString.trim().length() > 0) {
			Calendar calendar = getCalendarFromDateString(dateString);
			dayOfWeek = dayNamesArray[calendar.get(Calendar.DAY_OF_WEEK)];
		}
		return dayOfWeek;
	}

	public static long getTimeStampFromDateString(String dateString) {
		long timestamp = 0;
		if (dateString != null && dateString.trim().length() > 0) {
			timestamp = getCalendarFromDateString(dateString).getTimeInMillis();
		}
		return timestamp;
	}

	public static Calendar getCalendarFromDateString(String dateString) {
		Calendar calendar = Calendar.getInstance();
		if (dateString != null && dateString.trim().length() > 0) {
			String tempArray[] = dateString.split("-");
			calendar.set(Integer.valueOf(tempArray[2]), Integer.valueOf(tempArray[1]) - 1,
					Integer.valueOf(tempArray[0]));
		}
		return calendar;
	}

	/**
	 * Launches an Intent to open a web page
	 * 
	 * @param context
	 * @param url
	 */
	public static void launchWebPage(Context context, String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		context.startActivity(i);
	}

	public static void shareStatus(Context context, PNRStatusVo pnrStatusVo) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");

		PassengerDataVo dataVo = pnrStatusVo.getFirstPassengerData();
		String shareSubject = context.getResources().getString(R.string.str_share_status_subject,
				pnrStatusVo.getPnrNumber());
		String shareBody = context.getResources().getString(R.string.str_share_status_detail,
				dataVo.getTrainCurrentStatus(), dataVo.getTrainBookingBerth())
				+ AppConstants.APP_HASH_TAG;

		// Put the extras
		intent.putExtra(Intent.EXTRA_TEXT, shareBody);
		intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);

		// Start the chooser activity to let the user choose an app to share
		String shareVia = context.getResources().getString(R.string.str_share_via);
		context.startActivity(Intent.createChooser(intent, shareVia));
	}

	/**
	 * Returns true if we are running in on emulator
	 * 
	 * @return
	 */
	public static boolean isDevelopmentMode() {
		return Build.PRODUCT.equals(AppConstants.GOOGLE_SDK);
	}

	/**
	 * Return an Adapter View implementation based on the OS version
	 * 
	 * @param context
	 * @param adapter
	 * @return
	 */
	@TargetApi(11)
	public static AdapterView<?> getAdapterView(Context context, StackAdapter adapter) {
		AdapterView<?> adapterView = null;
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.widget.ListView listView = new android.widget.ListView(context);
			listView.setAdapter(adapter);
			adapterView = listView;
		} else {
			android.widget.StackView stackView = new android.widget.StackView(context);
			stackView.setAdapter(adapter);
			adapterView = stackView;
		}
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		adapterView.setLayoutParams(params);
		return adapterView;
	}

	/**
	 * Copy some text to the System clip board
	 * 
	 * @param context
	 * @param data
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(11)
	public static void copyTextToClipboard(Context context, String data) {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(data);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clipData = ClipData.newPlainText("pnrnumber", data);
			clipboard.setPrimaryClip(clipData);
		}
		Toast.makeText(context, R.string.str_pnr_copied_to_clip, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Returns a system style for a dialog based on the OS version
	 * 
	 * @return
	 */
	@TargetApi(11)
	public static int getDialogStyle() {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			return android.R.style.Theme_Black_NoTitleBar;
		} else {
			return android.R.style.Theme_Holo_Dialog_NoActionBar;
		}
	}

	@TargetApi(11)
	public static int getDialogWithTitleStyle() {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			return android.R.style.Theme_Black;
		} else {
			return android.R.style.Theme_Holo_Dialog;
		}
	}

}