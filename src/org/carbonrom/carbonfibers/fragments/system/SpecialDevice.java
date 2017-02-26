/*
 *  Copyright (C) 2016 CarbonROM
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
 *
 */

package org.carbonrom.carbonfibers.fragments.system;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;

import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.internal.util.cr.CrUtils;
import com.android.settings.Utils;

public class SpecialDevice extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

   private static final String KEY_ONEPLUSDOZE = "oneplusdoze";
   private static final String KEY_ONEPLUSDOZE_PACKAGE_NAME = "com.cyanogenmod.settings.doze";
   private static final String KEY_ONEPLUS_GESTURES = "oneplus_gestures";
   private static final String KEY_ONEPLUS_GESTURES_PACKAGE_NAME = "com.cyanogenmod.settings.device";
   private PreferenceScreen mOneplusDoze;
   private PreferenceScreen mOneplusGestures;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      addPreferencesFromResource(R.xml.special_device);

      ContentResolver resolver = getActivity().getContentResolver();
      PreferenceScreen prefSet = getPreferenceScreen();

      mOneplusDoze = (PreferenceScreen) findPreference(KEY_ONEPLUSDOZE);
      if (!CrUtils.isPackageInstalled(getActivity(), KEY_ONEPLUSDOZE_PACKAGE_NAME)) {
          prefSet.removePreference(mOneplusDoze);
       }

     mOneplusGestures = (PreferenceScreen) findPreference(KEY_ONEPLUS_GESTURES);
     if (!CrUtils.isPackageInstalled(getActivity(), KEY_ONEPLUS_GESTURES_PACKAGE_NAME)) {
         prefSet.removePreference(mOneplusGestures);
      }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.CARBONFIBERS;
    }

    @Override
    public void onResume() {
       super.onResume();
    }

    @Override
    public void onPause() {
       super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object objvalue) {
        final String key = preference.getKey();
        return true;
    }

}
