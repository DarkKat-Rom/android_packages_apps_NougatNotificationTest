package net.darkkatrom.nnotiftest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.darkkatrom.nnotiftest.R;

public class Randomizer {
    public static final String[] IMAGES = new String[] { "nougat.png", "io.png", "design.png",
            "play.png", "pitufina.png", "bob.png" };
    public static final int[] ICONS = new int[] { android.R.drawable.ic_menu_add,
            android.R.drawable.ic_menu_call, android.R.drawable.ic_menu_day,
            android.R.drawable.ic_menu_directions, android.R.drawable.ic_menu_edit,
            android.R.drawable.ic_menu_mylocation, android.R.drawable.ic_menu_preferences,
            android.R.drawable.ic_menu_zoom, android.R.drawable.ic_menu_delete,
            android.R.drawable.ic_menu_view, android.R.drawable.ic_menu_share };
    public static final int[] SMALL_ICONS = new int[] { R.drawable.ic_status_bar_image,
            R.drawable.ic_status_bar_note, R.drawable.ic_status_bar_text,
            R.drawable.ic_status_bar_message, R.drawable.ic_status_bar_media,
            R.drawable.ic_action_fast_rewind, R.drawable.ic_action_skip_previous,
            R.drawable.ic_action_play, R.drawable.ic_action_skip_next,
            R.drawable.ic_action_fast_forward };

    public static final Random rand = new Random();

    private List<Bitmap> mBitmaps;

    public Randomizer(Context ctx) {
        mBitmaps = new ArrayList<Bitmap>();
        // fill it, baby
        for (String name : IMAGES) {
            try {
                mBitmaps.add(BitmapFactory.decodeStream(ctx.getAssets().open(name)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getRandomImage() {
        return mBitmaps.get(rand.nextInt(mBitmaps.size()));
    }

    public int getRandomIconId() {
        return ICONS[rand.nextInt(ICONS.length)];
    }

    public int getRandomSmallIconId() {
        return SMALL_ICONS[rand.nextInt(SMALL_ICONS.length)];
    }
}
