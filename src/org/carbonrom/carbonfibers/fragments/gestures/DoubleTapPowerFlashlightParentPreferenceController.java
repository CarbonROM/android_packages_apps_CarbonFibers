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

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceData;
import com.android.settings.widget.VideoPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class DoubleTapPowerFlashlightParentPreferenceController extends BasePreferenceController {

    private final String SECURE_KEY = TORCH_POWER_BUTTON_GESTURE;
    private final int OFF = 0;
    private final int DOUBLE_TAP_FLASHLIGHT = 1;
    private final int LONGPRESS_FLASHLIGHT = 2;

    private VideoPreference mVideoPreference;

    public DoubleTapPowerFlashlightParentPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return CrUtils.deviceHasFlashlight(mContext)
                ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public CharSequence getSummary() {
        int value = Settings.Secure.getInt(
                mContext.getContentResolver(), SECURE_KEY, OFF);
        int summary;
        switch (value) {
            case DOUBLE_TAP_FLASHLIGHT:
                summary = R.string.torch_power_button_gesture_dt_summary;
                break;
            case LONGPRESS_FLASHLIGHT:
                summary = R.string.torch_power_button_gesture_lp_summary;
                break;
            default:
                summary = R.string.torch_power_button_gesture_none_summary;
        }
        return mContext.getText(summary);
    }
}
