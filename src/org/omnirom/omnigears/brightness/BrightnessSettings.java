/*
 *  Copyright (C) 2013 The OmniROM Project
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

package org.omnirom.omnigears.brightness;

import com.android.settings.SettingsPreferenceFragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.IPowerManager;
import android.os.ServiceManager;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.ListPreference;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.provider.Settings;
import android.view.View;
import android.util.Log;
import android.app.AlertDialog;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import org.omnirom.omnigears.R;

public class BrightnessSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "BrightnessSettings";

    private static final String KEY_AUTOMATIC_SENSITIVITY = "auto_brightness_sensitivity";
    private static final String KEY_SCREEN_AUTO_BRIGHTNESS = "screen_auto_brightness";

    private ListPreference mAutomaticSensitivity;
    private Preference mAutomaticScreenBrightness;
    private AutoBrightnessDialog mScreenBrightnessDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.brightness_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mAutomaticSensitivity = (ListPreference) findPreference(KEY_AUTOMATIC_SENSITIVITY);
        float currentSensitivity = Settings.System.getFloat(resolver,
            Settings.System.AUTO_BRIGHTNESS_RESPONSIVENESS, 1.0f);

        int currentSensitivityInt = (int) (currentSensitivity * 100);
        mAutomaticSensitivity.setValue(String.valueOf(currentSensitivityInt));
        updateAutomaticSensityDescription(currentSensitivityInt);
        mAutomaticSensitivity.setOnPreferenceChangeListener(this);

        mAutomaticScreenBrightness = (Preference) findPreference(KEY_SCREEN_AUTO_BRIGHTNESS);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mScreenBrightnessDialog != null) {
            mScreenBrightnessDialog.dismiss();
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mAutomaticScreenBrightness) {
            showScreenAutoBrightnessDialog();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();

        if (KEY_AUTOMATIC_SENSITIVITY.equals(key)) {
            int value = Integer.parseInt((String) objValue);
            float sensitivity = 0.01f * value;

            Settings.System.putFloat(getContentResolver(),
                        Settings.System.AUTO_BRIGHTNESS_RESPONSIVENESS, sensitivity);

            updateAutomaticSensityDescription(value);
        } else {
            return false;
        }
        return true;
    }

    private void updateAutomaticSensityDescription(int value) {
        String[] sensitivityValues = getResources().getStringArray(
            R.array.auto_brightness_sensitivity_values);

        for (int i = 0; i < sensitivityValues.length; i++) {
            if (sensitivityValues[i].equals(String.valueOf(value))) {
                mAutomaticSensitivity.setSummary(getResources().getStringArray(
                    R.array.auto_brightness_sensitivity_entries)[i]);
                break;
            }
        }
    }

    private void showScreenAutoBrightnessDialog() {
        if (mScreenBrightnessDialog != null && mScreenBrightnessDialog.isShowing()) {
            return;
        }

        mScreenBrightnessDialog = new AutoBrightnessDialog(getActivity(), true);
        mScreenBrightnessDialog.show();
    }
}
