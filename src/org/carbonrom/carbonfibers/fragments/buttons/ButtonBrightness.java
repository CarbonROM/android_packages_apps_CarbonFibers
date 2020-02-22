/*
 *  Copyright (C) 2014 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.carbonrom.carbonfibers.fragments.buttons;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.SearchIndexableResource;

import androidx.preference.PreferenceCategory;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.carbon.CustomSettingsPreferenceFragment;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.util.ArrayList;
import java.util.List;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.carbon.CustomSeekBarPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

public class ButtonBrightness extends CustomSettingsPreferenceFragment implements
	Preference.OnPreferenceChangeListener, Indexable {

    private static final String KEY_BUTTON_BACKLIGHT_ENABLE = "button_backlight_enable";
    private static final String KEY_BUTTON_USE_SCREEN_BRIGHTNESS = "custom_button_use_screen_brightness";
    private static final String KEY_BUTTON_MANUAL_BRIGHTNESS_NEW = "button_manual_brightness_new";
    private static final String KEY_BUTTON_TIMEOUT = "button_timeout";
    private static final String KEY_BUTTON_BACKLIGHT_ON_TOUCH_ONLY = "button_backlight_on_touch_only";

    private CustomSeekBarPreference mButtonTimoutBar;
    private CustomSeekBarPreference mManualButtonBrightness;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.button_brightness);

        addCustomPreference(findPreference(KEY_BUTTON_BACKLIGHT_ENABLE), SYSTEM_TWO_STATE, STATE_ON);
        addCustomPreference(findPreference(KEY_BUTTON_USE_SCREEN_BRIGHTNESS), SYSTEM_TWO_STATE, STATE_OFF);
        addCustomPreference(findPreference(KEY_BUTTON_BACKLIGHT_ON_TOUCH_ONLY), SYSTEM_TWO_STATE, STATE_OFF);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mManualButtonBrightness = (CustomSeekBarPreference) findPreference(
                KEY_BUTTON_MANUAL_BRIGHTNESS_NEW);
        final int customButtonBrightness = getResources().getInteger(
                com.android.internal.R.integer.config_button_brightness_default);
        final int currentBrightness = Settings.System.getInt(resolver,
                Settings.System.CUSTOM_BUTTON_BRIGHTNESS, customButtonBrightness);
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);

        mManualButtonBrightness.setMax(pm.getMaximumScreenBrightnessSetting());
        mManualButtonBrightness.setValue(currentBrightness);
        mManualButtonBrightness.setOnPreferenceChangeListener(this);

        mButtonTimoutBar = (CustomSeekBarPreference) findPreference(KEY_BUTTON_TIMEOUT);
        int currentTimeout = Settings.System.getInt(resolver,
                Settings.System.BUTTON_BACKLIGHT_TIMEOUT, 0);
        mButtonTimoutBar.setValue(currentTimeout);
        mButtonTimoutBar.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mButtonTimoutBar) {
            int buttonTimeout = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.BUTTON_BACKLIGHT_TIMEOUT, buttonTimeout);
        } else if (preference == mManualButtonBrightness) {
            int buttonBrightness = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.CUSTOM_BUTTON_BRIGHTNESS, buttonBrightness);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CARBONFIBERS;
    }

    /**
     * For Search.
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider() {

            @Override
            public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                    boolean enabled) {
                ArrayList<SearchIndexableResource> result =
                        new ArrayList<SearchIndexableResource>();

                SearchIndexableResource sir = new SearchIndexableResource(context);
                sir.xmlResId = R.xml.button_brightness;
                if(context.getResources().getBoolean(
                        com.android.internal.R.bool.config_button_brightness_support)) {
                    result.add(sir);
                }
                return result;
            }

            @Override
            public List<String> getNonIndexableKeys(Context context) {
                final List<String> keys = super.getNonIndexableKeys(context);
                return keys;
            }
        };
}
