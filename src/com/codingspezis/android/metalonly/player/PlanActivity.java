package com.codingspezis.android.metalonly.player;

import java.text.*;
import java.util.*;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.MenuItem;
import com.codingspezis.android.metalonly.player.plan.*;
import com.googlecode.androidannotations.annotations.*;
import com.googlecode.androidannotations.annotations.res.*;

@EActivity
@SuppressLint("SimpleDateFormat")
public class PlanActivity extends SherlockListActivity implements OnItemClickListener {

	private static final String TAG = PlanActivity.class.getSimpleName();

	@StringRes
	String plan;

	@StringArrayRes
	String[] days;

	@Extra
	String site;
	public static final String KEY_SITE = "site";

	public static final SimpleDateFormat DATE_FORMAT_PARSER = new SimpleDateFormat(
			"{dd.MM.yy HH:mm");
	public static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm");

	public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("dd.MM.yy");

	public static final SimpleDateFormat DATE_FORMAT_DATE_DAY = new SimpleDateFormat("dd");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	@AfterInject
	void afterInject() {
		setTitle(plan);
	}

	@AfterViews
	void afterViews() {
		ArrayList<PlanData> listEvents = extractEvents(site);
		ArrayList<Item> listItems = convertToPlan(listEvents);

		PlanAdapter adapter = new PlanAdapter(this, listItems);
		getListView().setAdapter(adapter);
		getListView().setOnItemClickListener(this);

	}

	private ArrayList<Item> convertToPlan(ArrayList<PlanData> listEvents) {
		ArrayList<Item> listItems = new ArrayList<Item>();
		Calendar cal = new GregorianCalendar();

		int day = 0;

		SectionItem nextDaySection = new SectionItem(days[day]);

		listItems.add(nextDaySection);

		for (int i = 0; i < listEvents.size(); i++) {
			PlanData d = listEvents.get(i);
			listItems.add(new EntryItem(d));
			if (hasNextListItem(listEvents, i)) {
				PlanData nextItem = listEvents.get(i + 1);
				if (notOnSameDay(d, nextItem)) {
					day++;
					listItems.add(nextDaySection);
					int dayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
					if (day == dayOfWeek) {
						final int pos = listItems.size() - 1;
						getListView().setSelection(pos);
					}
				}
			}
		}
		return listItems;
	}

	private boolean notOnSameDay(PlanData d, PlanData nextItem) {
		return !d.sameDay(nextItem);
	}

	private boolean hasNextListItem(ArrayList<PlanData> listEvents, int i) {
		return i < listEvents.size() - 1;
	}

	String pattern = "(.*?)_(.*?)_(.*)_(.*)_(.*)";

	private ArrayList<PlanData> extractEvents(String site) {
		StringTokenizer tokenizer = new StringTokenizer(site, "}");

		ArrayList<PlanData> listEvents = new ArrayList<PlanData>();

		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			PlanData planData = convertTokenToPlanEntry(token);
			if (null != planData) {
				listEvents.add(planData);
			}
		}
		return listEvents;
	}

	private PlanData convertTokenToPlanEntry(String token) {
		try {
			boolean metalHeadIsMod = token.replaceAll(pattern, "$3").equals("MetalHead");
			boolean hasNoMod = token.replaceAll(pattern, "$3").equals("frei");
			boolean hasModerator = !(metalHeadIsMod || hasNoMod);
			if (hasModerator) {
				GregorianCalendar tmpCal = new GregorianCalendar();
				tmpCal.setTimeInMillis(DATE_FORMAT_PARSER.parse(token.replaceAll(pattern, "$1"))
						.getTime());
				PlanData planData = new PlanData(token.replaceAll(pattern, "$3"), token.replaceAll(
						pattern, "$4"), token.replaceAll(pattern, "$5"));
				planData.setStart(tmpCal);
				planData.setDuration(Integer.parseInt(token.replaceAll(pattern, "$2")));
				return planData;
			}
		} catch (ParseException e) {
			// drop entry with wrongly formatted date
		}

		return null;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
		PlanAdapter adapter = (PlanAdapter) adapterView.getAdapter();
		final PlanData data = ((Item) adapter.getItem(arg2)).getPlanData();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.plan_options_array, new DialogInterface.OnClickListener() {
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
		});
		builder.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, intent);
			return true;
		}
		return false;
	}

	private void addEntryToCalendar(final PlanData data) {
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("title", "Metal Only");
		intent.putExtra("description", data.getDescription());
		intent.putExtra("beginTime", data.getStart().getTimeInMillis());
		intent.putExtra("endTime", data.getEnd().getTimeInMillis());
		startActivity(intent);
	}

	private void shareEntry(final PlanData data) {
		String message = data.getDateString() + " " + data.getTimeString() + "\n"
				+ data.getTitle() + "\n" + data.getMod() + "\n" + data.getGenre();
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, message);
		startActivity(Intent.createChooser(share,
				getResources().getStringArray(R.array.plan_options_array)[1]));
	}
}