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

package org.carbonrom.carbonfibers.fragments.gestures;

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.SearchIndexableResource;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.gestures.GestureSettings;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class Gestures extends GestureSettings implements Indexable {
    private static final String TAG = "Gestures";
    private static final String DOUBLE_TAP_STATUS_BAR_TO_SLEEP = "double_tap_sleep_gesture";
    private static final String DOUBLE_TAP_LOCK_SCREEN_TO_SLEEP = "double_tap_sleep_anywhere";

    private static final String KEY_ASSIST = "gesture_assist_input_summary";
    private static final String KEY_SWIPE_DOWN = "gesture_swipe_down_fingerprint_input_summary";
    private static final String KEY_DOUBLE_TAP_POWER = "gesture_double_tap_power_input_summary";
    private static final String KEY_DOUBLE_TWIST = "gesture_double_twist_input_summary";
    private static final String KEY_DOUBLE_TAP_SCREEN = "gesture_double_tap_screen_input_summary";
    private static final String KEY_PICK_UP = "gesture_pick_up_input_summary";
    private static final String KEY_PREVENT_RINGING = "gesture_prevent_ringing_summary";
    private static final String KEY_SWIPE_UP = "gesture_swipe_up_input_summary";

    private ContentResolver mContentResolver;
    private SwitchPreference mDoubleTapStatusBarToSleep;
    private SwitchPreference mDoubleTapLockScreenToSleep;

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CARBONFIBERS;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        mContentResolver = getActivity().getContentResolver();
        mDoubleTapStatusBarToSleep = (SwitchPreference) findPreference(DOUBLE_TAP_STATUS_BAR_TO_SLEEP);
        mDoubleTapLockScreenToSleep = (SwitchPreference) findPreference(DOUBLE_TAP_LOCK_SCREEN_TO_SLEEP);
        updatePreferences();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (mDoubleTapStatusBarToSleep == preference) {
            updateDoubleTapStatusBarToSleep(true);
            return true;
        } else if (mDoubleTapLockScreenToSleep == preference) {
            updateDoubleTapLockScreenToSleep(true);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void updateDoubleTapStatusBarToSleep(boolean clicked) {
        if (clicked) {
            Settings.System.putIntForUser(mContentResolver,
                    Settings.System.DOUBLE_TAP_SLEEP_GESTURE,
                    (mDoubleTapStatusBarToSleep.isChecked() ? 1 : 0),
                    UserHandle.USER_CURRENT);
        }
        boolean isDoubleTapStatusBarToSleep = (Settings.System.getIntForUser(
                mContentResolver, Settings.System.DOUBLE_TAP_SLEEP_GESTURE,
                0, UserHandle.USER_CURRENT) == 1);
        mDoubleTapStatusBarToSleep.setChecked(isDoubleTapStatusBarToSleep);
    }

    private void updateDoubleTapLockScreenToSleep(boolean clicked) {
        if (clicked) {
            Settings.System.putIntForUser(mContentResolver,
                    Settings.System.DOUBLE_TAP_SLEEP_ANYWHERE,
                    (mDoubleTapLockScreenToSleep.isChecked() ? 1 : 0),
                    UserHandle.USER_CURRENT);
        }
        boolean isDoubleTapLockScreenToSleep = (Settings.System.getIntForUser(
                mContentResolver, Settings.System.DOUBLE_TAP_SLEEP_ANYWHERE,
                0, UserHandle.USER_CURRENT) == 1);
        mDoubleTapLockScreenToSleep.setChecked(isDoubleTapLockScreenToSleep);
    }

    private void updatePreferences() {
        updateDoubleTapStatusBarToSleep(false);
        updateDoubleTapLockScreenToSleep(false);
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.gestures;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    // Duplicates in summary and details pages.
                    keys.add(KEY_SWIPE_DOWN);
                    keys.add(KEY_DOUBLE_TAP_POWER);
                    keys.add(KEY_DOUBLE_TWIST);
                    keys.add(KEY_SWIPE_UP);
                    keys.add(KEY_DOUBLE_TAP_SCREEN);
                    keys.add(KEY_PICK_UP);
                    keys.add(KEY_PREVENT_RINGING);
                    keys.add(ACTIVE_EDGE_CATEGORY);
                    return keys;
                }
            };
}
