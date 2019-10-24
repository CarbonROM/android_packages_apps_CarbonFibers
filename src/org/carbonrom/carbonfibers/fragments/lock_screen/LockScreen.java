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

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.settings.carbon.CustomSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class LockScreen extends CustomSettingsPreferenceFragment implements Indexable {
    private static final String TAG = "LockScreen";
    private static final String LOCKSCREEN_PIN_SCRAMBLE_LAYOUT = "lockscreen_scramble_pin_layout";
    private static final String LOCKSCREEN_BATTERY_INFO = "lockscreen_battery_info";
    private static final String LOCKSCREEN_ICON = "sysui_keyguard_show_lock_icon";
    private static final String LOCK_SHOW_STATUS_BAR = "lockscreen_show_status_bar";
    private static final String LOCKSCREEN_VISUALIZER_ENABLED = "lockscreen_visualizer_enabled";
    private static final String LOCKSCREEN_MEDIA_METADATA = "lockscreen_media_metadata";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lock_screen);
        addCustomPreference(findPreference(LOCKSCREEN_PIN_SCRAMBLE_LAYOUT), SYSTEM_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(LOCKSCREEN_BATTERY_INFO), SYSTEM_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(LOCKSCREEN_ICON), SECURE_TWO_STATE, STATE_ON);
        addCustomPreference(findPreference(LOCK_SHOW_STATUS_BAR), SYSTEM_TWO_STATE, STATE_ON);
        addCustomPreference(findPreference(LOCKSCREEN_VISUALIZER_ENABLED), SECURE_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(LOCKSCREEN_MEDIA_METADATA), SYSTEM_TWO_STATE, STATE_ON);
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.lock_screen;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
            };
}
