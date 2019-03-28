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

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.carbon.CustomSettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class AggressiveBattery extends CustomSettingsPreferenceFragment implements Indexable {
    private static final String TAG = "AggressiveBattery";
    private static final String AGGRESSIVE_IDLE_ENABLED = "aggressive_idle_enabled";
    private static final String AGGRESSIVE_STANDBY_ENABLED = "aggressive_standby_enabled";
    private static final String AGGRESSIVE_BATTERY_SAVER = "aggressive_battery_saver";

    private AggressiveBatteryObserver mAggressiveBatteryObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.aggressive_battery);

        mFooterPreferenceMixin.createFooterPreference().setTitle(R.string.aggressive_battery_warning_text);

        addCustomPreference(findPreference(AGGRESSIVE_IDLE_ENABLED), GLOBAL_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(AGGRESSIVE_STANDBY_ENABLED), GLOBAL_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(AGGRESSIVE_BATTERY_SAVER), GLOBAL_TWO_STATE, STATE_OFF);
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
            getActivity().getContentResolver().registerContentObserver(Settings.Global.getUriFor(
                    AGGRESSIVE_BATTERY_SAVER), false, this, UserHandle.USER_CURRENT);
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

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.aggressive_battery;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
            };
}
