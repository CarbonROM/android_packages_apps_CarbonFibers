/*
 *  Copyright (C) 2016 CarbonROM
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.carbonrom.carbonfibers.fragments.display;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class DisplayTweaks extends SettingsPreferenceFragment implements
       Preference.OnPreferenceChangeListener {

private ListPreference mDashboardColumns;

@Override
protected int getMetricsCategory() {
      return MetricsEvent.CARBONFIBERS;
}

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.display_tweaks);

    ContentResolver resolver = getActivity().getContentResolver();

    }

        mDashboardColumns = (ListPreference) findPreference(DASHBOARD_COLUMNS);
        mDashboardColumns.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.DASHBOARD_COLUMNS, DashboardSummary.mNumColumns)));
        mDashboardColumns.setSummary(mDashboardColumns.getEntry());
        mDashboardColumns.setOnPreferenceChangeListener(this);


@Override
public boolean onPreferenceChange(Preference preference, Object objValue) {
    final String key = preference.getKey();

        if (preference == mDashboardColumns) {
            Settings.System.putInt(getContentResolver(), Settings.System.DASHBOARD_COLUMNS,
                    Integer.valueOf((String) objValue));
            mDashboardColumns.setValue(String.valueOf(objValue));
            mDashboardColumns.setSummary(mDashboardColumns.getEntry());
            return true;
        }
    }
}
