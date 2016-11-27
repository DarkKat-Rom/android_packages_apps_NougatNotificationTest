package net.darkkatrom.nnotiftest;

import android.app.Fragment;
import android.app.Notification;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PriorityFragment extends Fragment implements View.OnClickListener {

	private MainActivity mContext;

	private Randomizer mRandomizer;

	private int count = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = (MainActivity) getActivity();

		mRandomizer = new Randomizer(mContext);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_priorities, container, false);

		Button maxPrior = (Button) v.findViewById(R.id.priority_max);
		Button highPrior = (Button) v.findViewById(R.id.priority_high);
		Button defaultPrior = (Button) v.findViewById(R.id.priority_default);
		Button lowPrior = (Button) v.findViewById(R.id.priority_low);
		Button minPrior = (Button) v.findViewById(R.id.priority_min);

		maxPrior.setOnClickListener(this);
		highPrior.setOnClickListener(this);
		defaultPrior.setOnClickListener(this);
		lowPrior.setOnClickListener(this);
		minPrior.setOnClickListener(this);
		
		return v;
	}

	@Override
	public void onClick(View v) {
		Notification.Builder builder = new Notification.Builder(mContext)
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis())
				.setContentInfo(getResources().getString(
                        R.string.priority_notification_info_text, count++))
				.setContentText(getResources().getString(
                        R.string.type_default_notification_content_text))
				.setLargeIcon(mRandomizer.getRandomImage());

		switch (v.getId()) {
		case R.id.priority_max:
			builder.setContentTitle(getResources().getString(
                    R.string.priority_max_notification_title_text))
					.setPriority(Notification.PRIORITY_MAX);
			break;
		case R.id.priority_high:
			builder.setContentTitle(getResources().getString(
                    R.string.priority_high_notification_title_text))
					.setPriority(Notification.PRIORITY_HIGH);
			break;

		case R.id.priority_low:
			builder.setContentTitle(getResources().getString(
                    R.string.priority_low_notification_title_text))
					.setPriority(Notification.PRIORITY_LOW);
			break;
		case R.id.priority_min:
			builder.setContentTitle(getResources().getString(
                    R.string.priority_min_notification_title_text))
					.setPriority(Notification.PRIORITY_MIN);
			break;
		case R.id.priority_default:
		default:
			builder.setContentTitle(getResources().getString(
                    R.string.priority_default_notification_title_text))
					.setPriority(Notification.PRIORITY_DEFAULT);
			break;
		}
		
		mContext.sendNotification(builder.build());
	}
}
