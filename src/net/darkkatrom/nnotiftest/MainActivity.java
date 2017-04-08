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

package net.darkkatrom.nnotiftest;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import net.darkkatrom.nnotiftest.utils.PreferenceUtils;

public class MainActivity extends Activity implements  View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    public static final String TYPE_BUTTONS_CHECKED_ID = "type_button_checked_id";
    public static final String SET_PRIORITY_CHECKED    = "set_priority_checked";
    public static final String SHOW_ACTION_BUTTONS     = "show_action_buttons";

    private PreferenceUtils mUtils;
    private NotificationManager mManager;
    private Randomizer mRandomizer;

    private RadioGroup mTypeButtonsGroup;
    private RadioGroup mPriorityGroup;
    private RadioGroup mActionButtonsGroup;

    private Switch mSetPriority;
    private Switch mShowActionButtons;
    private ImageView mFab;

    private GradientDrawable mFabBackground;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUtils = new PreferenceUtils(getApplicationContext());
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mRandomizer = new Randomizer(this);

        setContentView(R.layout.activity_main);

        mTypeButtonsGroup = (RadioGroup) findViewById(R.id.type_buttons_group);
        mPriorityGroup = (RadioGroup) findViewById(R.id.priority_group);
        mActionButtonsGroup = (RadioGroup) findViewById(R.id.number_of_actions_buttons_group);

        mSetPriority = (Switch) findViewById(R.id.set_priority_switch);
        mShowActionButtons = (Switch) findViewById(R.id.show_action_buttons_switch);
        mFab = (ImageView) findViewById(R.id.floating_action_button);

        mFabBackground = (GradientDrawable) findViewById(R.id.floating_action_button_container)
                .getBackground().mutate();

        mTypeButtonsGroup.setOnCheckedChangeListener(this);

        mSetPriority.setOnCheckedChangeListener(this);
        mShowActionButtons.setOnCheckedChangeListener(this);
        mFab.setOnClickListener(this);

        if (savedInstanceState == null) {
            onCheckedChanged(mTypeButtonsGroup, R.id.type_default);
            onCheckedChanged(mSetPriority, mSetPriority.isChecked());
            mShowActionButtons.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i) instanceof LinearLayout) {
                LinearLayout l = (LinearLayout) group.getChildAt(i);
                for (int j = 0; j < l.getChildCount(); j++) {
                    if (l.getChildAt(j) instanceof ToggleButton) {
                        ToggleButton tb = (ToggleButton) l.getChildAt(j);
                        tb.setChecked(tb.getId() == checkedId);
                    }
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.show_action_buttons_switch) {
            for (int i = 0; i < mActionButtonsGroup.getChildCount(); i++) {
                ((RadioButton) mActionButtonsGroup.getChildAt(i)).setEnabled(isChecked);
            }
        } else if (buttonView.getId() == R.id.set_priority_switch) {
            for (int i = 0; i < mPriorityGroup.getChildCount(); i++) {
                ((RadioButton) mPriorityGroup.getChildAt(i)).setEnabled(isChecked);
            }
        }
    }

    public void onToggle(View view) {
        updateTypeState(view);
    }

    private void updateTypeState(View view) {
        mTypeButtonsGroup.check(0);
        mTypeButtonsGroup.check(view.getId());
        int fabIconResId = R.drawable.ic_send;
        switch (view.getId()) {
            default:
            case R.id.type_default:
                break;
            case R.id.type_big_text:
                fabIconResId = R.drawable.ic_status_bar_text;
                break;
            case R.id.type_big_picture:
                fabIconResId = R.drawable.ic_status_bar_image;
                break;
            case R.id.type_inbox:
            case R.id.type_messaging:
                fabIconResId = R.drawable.ic_status_bar_message;
                break;
            case R.id.type_media:
                fabIconResId = R.drawable.ic_status_bar_media;
                break;
        }

        mFab.setImageResource(fabIconResId);
    }

    @Override
    public void onClick(View v) {
        Notification notification = buildNotification();
        if (notification != null) {
            sendNotification(notification);
        }
    }

    private Notification buildNotification() {
        Notification notification = null;
        Notification.Builder builder = new Notification.Builder(this)
            .setShowWhen(true)
            .setWhen(System.currentTimeMillis())
            .setSubText(getResources().getString(R.string.notification_sub_text))
            .setContentTitle(getResources().getString(R.string.notification_title))
            .setContentText(getResources().getString(R.string.notification_content_text));

        if (mTypeButtonsGroup.getCheckedRadioButtonId() != R.id.type_media) {
            setButtons(builder);
        }
        if (mSetPriority.isChecked()) {
            setPriority(builder);
        }
        if (mUtils.getShowEmphasizedActions() && !mUtils.getShowTombstoneActions()) {
            PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(), 0);
            builder.setFullScreenIntent(intent, false);
        }
        if (!mUtils.getUseDefaultNotificationColor()) {
            builder.setColor(mUtils.getNotificationColor());
        }

        switch (mTypeButtonsGroup.getCheckedRadioButtonId()) {
            default:
            case R.id.type_default:
                notification = getDefaultNotification(builder);
                break;
            case R.id.type_big_text:
                notification = getBigTextStyle(builder);
                break;
            case R.id.type_big_picture:
                notification = getBigPictureStyle(builder);
                break;
            case R.id.type_inbox:
                notification = getInboxStyle(builder);
                break;
            case R.id.type_messaging:
                notification = getMessagingStyle(builder);
                break;
            case R.id.type_media:
                notification = getMediaStyle(builder);
                break;
        }
        return notification;

    }

    public void showSettings(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void setPriority(Notification.Builder builder) {
        int priority = Notification.PRIORITY_MAX;
        switch (mPriorityGroup.getCheckedRadioButtonId()) {
            case R.id.priority_min:
                priority = Notification.PRIORITY_MIN;
                break;
            case R.id.priority_low:
                priority = Notification.PRIORITY_LOW;
                break;
            case R.id.priority_default:
                priority = Notification.PRIORITY_DEFAULT;
                break;
            case R.id.priority_high:
                priority = Notification.PRIORITY_HIGH;
                break;
            case R.id.priority_max:
                priority = Notification.PRIORITY_MAX;
                break;
        }
        builder.setPriority(priority);
    }

    private void setButtons(Notification.Builder builder) {
        int buttons = 0;
        if (mShowActionButtons.isChecked()) {
            switch (mActionButtonsGroup.getCheckedRadioButtonId()) {
                case R.id.action_buttons_1:
                    buttons = 1;
                    break;
                case R.id.action_buttons_2:
                    buttons = 2;
                    break;
                case R.id.action_buttons_3:
                    buttons = 3;
                    break;
            }
        }

        // Add as many buttons as you have to
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(), 0);
        boolean showReplyAction = mUtils.getShowReplyAction();
        for (int i = 0; i < buttons; i++) {
            boolean showTombstoneActions = mUtils.getShowTombstoneActions()
                    && !mUtils.getShowEmphasizedActions() && i != 1;
            if (showReplyAction && i == 0) {
                builder.addAction(getReplyAction());
            } else {
                builder.addAction(mRandomizer.getRandomIconId(), getResources().getString(
                        R.string.notification_action_text, (i + 1)), showTombstoneActions ? null : intent);
            }
        }
    }

    private Notification getDefaultNotification(Notification.Builder builder) {
        builder.setSmallIcon(mRandomizer.getRandomSmallIconId());

        return builder.build();
    }

    private Notification.Action getReplyAction() {
        Intent resultIntent = new Intent(this, NotificationReplyReceiver.class);
        resultIntent.setAction(NotificationReplyReceiver.ACTION_NOTIFICATION_REPLY);
        resultIntent.putExtra(PreferenceUtils.NOTIFICATION_ID, mUtils.getNotificationId());
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                mUtils.getNotificationId(), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String replyHint = getResources().getString(R.string.notification_action_reply_hint);
        RemoteInput remoteInput = new RemoteInput.Builder(PreferenceUtils.TEXT_REPLY)
            .setLabel(replyHint)
            .build();

        Notification.Action action = new Notification.Action.Builder(mRandomizer.getRandomIconId(),
                getResources().getString(R.string.notification_action_reply_title), resultPendingIntent)
                .addRemoteInput(remoteInput)
                .build();
        return action;
    }

    private Notification getBigTextStyle(Notification.Builder builder) {
        Notification.BigTextStyle style = new Notification.BigTextStyle()
            .bigText(getResources().getString(R.string.big_text));

        builder.setSmallIcon(R.drawable.ic_status_bar_text)
            .setStyle(style);

        return builder.build();
	}

    private Notification getBigPictureStyle(Notification.Builder builder) {
        Bitmap large = mRandomizer.getRandomImage();
        Notification.BigPictureStyle style = new Notification.BigPictureStyle()
            .bigPicture(large);

        builder.setSmallIcon(R.drawable.ic_status_bar_image)
            .setStyle(style)
            .setLargeIcon(large);

        return builder.build();
    }

    private Notification getInboxStyle(Notification.Builder builder) {
        Bitmap large = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Notification.InboxStyle style = new Notification.InboxStyle();
        addInboxLines(style);

        builder.setSmallIcon(R.drawable.ic_status_bar_message)
            .setStyle(style)
            .setLargeIcon(large);

        return builder.build();
    }

    private Notification getMessagingStyle(Notification.Builder builder) {
        Bitmap large = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Notification.MessagingStyle style = new Notification.MessagingStyle(
                getResources().getString(R.string.app_name))
            .setConversationTitle(getResources().getString(
                    R.string.conversation_title));
        addMessages(style);

        builder.setSmallIcon(R.drawable.ic_status_bar_message)
            .setStyle(style)
            .setLargeIcon(large);

        return builder.build();
    }

    private Notification getMediaStyle(Notification.Builder builder) {
        Notification.MediaStyle style = new Notification.MediaStyle()
                .setShowActionsInCompactView(1, 2, 3);
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(), 0);

        builder.setSmallIcon(R.drawable.ic_status_bar_media)
            .setStyle(style)
            .setLargeIcon(mRandomizer.getRandomImage())
            .addAction(R.drawable.ic_action_fast_rewind,
                    getResources().getString(R.string.fast_rewind_title), intent)
            .addAction(R.drawable.ic_action_skip_previous,
                    getResources().getString(R.string.skip_previous_title), intent)
            .addAction(R.drawable.ic_action_play,
                    getResources().getString(R.string.play_title), intent)
            .addAction(R.drawable.ic_action_skip_next,
                    getResources().getString(R.string.skip_next_title), intent)
            .addAction(R.drawable.ic_action_fast_forward,
                    getResources().getString(R.string.fast_forward_title), intent);
        return builder.build();
    }

    private void addInboxLines(Notification.InboxStyle style) {
        for (int i = 0; i < 6; i++) {
            style.addLine(getResources().getString(R.string.line_number_text, (i + 1)));
        }
    }

    private void addMessages(Notification.MessagingStyle style) {
        for (int i = 0; i < 6; i++) {
            style.addMessage(getResources().getString(R.string.line_number_text, (i + 1)),
                    System.currentTimeMillis(),getResources().getString(R.string.app_name));
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final View checkedTypeButton = findViewById(savedInstanceState.getInt(
                TYPE_BUTTONS_CHECKED_ID, R.id.type_default));
        final boolean priorityChecked = savedInstanceState.getBoolean(SET_PRIORITY_CHECKED, false);
        final boolean showActionButtons = savedInstanceState.getBoolean(SHOW_ACTION_BUTTONS, false);

        if (checkedTypeButton == null) {
            updateTypeState(findViewById(R.id.type_default));
        } else {
            updateTypeState(checkedTypeButton);
        }
        if (!priorityChecked) {
            onCheckedChanged(mSetPriority, false);
        }
        if (!showActionButtons) {
            onCheckedChanged(mShowActionButtons, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mFabBackground.setColor(ColorStateList.valueOf(mUtils.getNotificationColor()));
        mFab.setImageTintList(ColorStateList.valueOf(mUtils.getFabIconColor()));
    }
    

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(TYPE_BUTTONS_CHECKED_ID, mTypeButtonsGroup.getCheckedRadioButtonId());
        outState.putBoolean(SET_PRIORITY_CHECKED, mSetPriority.isChecked());
        outState.putBoolean(SHOW_ACTION_BUTTONS, mShowActionButtons.isChecked());
        super.onSaveInstanceState(outState);
    }

    public void sendNotification(Notification notif) {
        int notificationId = mUtils.getNotificationId();
        mManager.notify(notificationId, notif);
        int nextNotificationId = notificationId == 100 ? 1 : notificationId + 1;
        mUtils.setNotificationId(nextNotificationId);
    }
}
