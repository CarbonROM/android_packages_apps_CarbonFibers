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

package org.carbonrom.carbonfibers.fragments.misc;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

import com.android.settings.carbon.CustomSeekBarPreference;

public class Misc extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "Misc";
    private static final String SCREENSHOT_TYPE = "screenshot_type";
    private static final String SCROLLINGCACHE_PREF = "pref_scrollingcache";
    private static final String SCROLLINGCACHE_PERSIST_PROP = "persist.sys.scrollingcache";
    private static final String SCROLLINGCACHE_DEFAULT = "1";
    private static final String SCREENSHOT_DELAY = "screenshot_delay";

    private CustomSeekBarPreference mScreenshotDelay;
    private ListPreference mScreenshotType;
    private ListPreference mScrollingCachePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.misc);

        final ContentResolver resolver = getActivity().getContentResolver();

        mScrollingCachePref = (ListPreference) findPreference(SCROLLINGCACHE_PREF);
        mScrollingCachePref.setValue(SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP,
                SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP, SCROLLINGCACHE_DEFAULT)));
        mScrollingCachePref.setOnPreferenceChangeListener(this);
        mScreenshotType = (ListPreference) findPreference(SCREENSHOT_TYPE);
        int mScreenshotTypeValue = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SCREENSHOT_TYPE, 0);
        mScreenshotType.setValue(String.valueOf(mScreenshotTypeValue));
        mScreenshotType.setSummary(mScreenshotType.getEntry());
        mScreenshotType.setOnPreferenceChangeListener(this);

        mScreenshotDelay = (CustomSeekBarPreference) findPreference(SCREENSHOT_DELAY);
        int screenshotDelay = Settings.System.getInt(resolver,
                Settings.System.SCREENSHOT_DELAY, 1000);
        mScreenshotDelay.setValue(screenshotDelay / 1);
        mScreenshotDelay.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getMetricsCategory() {
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

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mScrollingCachePref) {
            if (newValue != null) {
                SystemProperties.set(SCROLLINGCACHE_PERSIST_PROP, (String)newValue);
            return true;
            }
        } else if  (preference == mScreenshotType) {
            int mScreenshotTypeValue = Integer.parseInt(((String) newValue).toString());
            mScreenshotType.setSummary(
                    mScreenshotType.getEntries()[mScreenshotTypeValue]);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREENSHOT_TYPE, mScreenshotTypeValue);
            mScreenshotType.setValue(String.valueOf(mScreenshotTypeValue));
            return true;
        } else if (preference == mScreenshotDelay) {
            int screenshotDelay = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SCREENSHOT_DELAY, screenshotDelay * 1);
            return true;
        }
        return false;
    }
}
