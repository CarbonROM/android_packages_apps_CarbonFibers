/*
 * Copyright (C) 2020 CarbonROM
 *
 * Author: calebcabob <calphonic@gmail.com>
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

import static android.provider.Settings.Global.putString;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContentResolver;
import android.net.Network;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.settings.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class CaptivePortalCustomizer extends DialogFragment {

    public static final String TAG_CUSTOM_CAPTIVE_PORTAL = "CaptivePortalCustomizer";
    private Dialog dialog;
    private View view;
    private ContentResolver mContentResolver;

    private String finalUrlString;
    private RadioButton userUrlBtn;
    private RadioButton ubuntuBtn;
    private RadioButton yxaBtn;
    private RadioButton googleBtn;
    private RadioGroup urlGroup;
    private EditText editText;
    private Button resetDefault;
    private Button okButton;

    /* Custom url verification */
    private Thread mTestingThread = null;
    private boolean validUrl = false;

    public CaptivePortalCustomizer() {
    }

    public static CaptivePortalCustomizer newInstance() {
        return new CaptivePortalCustomizer ();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new Dialog(Objects.requireNonNull(getActivity()), R.style.CustomCaptivePortal);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.captive_portal_chooser, container, false);

        // Bind views
        resetDefault = view.findViewById(R.id.reset_default);
        okButton = view.findViewById(R.id.submit);
        urlGroup = view.findViewById(R.id.radioGroup);
        userUrlBtn = view.findViewById(R.id.url_custom);
        editText = view.findViewById(R.id.enter_custom_url);
        googleBtn = view.findViewById(R.id.url_default);
        ubuntuBtn = view.findViewById(R.id.url_ubuntu);
        // Soon to be Carbon
        yxaBtn = view.findViewById(R.id.url_yxa);

        if (view != null) {
            viewDetails();
        }

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = (inflater.inflate(R.layout.captive_portal_chooser, null));
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    private void viewDetails() {
        urlGroup.setOnCheckedChangeListener(
            new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup urlGroup, @IdRes int checkedId) {
                }
            });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    userUrlBtn.setChecked(true);
                }
            }
        });

        googleBtn.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideSoftInput(getActivity());
                }
            });

        ubuntuBtn.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideSoftInput(getActivity());
                }
            });

        yxaBtn.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideSoftInput(getActivity());
                }
            });

        userUrlBtn.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editText.setFocusableInTouchMode(true);
                    final InputMethodManager imm =
                        (InputMethodManager)(getActivity())
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                    editText.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                editText.requestFocus();
                                imm.showSoftInput(editText, 0);
                            }
                        },
                        100);
                }
            });

        resetDefault.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    urlGroup.clearCheck();
                    urlGroup.check(R.id.url_default);

                    finalUrlString = getString(R.string.raw_url_default);
                    sendUrlDataToSystem((getContext()), finalUrlString);

                    Toast.makeText(getActivity(),
                        getString(R.string.toast_url_default),
                            Toast.LENGTH_LONG).show();

                    if (editText != null) {
                        editText.clearFocus();
                    }
                    dismiss();
                }
            });

        okButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userHasCustomUrl()) {
                        validateCustomUrl();
                    }
                    mFinalizeSelection();
                    dismiss();
                }
            });
    }

    private static void hideSoftInput(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void mFinalizeSelection() {
        if (!userHasCustomUrl()) {
            /* Pass Url strings associated with checked option */
            validUrl = true;
            if (googleBtn.isChecked()) {
                finalUrlString = getString(R.string.raw_url_default);
            } else if (ubuntuBtn.isChecked()) {
                finalUrlString = getString(R.string.raw_url_ubuntu);
                // Soon to be Carbon
            } else if (yxaBtn.isChecked()) {
                finalUrlString = getString(R.string.raw_url_yxa);
            }
            Toast.makeText(getActivity(),
                "New Captive Portal URL:\n" + finalUrlString,
                    Toast.LENGTH_LONG).show();
            sendUrlDataToSystem(
                Objects.requireNonNull(getContext()), finalUrlString);
            /*
             *  Custom Url button selected. See if it is valid.
             *  validUrl boolean is returned in validateCustomUrl().
             */
        } else if (userHasCustomUrl() && validUrl) {
            finalUrlString = (editText.getText().toString());
            Toast.makeText(getActivity(),
                "New Captive Portal URL:\n" + finalUrlString,
                     Toast.LENGTH_LONG).show();
            sendUrlDataToSystem(
                Objects.requireNonNull(getContext()), finalUrlString);
            /* Custom Url can't be verified. Bail and tell the user. */
        } else {
            Toast.makeText(getActivity(),
                "Chosen URL is invalid. Resetting to system default",
                    Toast.LENGTH_LONG).show();
        }
        if (editText != null) {
            editText.clearFocus();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /** Send new valid Url to system */
    private void sendUrlDataToSystem(Context context, String finalUrlString) {
        if (validUrl = true) {
           mContentResolver = context.getContentResolver();
           Settings.Global.putString(mContentResolver,
                   Settings.Global.CAPTIVE_PORTAL_HTTP_URL, finalUrlString);
        }
    }

    private boolean userHasCustomUrl() {
        return userUrlBtn.isChecked();
    }

    /**
     * Validate new Url. Server needs to return a response code of
     * "204" (no content) in order to work properly with AOSP.
     */
    private void validateCustomUrl() {
        mTestingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                URL url = null;
                try {
                    url = new URL(finalUrlString);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpsURLConnection connection = null;
                try {
                    connection = (HttpsURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int responseCode = HTTP_INTERNAL_ERROR;
                try {
                    System.out.println(
                        "Sending request to server for" + finalUrlString);
                    System.out.println("Waiting for response code");
                    connection.setConnectTimeout(10 * 1000);
                    connection.setReadTimeout(10 * 1000);
                    connection.setUseCaches(false);
                    connection.getInputStream();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    responseCode = connection.getResponseCode();
                } catch (IOException e) {
                    System.err.println("Caught IOException: " + e.getMessage());
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                if (responseCode == HTTP_NO_CONTENT) {
                    validUrl = true;
                    done();
                }
                mTestingThread.start();
            }
        });
    }

    private void done() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        done();
    }
}
