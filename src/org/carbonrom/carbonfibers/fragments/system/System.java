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

package org.carbonrom.carbonfibers.fragments.system;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.carbon.CustomSettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import java.util.ArrayList;
import java.util.List;

public class System extends CustomSettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {
    private static final String TAG = "System";
    private static final String SMART_PIXELS = "smart_pixels";
    private static final String AGGRESSIVE_BATTERY ="aggressive_battery";
    private static final String GLOBAL_ACTIONS = "global_actions";
    private static final String ADVANCED_REBOOT = "advanced_reboot";
    private static final String KEY_SCREEN_OFF_ANIMATION = "screen_off_animation";
    private static final String VIBRATION_ON_CHARGE_STATE_CHANGED = "vibration_on_charge_state_changed";
    private static final String FORCE_ASPECT_RATIO = "force_aspect_ratio";
    private static final String FORCE_ASPECT_RATIO_SWITCH = "force_aspect_ratio_switch";

    private ListPreference mScreenOffAnimation;
    private SwitchPreference mAspectRatioSwitch;
    private PreferenceScreen mForceAspectRatioApps;
    private boolean mAspectRatioEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system);
        addCustomPreference(findPreference(ADVANCED_REBOOT), SECURE_TWO_STATE, STATE_ON);
        addCustomPreference(findPreference(VIBRATION_ON_CHARGE_STATE_CHANGED), SYSTEM_TWO_STATE, STATE_ON);
        PreferenceScreen prefSet = getPreferenceScreen();
        boolean enableSmartPixels = getContext().getResources().
                getBoolean(com.android.internal.R.bool.config_enableSmartPixels);
        Preference smartPixels = findPreference(SMART_PIXELS);

        if (!enableSmartPixels){
            prefSet.removePreference(smartPixels);
        }

        mScreenOffAnimation = (ListPreference) findPreference(KEY_SCREEN_OFF_ANIMATION);
        int screenOffAnimation = Settings.Global.getInt(getContentResolver(),
                Settings.Global.SCREEN_OFF_ANIMATION, 0);
        mScreenOffAnimation.setValue(Integer.toString(screenOffAnimation));
        mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntry());
        mScreenOffAnimation.setOnPreferenceChangeListener(this);

        mAspectRatioSwitch = (SwitchPreference) findPreference(FORCE_ASPECT_RATIO_SWITCH);
        addCustomPreference(mAspectRatioSwitch, SYSTEM_TWO_STATE, STATE_OFF);
        mAspectRatioSwitch.setOnPreferenceChangeListener(this);
        mAspectRatioEnabled = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.ASPECT_RATIO_APPS_ENABLED, 0, UserHandle.USER_CURRENT) == 1;
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
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mScreenOffAnimation) {
            int value = Integer.valueOf((String) newValue);
            int index = mScreenOffAnimation.findIndexOfValue((String) newValue);
            mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntries()[index]);
            Settings.Global.putInt(getContentResolver(), Settings.Global.SCREEN_OFF_ANIMATION, value);
            return true;
        } else if (preference == mAspectRatioSwitch) {
            mAspectRatioEnabled = (boolean)newValue;
            mForceAspectRatioApps.setEnabled(mAspectRatioEnabled);
            return true;
        }
        return false;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.system;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    result.add(SMART_PIXELS);
                    result.add(AGGRESSIVE_BATTERY);
                    result.add(GLOBAL_ACTIONS);
                    return result;
                }
            };
}
