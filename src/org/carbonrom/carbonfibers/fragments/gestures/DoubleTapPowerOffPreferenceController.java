/*
 * Copyright (C) 2016 The Android Open Source Project
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

import android.content.Context;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.gestures.GesturePreferenceController;

public class DoubleTapPowerOffPreferenceController extends GesturePreferenceController {

    static final int ON = 1;
    static final int OFF = 0;
    static final int ALL_ENABLED = 3;
    static final int SB_ENABLED = 2;
    static final int LS_ENABLED = 1;

    public DoubleTapPowerOffPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    protected String getVideoPrefKey() {
        return ""; //null for now
    }

    @Override
    public boolean isChecked() {
        return true;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return true;
    }

    @Override
    public CharSequence getSummary() {
        int enabledOptions = 0;
        enabledOptions += Settings.Secure.getInt(
                mContext.getContentResolver(), Settings.Secure.DOUBLE_TAP_SLEEP_LOCKSCREEN, OFF) == ON ? 1 : 0;

        enabledOptions += Settings.System.getInt(
                mContext.getContentResolver(), Settings.System.DOUBLE_TAP_SLEEP_GESTURE, OFF) == ON ? 2 : 0;

        int summary;
        switch (enabledOptions) {
            case ALL_ENABLED:
                summary = R.string.doubletap_poweroff_all;
                break;
            case SB_ENABLED:
                summary = R.string.doubletap_poweroff_sb;
                break;
            case LS_ENABLED:
                summary = R.string.doubletap_poweroff_ls;
                break;
            default:
                summary = R.string.doubletap_poweroff_disabled;
        }
        return mContext.getText(summary);
    }
}
