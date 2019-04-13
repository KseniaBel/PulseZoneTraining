/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2014
All rights reserved.
 */

package com.ksenia.pulsezonetraining.ui.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.ksenia.pulsezonetraining.R;
import com.ksenia.pulsezonetraining.ui.Activity_MultiDeviceSearchSampler;

import java.util.ArrayList;

/**
 * Adapter that displays MultiDeviceSearchResultWithRSSI in a List View with
 * layout R.layout.multidevice_list_item
 */
public class ArrayAdapter_MultiDeviceSearchResult extends
        ArrayAdapter<Activity_MultiDeviceSearchSampler.MultiDeviceSearchResultWithRSSI> {

    private ArrayList<Activity_MultiDeviceSearchSampler.MultiDeviceSearchResultWithRSSI> mData;

    public ArrayAdapter_MultiDeviceSearchResult(Context context,
                                                ArrayList<Activity_MultiDeviceSearchSampler.MultiDeviceSearchResultWithRSSI> data) {
        super(context, R.layout.multidevice_list_item, data);
        mData = data;
    }

    /**
     * Update the display with new data for the specified position
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.multidevice_list_item, null);
        }

        Activity_MultiDeviceSearchSampler.MultiDeviceSearchResultWithRSSI i = mData.get(position);

        if (i != null) {
            //TextView tv_deviceType = convertView.findViewById(R.id.textView_multiDeviceType);
            TextView tv_deviceName = convertView.findViewById(R.id.textView_multiDeviceName);

            if (tv_deviceName != null) {
                tv_deviceName.setText(i.mDevice.getDeviceDisplayName());
            }
        }

        return convertView;
    }
}
