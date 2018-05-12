/*
 * Copyright (C) 2016-2018 CarbonROM
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
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

public class System extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "System";
    private static final String IMMERSIVE_RECENTS = "immersive_recents";
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String SCREEN_OFF_ANIMATION = "screen_off_animation";
    private static final String SMART_PIXELS = "smart_pixels";
    private static final String FORCE_ASPECT_RATIO = "force_aspect_ratio";
    private static final String FORCE_ASPECT_RATIO_SWITCH = "force_aspect_ratio_switch";

    private ListPreference mRecentsClearAllLocation;
    private ListPreference mImmersiveRecents;
    private ListPreference mScreenOffAnimation;
    private ContentResolver resolver;
    private SwitchPreference mAspectRatioSwitch;
    private PreferenceScreen mForceAspectRatioApps;
    private boolean mAspectRatioEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system);

        resolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();

        // Immersive recents
        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS);
        mImmersiveRecents.setValue(String.valueOf(Settings.System.getInt(
                resolver, Settings.System.IMMERSIVE_RECENTS, 0)));
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
        mImmersiveRecents.setOnPreferenceChangeListener(this);

        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);

        mScreenOffAnimation = (ListPreference) findPreference(SCREEN_OFF_ANIMATION);
        int screenOffStyle = Settings.System.getInt(resolver,
                Settings.System.SCREEN_OFF_ANIMATION, 0);
        mScreenOffAnimation.setValue(String.valueOf(screenOffStyle));
        mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntry());
        mScreenOffAnimation.setOnPreferenceChangeListener(this);

        boolean enableSmartPixels = getContext().getResources().
                getBoolean(com.android.internal.R.bool.config_enableSmartPixels);
        Preference SmartPixels = findPreference(SMART_PIXELS);

        if (!enableSmartPixels){
            prefSet.removePreference(SmartPixels);
        }

        mAspectRatioSwitch = (SwitchPreference) findPreference(FORCE_ASPECT_RATIO_SWITCH);
        mAspectRatioEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.ASPECT_RATIO_APPS_ENABLED, 0, UserHandle.USER_CURRENT) == 1;
        mAspectRatioSwitch.setOnPreferenceChangeListener(this);
        mAspectRatioSwitch.setChecked(mAspectRatioEnabled);

        boolean enableForceAspectRatio = getContext().getResources().
                getBoolean(com.android.internal.R.bool.config_haveHigherAspectRatioScreen);
        mForceAspectRatioApps = (PreferenceScreen) findPreference(FORCE_ASPECT_RATIO);
        mForceAspectRatioApps.setEnabled(mAspectRatioEnabled);

        if (!enableForceAspectRatio){
            prefSet.removePreference(mAspectRatioSwitch);
            prefSet.removePreference(mForceAspectRatioApps);
        }
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
        final String key = preference.getKey();
        if (preference.equals(mImmersiveRecents)) {
            Settings.System.putInt(resolver, Settings.System.IMMERSIVE_RECENTS,
                    Integer.valueOf((String) objValue));
            mImmersiveRecents.setValue(String.valueOf(objValue));
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
            return true;
        } else if (preference.equals(mRecentsClearAllLocation)) {
            int location = Integer.valueOf((String) objValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location, UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
            return true;
        } else if (preference.equals(mScreenOffAnimation)) {
            Settings.System.putInt(resolver,
                    Settings.System.SCREEN_OFF_ANIMATION, Integer.valueOf((String) objValue));
            int valueIndex = mScreenOffAnimation.findIndexOfValue((String) objValue);
            mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntries()[valueIndex]);
            return true;
        } else if (preference.equals(mAspectRatioSwitch)) {
            Settings.System.putIntForUser(resolver,
                    Settings.System.ASPECT_RATIO_APPS_ENABLED, (boolean)objValue ? 0 : 1, UserHandle.USER_CURRENT);
            mForceAspectRatioApps.setEnabled((boolean)objValue);
            return true;
        }
        return false;
    }

}
