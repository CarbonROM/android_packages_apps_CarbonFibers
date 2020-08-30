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

package org.carbonrom.carbonfibers.fragments.status_bar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.provider.Settings;
import androidx.preference.PreferenceCategory;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;

import com.android.settings.R;
import com.android.settings.carbon.CustomSettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class QSBrightness extends CustomSettingsPreferenceFragment implements Indexable {
    private static final String BRIGHTNESS_SLIDER = "qs_show_brightness";
    private static final String AUTO_BRIGHTNESS = "qs_show_brightness_icon";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.qsbrightness);
        addCustomPreference(findPreference(BRIGHTNESS_SLIDER), SECURE_TWO_STATE, STATE_ON);
        addCustomPreference(findPreference(AUTO_BRIGHTNESS), SYSTEM_TWO_STATE, STATE_ON);
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.qsbrightness;
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
