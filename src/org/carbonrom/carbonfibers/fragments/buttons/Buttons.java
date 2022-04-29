/*
 * Copyright (C) 2016 CarbonROM
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

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.SearchIndexableResource;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.android.settings.carbon.CustomSeekBarPreference;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class Buttons extends SettingsPreferenceFragment implements OnPreferenceChangeListener, Indexable {
    private static final String TAG = "Buttons";
    private static final int BUTTON_BRIGHTNESS_NOT_SUPPORTED = 0;
    private static final int BUTTON_BRIGHTNESS_TOGGLE_MODE_ONLY = 1;

    //Keys
    private static final String KEY_BUTTON_BRIGHTNESS = "button_brightness";
    private static final String KEY_BUTTON_BRIGHTNESS_SW = "button_brightness_sw";
    private static final String KEY_BACKLIGHT_TIMEOUT = "backlight_timeout";
    private static final String KEY_NAVIGATION_BAR_ENABLED = "force_show_navbar";

    // category keys
    private static final String CATEGORY_HWKEY = "buttons_hardware_category";

    private ListPreference mBacklightTimeout;
    private CustomSeekBarPreference mButtonBrightness;
    private SwitchPreference mButtonBrightness_sw;
    private SwitchPreference mNavigationBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.buttons);

        final PreferenceScreen prefScreen = getPreferenceScreen();
        final PreferenceCategory hwkeyCat = (PreferenceCategory) prefScreen
                .findPreference(CATEGORY_HWKEY);

        final int supportButtonBrightness = getResources().getInteger(com.android.internal.R.integer
                            .config_deviceSupportsButtonBrightnessControl);
        final boolean hasButtonBacklightSupport = supportButtonBrightness != BUTTON_BRIGHTNESS_NOT_SUPPORTED;

        mBacklightTimeout =
                (ListPreference) findPreference(KEY_BACKLIGHT_TIMEOUT);
        mButtonBrightness =
                (CustomSeekBarPreference) findPreference(KEY_BUTTON_BRIGHTNESS);
        mButtonBrightness_sw =
                (SwitchPreference) findPreference(KEY_BUTTON_BRIGHTNESS_SW);

        if (hasButtonBacklightSupport) {
            if (mBacklightTimeout != null) {
                mBacklightTimeout.setOnPreferenceChangeListener(this);
                int BacklightTimeout = Settings.System.getInt(getContentResolver(),
                        Settings.System.BUTTON_BACKLIGHT_TIMEOUT, 5000);
                mBacklightTimeout.setValue(Integer.toString(BacklightTimeout));
                mBacklightTimeout.setSummary(mBacklightTimeout.getEntry());
            }

            if (supportButtonBrightness == BUTTON_BRIGHTNESS_TOGGLE_MODE_ONLY) {
                hwkeyCat.removePreference(mButtonBrightness);
                if (mButtonBrightness_sw != null) {
                    mButtonBrightness_sw.setChecked((Settings.System.getFloat(getContentResolver(),
                            Settings.System.BUTTON_BRIGHTNESS, 1.0f) == 1));
                    mButtonBrightness_sw.setOnPreferenceChangeListener(this);
                }
            } else {
                hwkeyCat.removePreference(mButtonBrightness_sw);
                if (mButtonBrightness != null) {
                    float ButtonBrightness = Settings.System.getFloat(getContentResolver(),
                            Settings.System.BUTTON_BRIGHTNESS, 1.0f);
                    mButtonBrightness.setValue((int)(ButtonBrightness * 100.0f));
                    mButtonBrightness.setOnPreferenceChangeListener(this);
                }
            }
        } else {
            hwkeyCat.removePreference(mBacklightTimeout);
            hwkeyCat.removePreference(mButtonBrightness);
            hwkeyCat.removePreference(mButtonBrightness_sw);
        }

        final boolean defaultToNavigationBar = getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);
        final boolean navigationBarEnabled = Settings.System.getIntForUser(
                getActivity().getContentResolver(), Settings.System.FORCE_SHOW_NAVBAR,
                defaultToNavigationBar ? 1 : 0, UserHandle.USER_CURRENT) != 0;

        mNavigationBar = (SwitchPreference) findPreference(KEY_NAVIGATION_BAR_ENABLED);
        mNavigationBar.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FORCE_SHOW_NAVBAR,
                defaultToNavigationBar ? 1 : 0) == 1));
        mNavigationBar.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CARBONFIBERS;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBacklightTimeout) {
            String BacklightTimeout = (String) newValue;
            int BacklightTimeoutValue = Integer.parseInt(BacklightTimeout);
            Settings.System.putInt(resolver,
                    Settings.System.BUTTON_BACKLIGHT_TIMEOUT, BacklightTimeoutValue);
            int BacklightTimeoutIndex = mBacklightTimeout
                    .findIndexOfValue(BacklightTimeout);
            mBacklightTimeout
                    .setSummary(mBacklightTimeout.getEntries()[BacklightTimeoutIndex]);
            return true;
        } else if (preference == mButtonBrightness) {
            float value = (Integer) newValue;
            Settings.System.putFloat(resolver,
                    Settings.System.BUTTON_BRIGHTNESS, value / 100.0f);
            return true;
        } else if (preference == mButtonBrightness_sw) {
            boolean value = (Boolean) newValue;
            Settings.System.putFloat(resolver,
                    Settings.System.BUTTON_BRIGHTNESS, value ? 1.0f : -1.0f);
            return true;
        }
        if (preference == mNavigationBar) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                    Settings.System.FORCE_SHOW_NAVBAR, value ? 1 : 0);
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
                    final int supportButtonBrightness = context.getResources().getInteger(com.android.internal.R.integer
                                        .config_deviceSupportsButtonBrightnessControl);
                    final boolean hasButtonBacklightSupport = supportButtonBrightness != BUTTON_BRIGHTNESS_NOT_SUPPORTED;
                    if (!hasButtonBacklightSupport) {
                        keys.add(KEY_BUTTON_BRIGHTNESS);
                        keys.add(KEY_BUTTON_BRIGHTNESS_SW);
                        keys.add(KEY_BACKLIGHT_TIMEOUT);
                    } else if (supportButtonBrightness == BUTTON_BRIGHTNESS_TOGGLE_MODE_ONLY) {
                        keys.add(KEY_BUTTON_BRIGHTNESS);
                    } else {
                        keys.add(KEY_BUTTON_BRIGHTNESS_SW);
                    }
                    return keys;
                }
            };
}
