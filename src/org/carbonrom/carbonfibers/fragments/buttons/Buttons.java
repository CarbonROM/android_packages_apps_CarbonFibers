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
import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

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
public class Buttons extends CustomSettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Indexable {
    private static final String TAG = "Buttons";
    private static final String NAV_BAR_LAYOUT = "nav_bar_layout";
    private static final String SYSUI_NAV_BAR = "sysui_nav_bar";
    private static final String INVERSE_NAVBAR = "sysui_nav_bar_inverse";

    private ListPreference mNavBarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.buttons);
        addCustomPreference(findPreference(INVERSE_NAVBAR), SECURE_TWO_STATE, STATE_OFF);
        mNavBarLayout = (ListPreference) findPreference(NAV_BAR_LAYOUT);
        mNavBarLayout.setOnPreferenceChangeListener(this);
        String navBarLayoutValue = Settings.Secure.getString(resolver,
                                        SYSUI_NAV_BAR, "default");
            mNavBarLayout.setValue(navBarLayoutValue);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNavBarLayout) {
            Settings.Secure.putString(resolver,
                        SYSUI_NAV_BAR, (String) objValue);
            return true;
        }
        return false;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.buttons;
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
