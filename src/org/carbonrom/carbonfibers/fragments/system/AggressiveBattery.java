/*
 * Copyright (C) 2019 CarbonROM
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

package org.carbonrom.carbonfibers.fragments.system;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.carbon.CustomSettingsPreferenceFragment;

public class AggressiveBattery extends CustomSettingsPreferenceFragment {
    private static final String TAG = "AggressiveBattery";
    private static final String AGGRESSIVE_IDLE_ENABLED = "aggressive_idle_enabled";
    private static final String AGGRESSIVE_STANDBY_ENABLED = "aggressive_standby_enabled";

    private AggressiveBatteryObserver mAggressiveBatteryObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.aggressive_battery);

        mFooterPreferenceMixin.createFooterPreference().setTitle(R.string.aggressive_battery_warning_text);

        addCustomPreference(findPreference(AGGRESSIVE_IDLE_ENABLED), GLOBAL_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(AGGRESSIVE_STANDBY_ENABLED), GLOBAL_TWO_STATE, STATE_OFF);
        mAggressiveBatteryObserver = new AggressiveBatteryObserver(new Handler());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAggressiveBatteryObserver != null) {
            mAggressiveBatteryObserver.register();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAggressiveBatteryObserver != null) {
            mAggressiveBatteryObserver.unregister();
        }
    }

    private class AggressiveBatteryObserver extends ContentObserver {
        public AggressiveBatteryObserver(Handler handler) {
            super(handler);
        }

        public void register() {
            getActivity().getContentResolver().registerContentObserver(Settings.Global.getUriFor(
                    AGGRESSIVE_IDLE_ENABLED), false, this, UserHandle.USER_CURRENT);
            getActivity().getContentResolver().registerContentObserver(Settings.Global.getUriFor(
                    AGGRESSIVE_STANDBY_ENABLED), false, this, UserHandle.USER_CURRENT);
        }

        public void unregister() {
            getActivity().getContentResolver().unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            updateAllCustomPreferences();
        }
    }
}
