/*
 * Copyright (C) 2018 CarbonROM
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

package org.carbonrom.carbonfibers.fragments.status_bar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.carbon.CustomSeekBarPreference;
import com.android.settings.carbon.CustomSettingsPreferenceFragment;
import com.android.settings.carbon.SystemSettingListPreference;

public class StatusBar extends CustomSettingsPreferenceFragment implements
	    Preference.OnPreferenceChangeListener {
    private static final String TAG = "StatusBar";

    private static final String CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String STATUS_BAR_CLOCK = "status_bar_clock";
    private static final String STATUS_BAR_CLOCK_SHOW_SECONDS = "status_bar_clock_show_seconds";
    private static final String STATUS_BAR_CLOCK_SHOW_AM_PM = "status_bar_clock_show_am_pm";
    private static final String STATUS_BAR_CLOCK_SHOW_DAY = "status_bar_clock_show_day";
    private static final String NETWORK_TRAFFIC_STATE = "network_traffic_state";
    private static final String NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD = "network_traffic_autohide_threshold";

    private PreferenceScreen mCustomCarrierLabel;
    private SystemSettingListPreference mStatusBarClock;
    private CustomSeekBarPreference mThreshold;
    private String mCustomCarrierLabelText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar);

        addCustomPreference(findPreference(STATUS_BAR_CLOCK_SHOW_SECONDS), SYSTEM_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(STATUS_BAR_CLOCK_SHOW_AM_PM), SYSTEM_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(STATUS_BAR_CLOCK_SHOW_DAY), SYSTEM_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(NETWORK_TRAFFIC_STATE), SYSTEM_TWO_STATE, STATE_OFF);
        mStatusBarClock = (SystemSettingListPreference) findPreference(STATUS_BAR_CLOCK);
        mStatusBarClock.setOnPreferenceChangeListener(this);

        // custom carrier label
        mCustomCarrierLabel = (PreferenceScreen) findPreference(CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();

        int value = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 1, UserHandle.USER_CURRENT);
        mThreshold = (CustomSeekBarPreference) findPreference(NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD);
        mThreshold.setValue(value);
        mThreshold.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        boolean value;
        if (preference.getKey() == null) return false;
        if (preference.getKey().equals(CUSTOM_CARRIER_LABEL)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);
            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(TextUtils.isEmpty(mCustomCarrierLabelText) ? "" : mCustomCarrierLabelText);
            input.setSelection(input.getText().length());
            alert.setView(input);
            alert.setPositiveButton(getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((Spannable) input.getText()).toString().trim();
                            Settings.System.putString(getActivity().getContentResolver(),
                                    Settings.System.CUSTOM_CARRIER_LABEL, value);
                            updateCustomLabelTextSummary();
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_CUSTOM_CARRIER_LABEL_CHANGED);
                            getActivity().sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getString(android.R.string.cancel), null);
            alert.show();
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (mStatusBarClock.equals(preference)) {
            int clockConfig = Integer.parseInt((String) newValue);
            boolean isHidden = clockConfig == 0;
            getCustomPreference(STATUS_BAR_CLOCK_SHOW_SECONDS).setEnabled(!isHidden);
            getCustomPreference(STATUS_BAR_CLOCK_SHOW_AM_PM).setEnabled(!isHidden);
            getCustomPreference(STATUS_BAR_CLOCK_SHOW_DAY).setEnabled(!isHidden);
            return true;
        } else if (mThreshold.equals(preference)) {
             int val = (Integer) newValue;
             Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, val,
                    UserHandle.USER_CURRENT);
             return true;
        }
        return false;
    }

    private void updateCustomLabelTextSummary() {
        mCustomCarrierLabelText = Settings.System.getString(
            getContentResolver(), Settings.System.CUSTOM_CARRIER_LABEL);
        if (TextUtils.isEmpty(mCustomCarrierLabelText)) {
            mCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomCarrierLabel.setSummary(mCustomCarrierLabelText);
        }
    }
}
