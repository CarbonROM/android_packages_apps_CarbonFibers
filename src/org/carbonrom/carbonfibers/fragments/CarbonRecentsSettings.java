package org.carbonrom.carbonfibers.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.carbonrom.carbonfibers.R;

public class CarbonRecentsSettings extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.carbon_recents_settings);
    }
}