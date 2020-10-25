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

package org.carbonrom.carbonfibers.fragments.privacy;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import org.carbonrom.carbonfibers.preferences.CustomSettingsPreferenceFragment;
import org.carbonrom.carbonfibers.R;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class Privacy extends CustomSettingsPreferenceFragment implements Indexable {
    private static final String TAG = "Privacy";
    private static final String CARBON_STATS = "crstats";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.privacy);
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.privacy;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    keys.add(CARBON_STATS);
                    return keys;
                }
            };
}
