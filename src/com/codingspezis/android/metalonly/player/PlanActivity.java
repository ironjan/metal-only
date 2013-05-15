package com.codingspezis.android.metalonly.player;

import java.text.*;
import java.util.*;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.MenuItem;
import com.codingspezis.android.metalonly.player.plan.*;

@SuppressLint("SimpleDateFormat")
public class PlanActivity extends SherlockListActivity implements
		OnItemClickListener {

	public class EntryItem extends Item {

		public EntryItem(PlanData data) {
			this.data = data;
		}

		@Override
		public boolean isSection() {
			return false;
		}

	}

	public abstract class Item {

		public PlanData data;

		public PlanData getPlanData() {
			return data;
		}

		public abstract boolean isSection();

	}

	public class PlanData {
		private final String mod, genre, title;
		private Calendar start;
		private int duration;

		public PlanData() {
			title = "";
			mod = "";
			genre = "";
		}

		public PlanData(String mod, String title, String genre) {
			this.title = title;
			this.genre = genre;
			this.mod = mod;
		}

		public CharSequence getDateString() {
			CharSequence ret;
			if (getStart().get(Calendar.DAY_OF_WEEK) == getEnd().get(
					Calendar.DAY_OF_WEEK)
					|| getEnd().get(Calendar.HOUR_OF_DAY) == 0) {
				ret = DATE_FORMAT_DATE.format(getStart().getTime());
			} else {
				ret = DATE_FORMAT_DATE_DAY.format(getStart().getTime()) + "/"
						+ DATE_FORMAT_DATE.format(getEnd().getTime());
			}

			return ret;
		}

		public String getDescription() {
			return getTitle() + "\n" + getMod() + "\n" + getGenre() + "\n";
		}

		public int getDuration() {
			return duration;
		}

		public Calendar getEnd() {
			Calendar tmpCal = (Calendar) start.clone();
			tmpCal.add(Calendar.HOUR_OF_DAY, getDuration());
			return tmpCal;
		}

		public String getGenre() {
			return genre;
		}

		public String getMod() {
			return mod;
		}

		public int getProgress() {

			Calendar cal = new GregorianCalendar();
			float timeToEnd = getEnd().getTimeInMillis()
					- cal.getTimeInMillis();
			float durationInMillis = getDuration() * 60 * 60 * 1000;
			return (int) ((timeToEnd / durationInMillis) * 100);

		}

		public Calendar getStart() {
			return start;
		}

		public CharSequence getTimeString() {
			return DATE_FORMAT_TIME.format(start.getTime()) + " - "
					+ DATE_FORMAT_TIME.format(getEnd().getTime());
		}

		public String getTitle() {
			return title;
		}

		public boolean sameDay(PlanData d1) {
			return getStart().get(Calendar.DAY_OF_WEEK) == d1.getStart().get(
					Calendar.DAY_OF_WEEK);
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public void setStart(Calendar start) {
			this.start = start;
		}
	}

	public class SectionItem extends Item {

		private final String title;

		public SectionItem(String title) {
			this.title = title;
			data = null;
		}

		public String getTitle() {
			return title;
		}

		@Override
		public boolean isSection() {
			return true;
		}

	}

	public static final SimpleDateFormat DATE_FORMAT_PARSER = new SimpleDateFormat(
			"{dd.MM.yy HH:mm");
	public static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat(
			"HH:mm");

	public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat(
			"dd.MM.yy");

	public static final SimpleDateFormat DATE_FORMAT_DATE_DAY = new SimpleDateFormat(
			"dd");

	private ArrayList<PlanData> listEvents;

	public static final String KEY_SITE = "site";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		setTitle(getResources().getString(R.string.plan));

		String site = getIntent().getStringExtra(KEY_SITE);

		// parse site
		StringTokenizer token = new StringTokenizer(site, "}");
		// Pattern pat = Pattern.compile("{.*?}");

		// date_duration_Mod_Sendung_Genre
		String pattern = "(.*?)_(.*?)_(.*)_(.*)_(.*)";

		listEvents = new ArrayList<PlanData>();

		while (token.hasMoreTokens()) {
			String tmp = token.nextToken();
			PlanData tmpData = new PlanData();

			try {
				if (!((tmp.replaceAll(pattern, "$3").equals("MetalHead") || (tmp
						.replaceAll(pattern, "$3").equals("frei"))))) {
					GregorianCalendar tmpCal = new GregorianCalendar();
					tmpCal.setTimeInMillis(DATE_FORMAT_PARSER.parse(
							tmp.replaceAll(pattern, "$1")).getTime());
					tmpData = new PlanData(tmp.replaceAll(pattern, "$3"),
							tmp.replaceAll(pattern, "$4"), tmp.replaceAll(
									pattern, "$5"));
					tmpData.setStart(tmpCal);
					tmpData.setDuration(Integer.parseInt(tmp.replaceAll(
							pattern, "$2")));
					listEvents.add(tmpData);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ArrayList<Item> listItems = new ArrayList<PlanActivity.Item>();
		String[] days = getResources().getStringArray(R.array.days);
		int day = 0;
		listItems.add(new SectionItem(days[day]));
		int pos = 0;
		Calendar cal = new GregorianCalendar();
		for (int i = 0; i < listEvents.size(); i++) {
			PlanData d = listEvents.get(i);
			listItems.add(new EntryItem(d));
			if (i < listEvents.size() - 1) {
				if (!d.sameDay(listEvents.get(i + 1))) {
					listItems.add(new SectionItem(days[++day]));
					if ((cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 == day) {
						pos = listItems.size() - 1;
					}
				}
			}
		}
		ListView listView = getListView();
		PlanAdapter adapter = new PlanAdapter(this, listItems);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setSelection(pos);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// final int index = arg2;
		PlanAdapter adapter = (PlanAdapter) arg0.getAdapter();
		final PlanData data = ((Item) adapter.getItem(arg2)).getPlanData();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.plan_options_array,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intent = new Intent(Intent.ACTION_EDIT);
							intent.setType("vnd.android.cursor.item/event");
							intent.putExtra("title", "Metal Only");
							intent.putExtra("description",
									data.getDescription());
							intent.putExtra("beginTime", data.getStart()
									.getTimeInMillis());
							intent.putExtra("endTime", data.getEnd()
									.getTimeInMillis());
							startActivity(intent);
							break;
						case 1:
							String message = data.getDateString() + " "
									+ data.getTimeString() + "\n"
									+ data.getTitle() + "\n" + data.getMod()
									+ "\n" + data.getGenre();
							Intent share = new Intent(Intent.ACTION_SEND);
							share.setType("text/plain");
							share.putExtra(Intent.EXTRA_TEXT, message);
							startActivity(Intent.createChooser(
									share,
									getResources().getStringArray(
											R.array.plan_options_array)[1]));
							break;

						}
					}
				});
		builder.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == com.actionbarsherlock.R.id.abs__home) {
			Intent intent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, intent);
			return true;
		}
		return false;
	}
}