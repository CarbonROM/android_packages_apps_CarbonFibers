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

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.internal.util.cr.CrUtils;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.VideoPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.RadioButtonPreference;

public class DoubleTapPowerFlashlightGesturePreferenceController extends AbstractPreferenceController
        implements RadioButtonPreference.OnClickListener, LifecycleObserver,
        OnResume, OnPause, PreferenceControllerMixin {

    static final String KEY_LONGPRESS = "torch_power_button_gesture_lp";
    static final String KEY_DOUBLE_TAP = "torch_power_button_gesture_dt";

    private final String PREF_KEY_VIDEO = "gesture_double_tap_power_flashlight_video";
    private final String KEY = "double_tap_power_flashlight_category";
    private final int OFF = 0;
    private final int DOUBLE_TAP_FLASHLIGHT = 1;
    private final int LONGPRESS_FLASHLIGHT = 2;
    private final Context mContext;

    private VideoPreference mVideoPreference;

    PreferenceCategory mPreferenceCategory;
    RadioButtonPreference mLongpressPref;
    RadioButtonPreference mDoubleTapPref;

    private SettingObserver mSettingObserver;

    public DoubleTapPowerFlashlightGesturePreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        mContext = context;

        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        if (!isAvailable()) {
            return;
        }
        mPreferenceCategory = screen.findPreference(getPreferenceKey());
        mLongpressPref = makeRadioPreference(KEY_LONGPRESS, R.string.torch_power_button_gesture_lp);
        mDoubleTapPref = makeRadioPreference(KEY_DOUBLE_TAP, R.string.torch_power_button_gesture_dt);

        if (mPreferenceCategory != null) {
            mSettingObserver = new SettingObserver(mPreferenceCategory);
        }

        mVideoPreference = screen.findPreference(getVideoPrefKey());
    }

    @Override
    public boolean isAvailable() {
        return CrUtils.deviceHasFlashlight(mContext);
    }

    @Override
    public String getPreferenceKey() {
        return KEY;
    }

    public String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }

    @Override
    public void onRadioButtonClicked(RadioButtonPreference preference) {
        int powerTorchSetting = keyToSetting(preference.getKey());
        if (powerTorchSetting != Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.TORCH_POWER_BUTTON_GESTURE, OFF)) {
            if (powerTorchSetting == 1) {
                // if doubletap for torch is enabled, switch off double tap for camera
                Settings.Secure.putInt(mContext.getContentResolver(),
                        Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1);
            }
            Settings.Secure.putInt(mContext.getContentResolver(),
                    Settings.Secure.TORCH_POWER_BUTTON_GESTURE, powerTorchSetting);
        }
    }

    @Override
    public void updateState(Preference preference) {
        int powerTorchSetting = Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.TORCH_POWER_BUTTON_GESTURE, OFF);
        final boolean isLongpress = powerTorchSetting == LONGPRESS_FLASHLIGHT;
        final boolean isDoubleTap = powerTorchSetting == DOUBLE_TAP_FLASHLIGHT;
        if (mLongpressPref != null && mLongpressPref.isChecked() != isLongpress) {
            mLongpressPref.setChecked(isLongpress);
        }
        if (mDoubleTapPref != null && mDoubleTapPref.isChecked() != isDoubleTap) {
            mDoubleTapPref.setChecked(isDoubleTap);
        }

        if (powerTorchSetting == OFF) {
            mLongpressPref.setEnabled(false);
            mDoubleTapPref.setEnabled(false);
        } else {
            mLongpressPref.setEnabled(true);
            mDoubleTapPref.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        if (mSettingObserver != null) {
            mSettingObserver.register(mContext.getContentResolver());
            mSettingObserver.onChange(false, null);
        }

        if (mVideoPreference != null) {
            mVideoPreference.onViewVisible();
        }
    }

    @Override
    public void onPause() {
        if (mSettingObserver != null) {
            mSettingObserver.unregister(mContext.getContentResolver());
        }

        if (mVideoPreference != null) {
            mVideoPreference.onViewInvisible();
        }
    }

    private int keyToSetting(String key) {
        switch (key) {
            case KEY_LONGPRESS:
                return LONGPRESS_FLASHLIGHT;
            case KEY_DOUBLE_TAP:
                return DOUBLE_TAP_FLASHLIGHT;
            default:
                return OFF;
        }
    }

    private RadioButtonPreference makeRadioPreference(String key, int titleId) {
        RadioButtonPreference pref = new RadioButtonPreference(mPreferenceCategory.getContext());
        pref.setKey(key);
        pref.setTitle(titleId);
        pref.setOnClickListener(this);
        mPreferenceCategory.addPreference(pref);
        return pref;
    }

    private class SettingObserver extends ContentObserver {
        private final Uri TORCH_POWER_BUTTON_GESTURE = Settings.Secure.getUriFor(
                Settings.Secure.TORCH_POWER_BUTTON_GESTURE);

        private final Preference mPreference;

        public SettingObserver(Preference preference) {
            super(new Handler());
            mPreference = preference;
        }

        public void register(ContentResolver cr) {
            cr.registerContentObserver(TORCH_POWER_BUTTON_GESTURE, false, this);
        }

        public void unregister(ContentResolver cr) {
            cr.unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri == null || TORCH_POWER_BUTTON_GESTURE.equals(uri)) {
                updateState(mPreference);
            }
        }
    }
}
