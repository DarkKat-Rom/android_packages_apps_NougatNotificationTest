package net.darkkatrom.nnotiftest;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends Activity implements ActionBar.TabListener {
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private Fragment mFragType, mFragPriority;
    private int NOTIF_REF = 1;
    private NotificationManager mManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText(R.string.types_title).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.priority_title).setTabListener(this));

        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }
	
    public void sendNotification(Notification notif){
        mManager.notify(NOTIF_REF++, notif);
    }

    public void showDefaultNotification(View v) {
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, show the tab contents in the
        // container
        switch (tab.getPosition()) {
            default:
            case 0:
                if (mFragType == null) {
                    mFragType = new TypeFragment();
                }
                getFragmentManager().beginTransaction().replace(R.id.container, mFragType).commit();
                break;
            case 1:
                if (mFragPriority == null) {
                    mFragPriority = new PriorityFragment();
                }
                getFragmentManager().beginTransaction().replace(R.id.container, mFragPriority).commit();
                break;
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
}
