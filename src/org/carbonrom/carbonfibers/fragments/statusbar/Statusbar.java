/*
 * Copyright (C) 2016 CarbonROM
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

package org.carbonrom.carbonfibers.fragments.statusbar;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.carbon.GlobalSettingSwitchPreference;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

public class Statusbar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "Status bar";
    private static final String KEY_HEADS_UP_NOTIFICATIONS_ENABLED = "heads_up_notifications_enabled";
    private ListPreference mTickerMode;

    private GlobalSettingSwitchPreference mHeadsUpNotificationsEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.statusbar);

        ContentResolver resolver = getActivity().getContentResolver();

        mTickerMode = (ListPreference) findPreference("ticker_mode");
        mTickerMode.setOnPreferenceChangeListener(this);
        int tickerMode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_TICKER,
                1, UserHandle.USER_CURRENT);
        mTickerMode.setValue(String.valueOf(tickerMode));
        mTickerMode.setSummary(mTickerMode.getEntry());

        mHeadsUpNotificationsEnabled = (GlobalSettingSwitchPreference) findPreference(KEY_HEADS_UP_NOTIFICATIONS_ENABLED);
        updatePrefs();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CARBONFIBERS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference.equals(mTickerMode)) {
            int tickerMode = Integer.parseInt(((String) objValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_TICKER, tickerMode, UserHandle.USER_CURRENT);
            int index = mTickerMode.findIndexOfValue((String) objValue);
            mTickerMode.setSummary(
                    mTickerMode.getEntries()[index]);
            updatePrefs();
            return true;
        }
        return false;
    }

    private void updatePrefs() {
           ContentResolver resolver = getActivity().getContentResolver();
           boolean mHideHeadsUp = (Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_SHOW_TICKER, 1) == 0) ||
                    (Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_SHOW_TICKER, 1) == 2);
           Settings.Global.putInt(resolver,
                    Settings.Global.HEADS_UP_NOTIFICATIONS_ENABLED,
                    mHideHeadsUp ? 0 : 1);
      }

}

