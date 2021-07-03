/*
 * Copyright (C) 2018 The Android Open Source Project
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

import static android.provider.Settings.Secure.TORCH_POWER_BUTTON_GESTURE;
import static android.provider.Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED;

import android.content.Context;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.util.cr.CrUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceData;
import com.android.settings.widget.VideoPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

import org.carbonrom.carbonfibers.R;

public class DoubleTapPowerFlashlightPreferenceController extends BasePreferenceController implements
        Preference.OnPreferenceChangeListener, LifecycleObserver, OnStart, OnStop {

    private final String SECURE_KEY_CAMERA = CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED; 
    private final String SECURE_KEY = TORCH_POWER_BUTTON_GESTURE;
    private final int OFF = 0;
    private final int DOUBLE_TAP_FLASHLIGHT = 1;
    private final int LONGPRESS_FLASHLIGHT = 2;
    private final String PREF_KEY_VIDEO = "gesture_double_tap_power_flashlight_video";

    private VideoPreference mVideoPreference;

    public DoubleTapPowerFlashlightPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        if (isAvailable()) {
            mVideoPreference = screen.findPreference(getVideoPrefKey());
        }
    }

    @Override
    public int getAvailabilityStatus() {
        return CrUtils.deviceHasFlashlight(mContext)
                ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    protected String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            if (preference instanceof ListPreference) {
                ListPreference pref = (ListPreference) preference;
                int value = Settings.Secure.getInt(
                        mContext.getContentResolver(), SECURE_KEY, OFF);
                switch (value) {
                    case DOUBLE_TAP_FLASHLIGHT:
                        pref.setValue(String.valueOf(value));
                        break;
                    case LONGPRESS_FLASHLIGHT:
                        pref.setValue(String.valueOf(value));
                        break;
                    default:
                        pref.setValue(String.valueOf(OFF));
                }
            }
        }
    }

    @Override
    public CharSequence getSummary() {
        int value = Settings.Secure.getInt(
                mContext.getContentResolver(), SECURE_KEY, OFF);
        int summary;
        switch (value) {
            case DOUBLE_TAP_FLASHLIGHT:
                summary = R.string.torch_power_button_gesture_dt;
                break;
            case LONGPRESS_FLASHLIGHT:
                summary = R.string.torch_power_button_gesture_lp;
                break;
            default:
                summary = R.string.torch_power_button_gesture_none;
        }
        return mContext.getText(summary);
    }

    @Override
    public void onStart() {
        if (mVideoPreference != null) {
            mVideoPreference.onViewVisible();
        }
    }

    @Override
    public void onStop() {
        if (mVideoPreference != null) {
            mVideoPreference.onViewInvisible();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int mTorchPowerButtonValue = Integer.parseInt((String) newValue);
        preference.setSummary(getSummary());
        if (mTorchPowerButtonValue == 1) {
            // if doubletap for torch is enabled, switch off double tap for camera
            Settings.Secure.putInt(mContext.getContentResolver(),
                    SECURE_KEY_CAMERA, 1/*camera gesture is disabled with 1*/);
        }

        return Settings.Secure.putInt(mContext.getContentResolver(),
                SECURE_KEY, mTorchPowerButtonValue);
    }

    @Override
    @SliceData.SliceType
    public int getSliceType() {
        return SliceData.SliceType.SWITCH;
    }

    @Override
    public boolean isSliceable() {
        return true;
    }

    @Override
    public boolean isPublicSlice() {
        return false;
    }
}
