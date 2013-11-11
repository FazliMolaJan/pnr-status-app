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

package com.ae.apps.pnrstatus.adapters;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ae.apps.pnrstatus.fragments.PnrStatusFragment;
import com.ae.apps.pnrstatus.utils.AppConstants;
import com.ae.apps.pnrstatus.utils.PNRUtils;
import com.ae.apps.pnrstatus.utils.Utils;
import com.ae.apps.pnrstatus.v3.R;
import com.ae.apps.pnrstatus.vo.PNRStatusVo;
import com.ae.apps.pnrstatus.vo.PassengerDataVo;

/**
 * Adapter for displaying PNR Rows
 * 
 * @author midhun_harikumar
 * 
 */
public class PnrRowAdapter extends BaseAdapter {

	private PnrStatusFragment	parentFragment;
	private List<PNRStatusVo>	arrayList;
	private LayoutInflater		inflater;
	private Context				context;

	public PnrRowAdapter(Context context, PnrStatusFragment parentFragment, List<PNRStatusVo> list) {
		arrayList = list;
		this.context = context;
		this.parentFragment = parentFragment;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final String normalPNR = arrayList.get(position).getPnrNumber();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.pnr_row_item, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.pnr_number);
			holder.txtStatus = (TextView) convertView.findViewById(R.id.pnr_status);
			holder.btnCheck = (ImageButton) convertView.findViewById(R.id.check_status);
			holder.btnDelete = (ImageButton) convertView.findViewById(R.id.delete_status);
			holder.btnInfo = (ImageButton) convertView.findViewById(R.id.more_info);

			convertView.setTag(holder);

			// When clicked on the pnr number, copy it to the clipboard
			holder.txtName.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Utils.copyTextToClipboard(context, normalPNR);
				}
			});
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String formattedString = PNRUtils.formatPNRString(normalPNR);
		holder.txtName.setText(formattedString);

		// Show the FirstPassenger Booking Berth if its available
		PassengerDataVo passengerDataVo = arrayList.get(position).getFirstPassengerData();
		if (null != passengerDataVo) {
			String message = "Update " + holder.txtName.getText() + " with " + passengerDataVo.getTrainCurrentStatus();
			Log.d(AppConstants.TAG, message);

			holder.btnInfo.setEnabled(true);
			//holder.btnInfo.setImageResource(R.drawable.ic_info);
			holder.txtStatus.setText(passengerDataVo.getTrainCurrentStatus());
		} else {
			// Disable the Extra Info Button
			holder.btnInfo.setEnabled(false);
			//holder.btnInfo.setImageResource(R.drawable.ic_info_disabled);
			holder.txtStatus.setText("");
		}

		// Get the PNRStatusVo object
		final PNRStatusVo pnrStatusVo = arrayList.get(position);

		// Check the status of the PNR Number
		holder.btnCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				Log.d(AppConstants.TAG, "PnrRowAdapter :: Check Button Clicked");
				parentFragment.checkStatus(pnrStatusVo);
			}
		});

		// Remove the PNR Number from the view
		holder.btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				Log.d(AppConstants.TAG, "PnrRowAdapter :: Remove Button Clicked");
				Activity activity = parentFragment.getActivity();
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setCancelable(true);
				builder.setMessage(activity.getString(R.string.message_delete_pnr));

				// Set the positive button click
				builder.setPositiveButton(activity.getString(R.string.button_yes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								parentFragment.removeRow(pnrStatusVo);
								dialog.dismiss();
							}
						});

				// Set the negative button click
				builder.setNegativeButton(activity.getString(R.string.button_no),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});

				builder.show();

			}
		});

		// Display the details of the pnrstatus
		holder.btnInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				Log.d(AppConstants.TAG, "PnrRowAdapter :: Info Button Clicked");
				parentFragment.showPNRStatusDetail(pnrStatusVo);
			}
		});
		return convertView;
	}

	/**
	 * Inner Class that describes the contents of a PNR Status Row
	 * @author Midhun
	 *
	 */
	private static class ViewHolder {
		TextView	txtName;
		TextView	txtStatus;
		ImageButton	btnCheck;
		ImageButton	btnDelete;
		ImageButton	btnInfo;
	}
}