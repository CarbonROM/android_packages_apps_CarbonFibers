/*
 *  Copyright (C) 2013 The OmniROM Project
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

package org.omnirom.omnigears.interfacesettings;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.UserManager;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.widget.LockPatternUtils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

import java.util.ArrayList;
import java.util.List;

import org.omnirom.omnigears.chameleonos.SeekBarPreference;

public class LockscreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "LockscreenSettings";

    private LockPatternUtils mLockPatternUtils;
    private DevicePolicyManager mDPM;

    private static final String KEY_MAXIMIZE_WIDGETS = "maximize_widgets";
    private static final String KEY_ENABLE_CAMERA = "enable_camera";
    private static final String BATTERY_AROUND_LOCKSCREEN_RING = "battery_around_lockscreen_ring";
    private static final String KEY_BLUR_RADIUS = "lockscreen_blur_radius";

    private CheckBoxPreference mMaximizeWidgets;
    private CheckBoxPreference mEnableCameraWidget;
    private CheckBoxPreference mLockRingBattery;
    private SeekBarPreference mBlurRadius;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lockscreen_settings);

        ContentResolver resolver = getContentResolver();
        PreferenceScreen root = getPreferenceScreen();
        mLockPatternUtils = new LockPatternUtils(getActivity());
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (!mLockPatternUtils.isLockScreenDisabled() && !mLockPatternUtils.isSecure()) {
            addPreferencesFromResource(R.xml.og_security_settings_chooser);
        }
        // Add the additional Omni settings
        mLockRingBattery = (CheckBoxPreference) root
                .findPreference(BATTERY_AROUND_LOCKSCREEN_RING);
        if (mLockRingBattery != null) {
            mLockRingBattery.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.BATTERY_AROUND_LOCKSCREEN_RING, 0) == 1);
        }


        // Enable or disable keyguard widget checkbox based on DPM state
        mMaximizeWidgets = (CheckBoxPreference) findPreference(KEY_MAXIMIZE_WIDGETS);
        if (ActivityManager.isLowRamDeviceStatic() || mLockPatternUtils.isLockScreenDisabled()) {
            // Widgets take a lot of RAM, so disable them on low-memory devices
            root.removePreference(mMaximizeWidgets);
        } else {
            mMaximizeWidgets.setChecked(Settings.System.getInt(resolver,
                    Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, 0) == 1);
        }

        // Enable or disable camera widget settings based on device
        mEnableCameraWidget = (CheckBoxPreference) findPreference(KEY_ENABLE_CAMERA);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                Camera.getNumberOfCameras() == 0) {
            root.removePreference(mEnableCameraWidget);
        } else if (isCameraDisabledByDpm()) {
            mEnableCameraWidget.setEnabled(false);
        } else {
            mEnableCameraWidget.setChecked(Settings.System.getInt(resolver,
                    Settings.System.LOCKSCREEN_ENABLE_CAMERA, 1) == 1);
        }

        // Lockscreen Blur
        mBlurRadius = (SeekBarPreference) findPreference(KEY_BLUR_RADIUS);
        mBlurRadius.setValue(Settings.System.getInt(resolver,
                Settings.System.LOCKSCREEN_BLUR_RADIUS, 12));
        mBlurRadius.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBlurRadius) {
            Settings.System.putInt(resolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, (Integer) objValue);
        }
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        mLockPatternUtils = new LockPatternUtils(getActivity());
        if (preference == mMaximizeWidgets) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, mMaximizeWidgets.isChecked() ? 1 : 0);
        } else if (preference == mLockRingBattery) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.BATTERY_AROUND_LOCKSCREEN_RING, mLockRingBattery.isChecked() ? 1 : 0);
        } else if (preference == mEnableCameraWidget) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_ENABLE_CAMERA, mEnableCameraWidget.isChecked() ? 1 : 0);
        }
        // If we didn't handle it, let preferences handle it.
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private boolean isCameraDisabledByDpm() {
        final DevicePolicyManager dpm =
                (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null) {
            try {
                final int userId = ActivityManagerNative.getDefault().getCurrentUser().id;
                final int disabledFlags = dpm.getKeyguardDisabledFeatures(null, userId);
                final  boolean disabledBecauseKeyguardSecure =
                        (disabledFlags & DevicePolicyManager.KEYGUARD_DISABLE_SECURE_CAMERA) != 0;
                return dpm.getCameraDisabled(null) || disabledBecauseKeyguardSecure;
            } catch (RemoteException e) {
                Log.e(TAG, "Can't get userId", e);
            }
        }
        return false;
    }
}
