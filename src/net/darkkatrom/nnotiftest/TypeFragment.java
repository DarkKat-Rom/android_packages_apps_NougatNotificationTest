package net.darkkatrom.nnotiftest;

import java.util.Random;

import android.app.Fragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Switch;

public class TypeFragment extends Fragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private MainActivity mContext;

    private Button mRandom, mDefault, mOld, mBigText, mBigPicture, mInbox, mBigMedia;

    private Randomizer mRandomizer;

    private Switch mEnableActionButtons;
    private RadioGroup mActionButtonsGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (MainActivity) getActivity();

        mRandomizer = new Randomizer(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_types, container, false);

        mRandom = (Button) v.findViewById(R.id.type_random);
        mDefault = (Button) v.findViewById(R.id.type_default);
        mOld = (Button) v.findViewById(R.id.type_old);
        mBigText = (Button) v.findViewById(R.id.type_big_text);
        mBigPicture = (Button) v.findViewById(R.id.type_big_picture);
        mInbox = (Button) v.findViewById(R.id.type_inbox);
        mBigMedia = (Button) v.findViewById(R.id.type_big_media);

        mEnableActionButtons = (Switch) v.findViewById(R.id.enable_action_buttons_switch);
        mActionButtonsGroup = (RadioGroup) v.findViewById(R.id.number_of_actions_buttons_group);

        mRandom.setOnClickListener(this);
        mDefault.setOnClickListener(this);
        mOld.setOnClickListener(this);
        mBigText.setOnClickListener(this);
        mBigPicture.setOnClickListener(this);
        mInbox.setOnClickListener(this);
        mBigMedia.setOnClickListener(this);

        mEnableActionButtons.setOnCheckedChangeListener(this);
        onCheckedChanged(mEnableActionButtons, mEnableActionButtons.isChecked());

        return v;
    }

    @Override
    public void onClick(View v) {
        Notification notif = null;
        Notification.Builder builder = new Notification.Builder(mContext);

        // If random, add random buttons and take a random type
        if (v.getId() == R.id.type_random) {
            setRandomButtons(builder);
            switch (new Random().nextInt(5)) {
                case 0:
                    // default
                    notif = getDefaultNotification(builder);
                    break;
                case 1:
                    // big text
                    notif = getBigTextStyle(builder);
                    break;
                case 2:
                    // big picture
                    notif = getBigPictureStyle(builder);
                    break;
                case 3:
                    // inbox
                    notif = getInboxStyle(builder);
                    break;
                case 4:
                    // big media
                    notif = getMediaStyle(builder);
                    break;
            }
        } else if (v.getId() == R.id.type_big_media) {
            notif = getMediaStyle(builder);
        } else {
            // Set selected buttons
            setButtons(builder, null);
            // And the selected type
            switch (v.getId()) {
                case R.id.type_big_text:
                    notif = getBigTextStyle(builder);
                    break;
                case R.id.type_inbox:
                    notif = getInboxStyle(builder);
                    break;
                case R.id.type_big_media:
                    notif = getMediaStyle(builder);
                    break;
                case R.id.type_big_picture:
                    notif = getBigPictureStyle(builder);
                    break;
                case R.id.type_old:
                    notif = getOldNotification();
                    break;
                default:
                    notif = getDefaultNotification(builder);
                    break;
            }
        }

        mContext.sendNotification(notif);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (int i = 0; i < mActionButtonsGroup.getChildCount(); i++) {
            ((RadioButton) mActionButtonsGroup.getChildAt(i)).setEnabled(isChecked);
        }
    }

    private Notification getDefaultNotification(Notification.Builder builder) {
        builder.setSmallIcon(mRandomizer.getRandomSmallIconId())
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getResources().getString(
                        R.string.type_default_notification_title_text))
                .setContentText(getResources().getString(
                        R.string.type_default_notification_content_text))
                .setContentInfo(getResources().getString(R.string.notification_info_text))
                .setColor(getResources().getColor(R.color.theme_accent));
        return builder.build();
    }

    private Notification getBigTextStyle(Notification.Builder builder) {
        builder.setSmallIcon(R.drawable.ic_status_bar_text)
                .setContentTitle(getResources().getString(R.string.type_text_notification_title_text))
                .setContentText(getResources().getString(R.string.type_text_notification_content_text))
                .setContentInfo(getResources().getString(R.string.notification_info_text))
                .setColor(getResources().getColor(R.color.theme_accent));

        return new Notification.BigTextStyle(builder)
                .bigText(getResources().getString(R.string.big_text))
                .setBigContentTitle(getResources().getString(
                        R.string.type_text_notification_title_expanded_text))
                .setSummaryText(getResources().getString(R.string.notification_summary_text))
                .build();
	}

    private Notification getBigPictureStyle(Notification.Builder builder) {
        // In this case the icon in reduced mode will be the same as the picture
        // when expanded.
        // And when expanded, the icon will be another one.
        Bitmap large = mRandomizer.getRandomImage();
        builder.setSmallIcon(R.drawable.ic_status_bar_image)
                .setContentTitle(getResources().getString(
                        R.string.type_picture_notification_title_text))
                .setContentText(getResources().getString(
                        R.string.type_picture_notification_content_text))
                .setContentInfo(getResources().getString(R.string.notification_info_text))
                .setLargeIcon(large)
                .setColor(getResources().getColor(R.color.theme_accent));

        return new Notification.BigPictureStyle(builder)
                .bigPicture(large)
                .bigLargeIcon(large)
                .setBigContentTitle(getResources().getString(
                        R.string.type_picture_notification_title_expanded_text))
                .setSummaryText(getResources().getString(R.string.notification_summary_text))
                .build();
    }

    private Notification getInboxStyle(Notification.Builder builder) {
        Bitmap large = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        builder.setSmallIcon(R.drawable.ic_status_bar_message)
                .setContentTitle(getResources().getString(R.string.type_inbox_notification_title_text))
                .setContentText(getResources().getString(
                        R.string.type_inbox_notification_content_text))
                .setContentInfo(getResources().getString(R.string.notification_info_text))
                .setLargeIcon(large)
                .setColor(getResources().getColor(R.color.theme_accent));

        Notification.InboxStyle n = new Notification.InboxStyle(builder)
                .setBigContentTitle(getResources().getString(
                        R.string.type_inbox_notification_title_expanded_text))
                .setSummaryText(getResources().getString(R.string.notification_summary_text));

        // Add 10 lines
        for (int i = 0; i < 10; i++) {
            n.addLine(getResources().getString(
                    R.string.type_inbox_notification_line_number_text, (i + 1)));
        }

        return n.build();
    }

    private Notification getMediaStyle(Notification.Builder builder) {
        Notification.MediaStyle style = new Notification.MediaStyle()
                .setShowActionsInCompactView(1, 2, 3);
        PendingIntent intent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);

        builder.setSmallIcon(R.drawable.ic_status_bar_media)
                .setContentTitle(getResources().getString(R.string.type_media_notification_title_text))
                .setContentText(getResources().getString(R.string.type_media_notification_content_text))
                .setLargeIcon(mRandomizer.getRandomImage())
                .setStyle(style)
                .addAction(R.drawable.ic_action_fast_rewind,
                        getResources().getString(R.string.fast_rewind_title), intent)
                .addAction(R.drawable.ic_action_skip_previous,
                        getResources().getString(R.string.skip_previous_title), intent)
                .addAction(R.drawable.ic_action_play,
                        getResources().getString(R.string.play_title), intent)
                .addAction(R.drawable.ic_action_skip_next,
                        getResources().getString(R.string.skip_next_title), intent)
                .addAction(R.drawable.ic_action_fast_forward,
                        getResources().getString(R.string.fast_forward_title), intent)
                .setColor(getResources().getColor(R.color.theme_accent));
        return builder.build();
    }

    private Notification getOldNotification() {
        Notification notif = new Notification(
                mRandomizer.getRandomSmallIconId(),
                null,
                System.currentTimeMillis());

        notif.setLatestEventInfo(
                mContext,
                getResources().getString(R.string.type_old_notification_title_text),
                getResources().getString(R.string.type_old_notification_content_text),
                PendingIntent.getActivity(mContext, 0, new Intent(), 0));

        return notif;
    }

    private void setRandomButtons(Notification.Builder builder) {
        setButtons(builder, new Random().nextInt(4));
    }

    private void setButtons(Notification.Builder builder, Integer buttons) {
        // Get number of buttons
        if (buttons == null) {
            // If not specified, check the input
            buttons = 0;
            if (mEnableActionButtons.isChecked()) {
                switch (mActionButtonsGroup.getCheckedRadioButtonId()) {
                    case R.id.radio0:
                        buttons = 1;
                        break;
                    case R.id.radio1:
                        buttons = 2;
                        break;
                    case R.id.radio2:
                        buttons = 3;
                        break;
                }
            }
        }
        // Add as many buttons as you have to
        PendingIntent intent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
        for (int i = 0; i < buttons; i++) {
            builder.addAction(mRandomizer.getRandomIconId(), getResources().getString(
                    R.string.notification_action_text, (i + 1)), intent);
        }
    }
}
