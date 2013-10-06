package com.codingspezis.android.metalonly.player.plan;

import android.content.*;

import com.codingspezis.android.metalonly.player.*;

public final class PlanEntryClickListener implements DialogInterface.OnClickListener {
	private final PlanData data;
	private Context context;

	public PlanEntryClickListener(PlanData data, Context context) {
		this.data = data;
		this.context = context;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case 0:
			addEntryToCalendar(data);
			break;
		case 1:
			shareEntry(data);
			break;
		}
	}

	private void addEntryToCalendar(final PlanData data) {
		Intent intent = new Intent(Intent.ACTION_EDIT);
		createAddToCalenderIntent(data, intent);
		context.startActivity(intent);
	}

	private void createAddToCalenderIntent(final PlanData data, Intent intent) {
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("title", "Metal Only");
		intent.putExtra("description", data.getDescription());
		intent.putExtra("beginTime", data.getStartTimeAsMillis());
		intent.putExtra("endTime", data.getEndTimeAsMillis());
	}

	private void shareEntry(final PlanData data) {
		String message = data.getDateString() + " " + data.getTimeString() + "\n" + data.getTitle()
				+ "\n" + data.getMod() + "\n" + data.getGenre();
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, message);
		context.startActivity(Intent.createChooser(share,
				context.getResources().getStringArray(R.array.plan_options_array)[1]));
	}

}