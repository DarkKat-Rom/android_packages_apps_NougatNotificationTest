/*
 * Copyright (C) 2016 DarkKat
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

package net.darkkatrom.nnotiftest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.preference.PreferenceManager;

import net.darkkatrom.nnotiftest.R;

public final class PreferenceUtils {

    public static final String NOTIFICATION_STYLE  = "notification_style";
    public static final String USE_PRIORITY        = "use_priority";
    public static final String SHOW_ACTION_BUTTONS = "show_action_buttons";

    public static final String CAT_NOTIFICATION_COLOR         = "cat_notification_color";
    public static final String SHOW_EMPHASIZED_ACTIONS        = "show_emphasized_actions";
    public static final String SHOW_TOMBSTONE_ACTIONS         = "show_tombstone_actions";
    public static final String SHOW_REPLY_ACTION              = "show_reply_action";
    public static final String USE_DEFAULT_NOTIFICATION_COLOR = "use_default_notification_color";
    public static final String NOTIFICATION_COLOR             = "notification_color";

    public static final int STYLE_DEFAULT = 0;

    private static PreferenceUtils sInstance;
    private final Context mContext;

    private final SharedPreferences mPreferences;

    public PreferenceUtils(final Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferenceUtils getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    public void setOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener){
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void setNotificationStyle(int style) {
        mPreferences.edit().putInt(NOTIFICATION_STYLE, style).commit();
    }

    public int getNotificationStyle() {
        return mPreferences.getInt(NOTIFICATION_STYLE, STYLE_DEFAULT);
    }

    public void setUsePriority(boolean use) {
        mPreferences.edit().putBoolean(USE_PRIORITY, use).commit();
    }

    public boolean getUsePriority() {
        return mPreferences.getBoolean(USE_PRIORITY, false);
    }

    public void setShowActionButtons(boolean show) {
        mPreferences.edit().putBoolean(SHOW_ACTION_BUTTONS, show).commit();
    }

    public boolean getShowActionButtons() {
        return mPreferences.getBoolean(SHOW_ACTION_BUTTONS, true);
    }

    public void setShowEmphasizedActions(boolean show) {
        mPreferences.edit().putBoolean(SHOW_EMPHASIZED_ACTIONS, show).commit();
    }

    public boolean getShowEmphasizedActions() {
        return mPreferences.getBoolean(SHOW_EMPHASIZED_ACTIONS, false);
    }

    public void setShowTombstoneActions(boolean show) {
        mPreferences.edit().putBoolean(SHOW_TOMBSTONE_ACTIONS, show).commit();
    }

    public boolean getShowTombstoneActions() {
        return mPreferences.getBoolean(SHOW_TOMBSTONE_ACTIONS, false);
    }

    public boolean getShowReplyAction() {
        return mPreferences.getBoolean(SHOW_REPLY_ACTION, false);
    }

    public void setUseDefaultNotificationColor(boolean use) {
        mPreferences.edit().putBoolean(USE_DEFAULT_NOTIFICATION_COLOR, use).commit();
    }

    public boolean getUseDefaultNotificationColor() {
        return mPreferences.getBoolean(USE_DEFAULT_NOTIFICATION_COLOR, false);
    }

    public int getNotificationColor() {
        if (getUseDefaultNotificationColor()) {
            return mContext.getResources().getColor(R.color.default_notification_color);
        } else {
            return mPreferences.getInt(NOTIFICATION_COLOR,
                    mContext.getResources().getColor(R.color.theme_accent));
        }
    }

    public int getFabIconColor() {
        return mContext.getResources().getColor(
                isColorGrayscale(getNotificationColor()) && !isColorDark(getNotificationColor())
                        ? R.color.floating_action_button_icon_color_dark
                        : R.color.floating_action_button_icon_color_light);
    }

    public static boolean isColorGrayscale(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Math.abs(r - g) < 20
                && Math.abs(r - b) < 20
                && Math.abs(g - b) < 20;
    }

    private static boolean isColorDark(int color) {
        double a = 1- (0.299 * Color.red(color)
                + 0.587 * Color.green(color)
                + 0.114 * Color.blue(color)) / 255;
        if (a < 0.5) {
            return false;
        } else {
            return true;
        }
    }
}
