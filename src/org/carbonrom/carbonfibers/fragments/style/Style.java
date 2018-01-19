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

package org.carbonrom.carbonfibers.fragments.style;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.util.ArrayMap;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.display.ThemePreferenceController;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.core.AbstractPreferenceController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Style extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener,
        PreferenceManager.OnPreferenceTreeClickListener {
    private static final String TAG = "Style";
    private Context mContext;
    private final Map<Class, AbstractPreferenceController> mPreferenceControllers =
            new ArrayMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.style);
        ContentResolver resolver = getActivity().getContentResolver();
        updatePreferenceStates();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        List<AbstractPreferenceController> controllers = getPreferenceControllers(context);
        if (controllers == null) {
            controllers = new ArrayList<>();
        }
        for (AbstractPreferenceController controller : controllers) {
            addPreferenceController(controller);
        }
        mContext = context;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CARBONFIBERS;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferenceStates();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        Collection<AbstractPreferenceController> controllers = mPreferenceControllers.values();
        // Give all controllers a chance to handle click.
        for (AbstractPreferenceController controller : controllers) {
            if (controller.handlePreferenceTreeClick(preference)) {
                return true;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    protected <T extends AbstractPreferenceController> T getPreferenceController(Class<T> clazz) {
        AbstractPreferenceController controller = mPreferenceControllers.get(clazz);
        return (T) controller;
    }

    protected void addPreferenceController(AbstractPreferenceController controller) {
        mPreferenceControllers.put(controller.getClass(), controller);
    }

    protected void updatePreferenceStates() {
        Collection<AbstractPreferenceController> controllers = mPreferenceControllers.values();
        for (AbstractPreferenceController controller : controllers) {
            if (!controller.isAvailable()) {
                continue;
            }

            final String key = controller.getPreferenceKey();
            final Preference preference = getPreferenceScreen().findPreference(key);
            controller.updateState(preference);
        }
    }

    protected List<AbstractPreferenceController> getPreferenceControllers(Context context) {
        return buildPreferenceControllers(context);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new ThemePreferenceController(context));
        return controllers;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return true;
    }

}
