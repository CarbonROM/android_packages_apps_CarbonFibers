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

package org.carbonrom.carbonfibers.fragments.status_bar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.SearchIndexableResource;
import android.util.Log;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

import org.carbonrom.carbonfibers.helpers.QsTileConfigDialog;

@SearchIndexable
public class StatusBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {
    private static final String TAG = "StatusBar";

    private ListPreference mBatteryPercent;
    private ListPreference mBatteryStyle;
    private int mBatteryPercentValue;
    private int mBatteryPercentValuePrev;

    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";

    private static final int BATTERY_STYLE_PORTRAIT = 0;
    private static final int BATTERY_STYLE_TEXT = 4;
    private static final int BATTERY_STYLE_HIDDEN = 5;
    private static final int BATTERY_PERCENT_HIDDEN = 0;
    private static final int BATTERY_PERCENT_SHOW = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar);

	final ContentResolver resolver = getActivity().getContentResolver();

        mBatteryStyle = (ListPreference) findPreference("status_bar_battery_style");
        mBatteryPercent = (ListPreference) findPreference("status_bar_show_battery_percent");
        mBatteryStyle.setOnPreferenceChangeListener(this);
        mBatteryPercent.setOnPreferenceChangeListener(this);

        updateBatteryPreferences();

        Preference qsTileConfig = findPreference("custom_qs_tile_config_dialog");
        qsTileConfig.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                QsTileConfigDialog newFragment = QsTileConfigDialog .newInstance();
                newFragment.show(ft, "QsTileConfigDialog");
                return true;

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBatteryPreferences();
    }

    @Override
     public boolean onPreferenceChange(Preference preference, Object newValue) {

        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mBatteryStyle) {
            int batterystyle = Integer.parseInt((String) newValue);
            updateBatteryOptions(batterystyle, mBatteryPercentValue);
            int index = mBatteryStyle.findIndexOfValue((String) newValue);
            mBatteryStyle.setSummary(mBatteryStyle.getEntries()[index]);
            return true;
        } else if (preference == mBatteryPercent) {
            mBatteryPercentValue = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, mBatteryPercentValue,
                    UserHandle.USER_CURRENT);
            int index = mBatteryPercent.findIndexOfValue((String) newValue);
            mBatteryPercent.setSummary(mBatteryPercent.getEntries()[index]);
            return true;
        }
         return false;
     }

    private void updateBatteryPreferences() {
        final ContentResolver resolver = getActivity().getContentResolver();

        int batterystyle = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, BATTERY_STYLE_PORTRAIT, UserHandle.USER_CURRENT);
        mBatteryPercentValue = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0, UserHandle.USER_CURRENT);
        mBatteryPercentValuePrev = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT + "_prev", -1, UserHandle.USER_CURRENT);

        batterystyle = (batterystyle == BATTERY_STYLE_TEXT
                && mBatteryPercentValue == BATTERY_PERCENT_HIDDEN) ? BATTERY_STYLE_HIDDEN : batterystyle;


        mBatteryStyle.setValue(String.valueOf(batterystyle));
        mBatteryStyle.setSummary(mBatteryStyle.getEntry());

        mBatteryPercent.setValue(String.valueOf(mBatteryPercentValue));
        mBatteryPercent.setSummary(mBatteryPercent.getEntry());

        updateBatteryOptions(batterystyle, mBatteryPercentValue);
    }

    private void updateBatteryOptions(int batterystyle, int batterypercent) {
        ContentResolver resolver = getActivity().getContentResolver();
        switch (batterystyle) {
            case BATTERY_STYLE_TEXT:
            handleTextPercentage(BATTERY_PERCENT_SHOW);
            break;
            case BATTERY_STYLE_HIDDEN:
            handleTextPercentage(BATTERY_PERCENT_HIDDEN);
            break;
            default:
            mBatteryPercent.setEnabled(true);
            if (mBatteryPercentValuePrev != -1) {
                Settings.System.putIntForUser(resolver,
                    Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT,
                    mBatteryPercentValuePrev, UserHandle.USER_CURRENT);
                Settings.System.putIntForUser(resolver,
                    Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT + "_prev",
                    -1, UserHandle.USER_CURRENT);
                mBatteryPercentValue = mBatteryPercentValuePrev;
                mBatteryPercentValuePrev = -1;
                int index = mBatteryPercent.findIndexOfValue(String.valueOf(mBatteryPercentValue));
                mBatteryPercent.setSummary(mBatteryPercent.getEntries()[index]);
            }

            Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, batterystyle,
                UserHandle.USER_CURRENT);
            break;
        }
    }

    private void handleTextPercentage(int batterypercent) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (mBatteryPercentValuePrev == -1) {
            mBatteryPercentValuePrev = mBatteryPercentValue;
            Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT + "_prev",
                mBatteryPercentValue, UserHandle.USER_CURRENT);
        }

        Settings.System.putIntForUser(resolver,
            Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT,
            batterypercent, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
            Settings.System.STATUS_BAR_BATTERY_STYLE, BATTERY_STYLE_TEXT,
            UserHandle.USER_CURRENT);
        int index = mBatteryPercent.findIndexOfValue(String.valueOf(batterypercent));
        mBatteryPercent.setSummary(mBatteryPercent.getEntries()[index]);
        mBatteryPercent.setEnabled(false);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CARBONFIBERS;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.status_bar;
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
