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

package org.carbonrom.carbonfibers.fragments.buttons;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.widget.Toast;

import com.android.internal.util.cr.CrUtils;
import com.android.settings.R;
import com.android.settings.carbon.CustomSettingsPreferenceFragment;

public class Buttons extends CustomSettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "Buttons";
    private static final String CALL_VOLUME_ANSWER = "call_volume_answer";
    private static final String CATEGORY_KEYS = "button_keys";
    private static final String VOLUME_BUTTON_MUSIC_CONTROL = "volume_button_music_control";
    private static final String TORCH_POWER_BUTTON_GESTURE = "torch_power_button_gesture";
    private static final String KEY_BUTTON_LIGHT = "button_brightness";
    private static final String NAVIGATION_BAR_ENABLED = "navigation_bar_enabled";

    private ListPreference mTorchPowerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.buttons);

        addCustomPreference(findPreference(CALL_VOLUME_ANSWER), SYSTEM_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(VOLUME_BUTTON_MUSIC_CONTROL), SYSTEM_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(NAVIGATION_BAR_ENABLED), SECURE_TWO_STATE,
                 getActivity().getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar) ? 1 : 0);

        PreferenceScreen prefSet = getPreferenceScreen();
        final PreferenceCategory keysCategory =	 
                (PreferenceCategory) prefSet.findPreference(CATEGORY_KEYS);
        if (!CrUtils.deviceSupportsFlashLight(getContext())) {
            Preference toRemove = (Preference) prefSet.findPreference(TORCH_POWER_BUTTON_GESTURE);
            if (toRemove != null) {
                keysCategory.removePreference(toRemove);
            }
        } else {
            mTorchPowerButton = (ListPreference) findPreference(TORCH_POWER_BUTTON_GESTURE);
            mTorchPowerButton.setOnPreferenceChangeListener(this);
        }

        if (!getResources().getBoolean(
                com.android.internal.R.bool.config_button_brightness_support)) {
            Preference toRemove = (Preference) prefSet.findPreference(KEY_BUTTON_LIGHT);
            if (toRemove != null) {
                keysCategory.removePreference(toRemove);
            }
        }
   }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mTorchPowerButton) {
            int torchPowerButtonValue = Integer.valueOf((String) objValue);
            boolean doubleTapCameraGesture = Settings.Secure.getInt(resolver,
                    Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 0) == 0;
            if (torchPowerButtonValue == 1 && doubleTapCameraGesture) {
                // if double-tap for torch is enabled, switch off double-tap for camera
                Settings.Secure.putInt(resolver,
                        Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1);
                Toast.makeText(getActivity(),
                        (R.string.torch_power_button_gesture_dt_toast),
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }
}
