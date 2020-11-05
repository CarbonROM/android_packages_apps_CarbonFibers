/*
 * Copyright (C) 2016 The CyanogenMod Project
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
package org.carbonrom.carbonfibers.search;

import android.annotation.XmlRes;
import android.content.Context;
import android.provider.SearchIndexableResource;
import android.util.Log;
import android.os.Bundle;

import androidx.annotation.CallSuper;

import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexableRaw;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Convenience class which can be used to return additional search metadata without
 * having to implement all methods.
 */
public class BaseSearchIndexProvider implements Indexable.SearchIndexProvider {

    private String TAG = "BaseSearchIndexProvider";

    public BaseSearchIndexProvider() {
    }

    public BaseSearchIndexProvider(int xmlRes) {
        mXmlRes = xmlRes;
    }

    @Override
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean enabled) {
        if (mXmlRes != 0) {
            final SearchIndexableResource sir = new SearchIndexableResource(context);
            sir.xmlResId = mXmlRes;
            return Arrays.asList(sir);
        }
        return null;
    }

    @Override
    @CallSuper
    public List<SearchIndexableRaw> getDynamicRawDataToIndex(Context context, boolean enabled) {
        return null;
    }

    @Override
    @CallSuper
    public List<String> getNonIndexableKeys(Context context) {
        if (!isPageSearchEnabled(context)) {
            // Entire page should be suppressed, mark all keys from this page as non-indexable.
            return getNonIndexableKeysFromXml(context, true /* suppressAllPage */);
        }
        final List<String> nonIndexableKeys = new ArrayList<>();
        nonIndexableKeys.addAll(getNonIndexableKeysFromXml(context, false /* suppressAllPage */));
        final List<AbstractPreferenceController> controllers = getPreferenceControllers(context);
        if (controllers != null && !controllers.isEmpty()) {
            for (AbstractPreferenceController controller : controllers) {
                if (controller instanceof PreferenceControllerMixin) {
                    ((PreferenceControllerMixin) controller)
                            .updateNonIndexableKeys(nonIndexableKeys);
                } else if (controller instanceof BasePreferenceController) {
                    ((BasePreferenceController) controller).updateNonIndexableKeys(
                            nonIndexableKeys);
                } else {
                    Log.e(TAG, controller.getClass().getName()
                            + " must implement " + PreferenceControllerMixin.class.getName()
                            + " treating the key non-indexable");
                    nonIndexableKeys.add(controller.getPreferenceKey());
                }
            }
        }
        return nonIndexableKeys;
    }

    /**
     * Returns true if the page should be considered in search query. If return false, entire page
     * will be suppressed during search query.
     */
    protected boolean isPageSearchEnabled(Context context) {
        return true;
    }

    /**
     * Get all non-indexable keys from xml. If {@param suppressAllPage} is set, all keys are
     * considered non-indexable. Otherwise, only keys with searchable="false" are included.
     */
    private List<String> getNonIndexableKeysFromXml(Context context, boolean suppressAllPage) {
        final List<SearchIndexableResource> resources = getXmlResourcesToIndex(
                context, true /* not used*/);
        if (resources == null || resources.isEmpty()) {
            return new ArrayList<>();
        }
        final List<String> nonIndexableKeys = new ArrayList<>();
        for (SearchIndexableResource res : resources) {
            nonIndexableKeys.addAll(
                    getNonIndexableKeysFromXml(context, res.xmlResId, suppressAllPage));
        }
        return nonIndexableKeys;
    }

    public List<String> getNonIndexableKeysFromXml(Context context, @XmlRes int xmlResId,
            boolean suppressAllPage) {
        return getKeysFromXml(context, xmlResId, suppressAllPage);
    }

    private List<String> getKeysFromXml(Context context, @XmlRes int xmlResId,
            boolean suppressAllPage) {
        final List<String> keys = new ArrayList<>();
        try {
            final List<Bundle> metadata = PreferenceXmlParserUtils.extractMetadata(context,
                    xmlResId, FLAG_NEED_KEY | FLAG_INCLUDE_PREF_SCREEN | FLAG_NEED_SEARCHABLE);
            for (Bundle bundle : metadata) {
                if (suppressAllPage || !bundle.getBoolean(METADATA_SEARCHABLE, true)) {
                    keys.add(bundle.getString(METADATA_KEY));
                }
            }
        } catch (IOException | XmlPullParserException e) {
            Log.w(TAG, "Error parsing non-indexable from xml " + xmlResId);
        }
        return keys;
    }
}