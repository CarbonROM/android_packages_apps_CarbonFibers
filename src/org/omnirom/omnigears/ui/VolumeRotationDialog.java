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
package org.omnirom.omnigears.ui;

import com.android.settings.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.android.settings.Utils;

public class VolumeRotationDialog extends AlertDialog implements
              DialogInterface.OnClickListener, CheckBox.OnCheckedChangeListener {

    private CheckBox mDisabled270;
    private CheckBox mDisabled90;
    private Context mContext;

    public VolumeRotationDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final View view = getLayoutInflater().inflate(R.layout.volume_rotation_dialog,
                null);
        setView(view);
        setTitle(R.string.swap_volume_advanced_settings_title);
        setCancelable(true);

        setButton(DialogInterface.BUTTON_NEUTRAL,
                mContext.getString(R.string.swap_volume_advanced_settings_close), this);

        mDisabled270 = (CheckBox) view.findViewById(R.id.swap_volume_disabled_270);
        mDisabled270.setOnCheckedChangeListener(this);
        mDisabled270.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SWAP_VOLUME_DISABLED_270,
                    Utils.isTablet(mContext) ? 0 : 1) != 0);

        mDisabled90 = (CheckBox) view.findViewById(R.id.swap_volume_disabled_90);
        mDisabled90.setOnCheckedChangeListener(this);
        mDisabled90.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SWAP_VOLUME_DISABLED_90,
                    Utils.isTablet(mContext) ? 1 : 0) != 0);

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mDisabled90) {
            Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SWAP_VOLUME_DISABLED_90, isChecked ? 1:0);
        } else if (buttonView == mDisabled270) {
            Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SWAP_VOLUME_DISABLED_270, isChecked ? 1:0);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            cancel();
        }
    }
}
