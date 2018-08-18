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

package org.carbonrom.carbonfibers.fragments.lock_screen;

import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.carbon.CustomSettingsPreferenceFragment;

public class LockScreen extends CustomSettingsPreferenceFragment {
    private static final String TAG = "LockScreen";

    private static final String LOCKSCREEN_QUICK_UNLOCK_CONTROL = "lockscreen_quick_unlock_control";
    private static final String LOCKSCREEN_PIN_SCRAMBLE_LAYOUT = "lockscreen_scramble_pin_layout";
    private static final String STATUS_BAR_LOCKED_ON_SECURE_KEYGUARD = "status_bar_locked_on_secure_keyguard";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lock_screen);
        addCustomPreference(findPreference(LOCKSCREEN_QUICK_UNLOCK_CONTROL), SYSTEM_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(LOCKSCREEN_PIN_SCRAMBLE_LAYOUT), SYSTEM_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(STATUS_BAR_LOCKED_ON_SECURE_KEYGUARD), SECURE_TWO_STATE, STATE_OFF);
    }
}
