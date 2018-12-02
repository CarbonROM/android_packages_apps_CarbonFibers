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

package org.carbonrom.carbonfibers.fragments.gestures;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v14.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.carbon.CustomSettingsPreferenceFragment;

public class OPGestures extends CustomSettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "OPGestures";
    private static final String USE_BOTTOM_NAVIGATION = "use_bottom_gesture_navigation";

    private SwitchPreference mBottomNavigation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.op_gestures);
        mBottomNavigation = (SwitchPreference) findPreference(USE_BOTTOM_NAVIGATION);
        addCustomPreference(mBottomNavigation, SYSTEM_TWO_STATE, STATE_OFF);
        mBottomNavigation.setOnPreferenceChangeListener(this);
        mFooterPreferenceMixin.createFooterPreference().setTitle(R.string.use_bottom_gesture_info);
   }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBottomNavigation) {
            boolean value = (Boolean) objValue;
            if(value) {
                Settings.Secure.putInt(getActivity().getContentResolver(),
                        Settings.Secure.NAVIGATION_BAR_ENABLED, STATE_OFF);
            }
            return true;
        }
        return false;
    }
}
